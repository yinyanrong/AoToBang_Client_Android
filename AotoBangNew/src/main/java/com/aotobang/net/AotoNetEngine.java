package com.aotobang.net;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.aotobang.exception.AotoNetBreakException;
import com.aotobang.exception.AotoParseException;
import com.aotobang.net.callback.IServerResponse;
import com.aotobang.net.callback.LongConnCallBack;
import com.aotobang.net.entity.AotoNetEvent;
import com.aotobang.net.entity.AotoRequest;
import com.aotobang.net.entity.AotoResponse;
import com.aotobang.utils.LogUtil;

/**
 * 连接类。通过长短连接。获取数据
 * @author hhj
 *
 */
public class AotoNetEngine {
	//未联网
	public static final int NO_NET=-100;
	//捕获异常 网络连接断开
	public static final int NET_BREAK=-10;
	//捕获异常 无数据实体
	public static final int NO_BYDY_DATA=-1;
	public static boolean longConnectRunning=false;
	private ExecutorService exec ;
	private final BlockingQueue<AotoRequest> queue;
	private static AotoNetEngine engineInstance;
	private LongConnectRunnable longConnRunnable;
	private AotoNetEngine(){
	//	int cpuNums = Runtime.getRuntime().availableProcessors();
		exec=Executors.newFixedThreadPool(10); 
		queue= new ArrayBlockingQueue<AotoRequest>(500, true);
		longConnRunnable=new LongConnectRunnable();
		
		
	}
	
	public static AotoNetEngine getInstance() {
		if (engineInstance == null) {
			synchronized (AotoNetEngine.class) {
				if (engineInstance == null) {
					engineInstance = new AotoNetEngine();
				}
			}
		}

		return engineInstance;
	}
	/*@Deprecated
	public void getNetDataLong(AotoRequest request,IDataCallBack callBack){
		exec.execute(new TaskLongConnRunnable(request,callBack));
	}*/
	public void getNetDataShort(AotoRequest request,String host,int port){
		exec.execute(new TaskShortConnRunnable(request,host,port));
	}
	public void sendRequest(AotoRequest request){
		exec.execute(new  PutQueueRunnable(request));
		
	}
	public void openLongConnect(String host,int port,LongConnCallBack callBack){
		longConnRunnable.setHost(host);
		longConnRunnable.setPort(port);
		longConnRunnable.setCallBack(callBack);
		exec.execute(longConnRunnable);
	}
	private class LongConnectRunnable implements Runnable{
		private String host;
		private int port;
		private LongConnCallBack callBack;
		public LongConnCallBack getCallBack() {
			return callBack;
		}



		public void setCallBack(LongConnCallBack callBack) {
			this.callBack = callBack;
		}



		public String getHost() {
			return host;
		}



		public void setHost(String host) {
			this.host = host;
		}



		public int getPort() {
			return port;
		}



		public void setPort(int port) {
			this.port = port;
		}
		@Override
		public void run() {
			AotoConnect.getInstance().connectServer(host, port,callBack);
			longConnectRunning=true;
			while(longConnectRunning){
				AotoRequest request=nextRequest();
				if(request!=null){
					AotoPacket packet=reuqestToPacket(request);
					try {
						AotoConnect.getInstance().sendPacket(packet, request.getRequestId());
					} catch (AotoNetBreakException e) {
						LogUtil.info(AotoNetEngine.class, "长连接断线了！");
						request.getCallBack().handleDataResultOnError(AotoPacket.Message_Type_ACK|request.getRequestId(), NET_BREAK, e.getMessage());
						
					}
					
					
				}
			}
		}
	}
	private AotoPacket reuqestToPacket(final AotoRequest request){
		AotoPacket packet=new AotoPacket();
		packet.setConnType(1);
		packet.setPacketId(AbstractPacket.Message_Type_REQ|request.getRequestId());
		packet.setContent(mapConvertJson(request.getParameters()));
		packet.setOnResponse(new IServerResponse(){
			@Override
			public void handleServiceResult(AotoResponse response) {
				AotoNetEvent event=null;
				try {
					event = ParseDataFactory.parseResponse(response);
				} catch (AotoParseException e) {
					request.getCallBack().handleDataResultOnError(response.getResponseId(), NO_BYDY_DATA, e.getMessage());
				}
				if(event.getErrorCode()==0)
				request.getCallBack().handleDataResultOnSuccee(response.getResponseId(), event.getData());
				else
					request.getCallBack().handleDataResultOnError(response.getResponseId(), event.getErrorCode(), event.getData());
			}
			
			
		});
		
		
		return packet;
	}
	private AotoRequest nextRequest(){
		AotoRequest request=null;
		while((request=queue.poll())==null){
			 synchronized (queue) {
                 try {
					queue.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
             }
			
		}
		
		return request;
	}
	public void reConnectLongConnection(){
		exec.execute(new Runnable(){
			@Override
			public void run() {
				AotoConnect.getInstance().reConnect();
				
			}
			
		});
		
	}
	/**
	 * 关闭长连接
	 */
	public void closeLongConnect(){
		exec.execute(new Runnable(){
			@Override
			public void run() {
				AotoConnect.getInstance().close();
			}
			
		});
		
		longConnectRunning=false;
		engineInstance=null;
	}
	private class PutQueueRunnable implements Runnable{
		private AotoRequest request;
		public PutQueueRunnable(AotoRequest request){
			this.request=request;
		}
		@Override
		public void run() {
		
				try {
					queue.put(request);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				synchronized (queue) {
	                queue.notifyAll();
	            }
		}
	}
	
	/*private class TaskLongConnRunnable implements Runnable {
		private AotoRequest request;
		private IDataCallBack callBack;
		public TaskLongConnRunnable(AotoRequest request,IDataCallBack callBack){
			this.request=request;
			this.callBack=callBack;
		}

		@Override
		public void run() {
			AotoPacket packet=new AotoPacket();
			packet.setConnType(1);
			packet.setPacketId(AbstractPacket.Message_Type_REQ|request.getRequestId());
			packet.setContent(mapConvertJson(request.getParameters()));
			packet.setOnResponse(new IServerResponse(){
				@Override
				public void handleServiceResult(AotoResponse response) {
					AotoNetEvent event=null;
					try {
						event = ParseDataFactory.parseResponse(response);
					} catch (AotoParseException e) {
						event=new AotoNetEvent();
						event.setResponseId(response.getResponseId());
						event.setErrorCode(NO_BYDY_DATA);
						event.setData(e.getMessage());
					}
					Message msg=new Message();
					msg.arg1=event.getResponseId();
					msg.arg2=event.getErrorCode();
					msg.obj=event.getData();
					request.getUiHandler().sendMessage(msg);
					//callBack.handleDataResult(event.getResponseId(), event.getErrorCode(), event.getData());
				}
				
				
			});
			try {
				AotoConnect.getInstance().sendPacket(packet,request.getRequestId());
			} catch (AotoNetBreakException e) {
				//
				Message msg=new Message();
				msg.arg1=0;
				msg.arg2=NET_BREAK;
				msg.obj=e.getMessage();
				request.getUiHandler().sendMessage(msg);
			}
			
			
		}
	}*/
	private class TaskShortConnRunnable implements Runnable {
		private AotoRequest request;
		private String host;
		private int port;
		public TaskShortConnRunnable(AotoRequest request,String host,int port){
			this.request=request;
			this.host=host;
			this.port=port;
		}

		@Override
		public void run() {
			AotoShortConnect shortConn=new AotoShortConnect();
			shortConn.connectServer(host, port);
			AotoPacket packet=new AotoPacket();
			packet.setConnType(0);
			packet.setPacketId(AbstractPacket.Message_Type_REQ|request.getRequestId());
			packet.setContent(mapConvertJson(request.getParameters()));
			shortConn.setShortConnHandleListener(new IServerResponse(){
				@Override
				public void handleServiceResult(AotoResponse response) {
					AotoNetEvent event=null;
					try {
						event = ParseDataFactory.parseResponse(response);
					} catch (AotoParseException e) {
						request.getCallBack().handleDataResultOnError(response.getResponseId(), NO_BYDY_DATA, e.getMessage());
					}
					if(event.getErrorCode()==0)
						request.getCallBack().handleDataResultOnSuccee(response.getResponseId(), event.getData());
					else
						request.getCallBack().handleDataResultOnError(response.getResponseId(), event.getErrorCode(), event.getData());
				}
				
				
			});
			try {
				shortConn.sendPacket(packet);
			} catch (AotoNetBreakException e) {
				request.getCallBack().handleDataResultOnError(AotoPacket.Message_Type_ACK|request.getRequestId(), NET_BREAK, e.getMessage());
			}
			
			
		}
	}
	private String mapConvertJson(Map<String,Object> map){

		
		return JSON.toJSON(map).toString();
	}
	
}
