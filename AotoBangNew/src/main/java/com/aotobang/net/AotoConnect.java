package com.aotobang.net;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.aotobang.app.GlobalParams;
import com.aotobang.app.R;
import com.aotobang.app.activity.LoginActivity;
import com.aotobang.app.applaction.AotoBangApp;
import com.aotobang.app.model.AotoPreLoadImp;
import com.aotobang.exception.AotoNetBreakException;
import com.aotobang.net.callback.IDataCallBack;
import com.aotobang.net.callback.INTMessageCallBack;
import com.aotobang.net.callback.LongConnCallBack;
import com.aotobang.net.callback.ReConnectListener;
import com.aotobang.net.entity.AotoRequest;
import com.aotobang.net.entity.AotoResponse;
import com.aotobang.net.keepalive.KeepAliveMessageFactoryImpl;
import com.aotobang.net.keepalive.KeepAliveRequestTimeoutHandlerImpl;
import com.aotobang.net.protocal.AotoProtocalCodecFactory;
import com.aotobang.utils.InterfaceUtil;
import com.aotobang.utils.LogUtil;
/**
 * 连接类
 * @author hhj
 *
 */
public class AotoConnect {
	private ReConnectListener  reConnectListener;
	private boolean activeClose=false;
	private int loopCount;
	private boolean reConnect=false;
	private String host;
	private int port;
	private INTMessageCallBack ntListener;
	private Map<Integer,AotoPacket> packets;//用于缓存请求对象。数据得到后回调对象里的接口
	private NioSocketConnector connector;
	private IoSession  session;
	private ConnectFuture cf;
	private static AotoConnect connectInstance;
	public static AotoConnect getInstance() {
		if (connectInstance == null) {
			synchronized (AotoConnect.class) {
				if (connectInstance == null) {
					connectInstance = new AotoConnect();
				}
			}
		}

		return connectInstance;
	}
	public void setReConnect(boolean reConnect){
		this.reConnect=reConnect;
		
		
	}
	private  AotoConnect(){
		packets=Collections.synchronizedMap(new HashMap<Integer,AotoPacket>());
		connector = new NioSocketConnector();  
	    DefaultIoFilterChainBuilder chain=connector.getFilterChain();  
	    KeepAliveFilter keepAliveFilter = new KeepAliveFilter(new KeepAliveMessageFactoryImpl(),IdleStatus.WRITER_IDLE);  
        keepAliveFilter.setForwardEvent(false);  
        keepAliveFilter.setRequestInterval(15);  
        keepAliveFilter.setRequestTimeout(10);  
        keepAliveFilter.setRequestTimeoutHandler(new KeepAliveRequestTimeoutHandlerImpl());
        chain.addFirst("reconnect", new IoFilterAdapter() {  
            @Override  
            public void sessionClosed(NextFilter nextFilter, IoSession ioSession) throws Exception {  
            	reConnect=true;
                while(reConnect&&GlobalParams.netEnable){  
                	loopCount++;
                	
                    try{  
                        Thread.sleep(3000);  
                        ConnectFuture future = connector.connect(new InetSocketAddress(host, port));  
                        future.awaitUninterruptibly();// 等待连接创建成功  
                        session = future.getSession();// 获取会话  
                        if(connector.isDisposed())
                        	break;
                        if(session!=null&&session.isConnected()){
                        	loopCount=0;
                        	if(reConnectListener!=null)
                        		reConnectListener.onReConnect(true);
                        	reConnect=false;
                            break;  
                        }  
                    }catch(Exception ex){  
                    	reConnectListener.onReConnect(false);
                    	LogUtil.info(AotoConnect.class,"重连服务器登录失败,3秒再连接一次:" + ex.getMessage()+"loopCount--"+loopCount); 
                    }  
                }
            	reConnect=false;
            }

			
        });
		chain.addLast("ProtocolCodecFilter", new ProtocolCodecFilter(new AotoProtocalCodecFactory(Charset.forName("utf-8"))));  
		chain.addLast("KeepAlive", keepAliveFilter);
		connector.setHandler(new AotoClientHandler());  
		connector.setConnectTimeoutMillis(15000);  
		
		
		
		
	}
	public void reConnect(ReConnectListener listener){
		   reConnect=true;
		   while(reConnect&&GlobalParams.netEnable){  
        	
            try{  
                Thread.sleep(3000);  
                ConnectFuture future = connector.connect(new InetSocketAddress(host, port));  
                future.awaitUninterruptibly();// 等待连接创建成功  
                session = future.getSession();// 获取会话  
                if(session!=null&&session.isConnected()){
                	if(listener!=null)
                		listener.onReConnect(true);
                	reConnect=false;
                    break;  
                }  
            }catch(Exception ex){  
         		try {
					reConnectListener.onReConnect(false);
				} catch (AotoNetBreakException e) {
					e.printStackTrace();
				}
            	LogUtil.info(AotoConnect.class,"重连服务器登录失败,3秒再连接一次:" + ex.getMessage()); 
            }  
        }
	}
	public void reConnect(){
		   reConnect=true;
		   while(reConnect&&GlobalParams.netEnable){  
           	
               try{  
                   Thread.sleep(3000);  
                   ConnectFuture future = connector.connect(new InetSocketAddress(host, port));  
                   future.awaitUninterruptibly();// 等待连接创建成功  
                   session = future.getSession();// 获取会话  
                   if(session!=null&&session.isConnected()){
                   	if(reConnectListener!=null)
                   		reConnectListener.onReConnect(true);
                   	reConnect=false;
                       break;  
                   }  
               }catch(Exception ex){  
            		try {
						reConnectListener.onReConnect(false);
					} catch (AotoNetBreakException e) {
						e.printStackTrace();
					}
               	LogUtil.info(AotoConnect.class,"重连服务器登录失败,3秒再连接一次:" + ex.getMessage()); 
               }  
           }
	}
	public void connectServer(String host ,int port,LongConnCallBack callback){
		this.host=host;
		this.port=port;
		/*if(session!=null)
			return;*/
		for (;;) {  
			
            try {  
            	cf = connector.connect(new InetSocketAddress(host, port));
        		cf.awaitUninterruptibly();
        		session=cf.getSession();
        		if(session!=null&&session.isConnected()){
        			callback.onSucceed();
        			loopCount=0;
        			break;  
                
        		}
            } catch (RuntimeIoException e) {  
            	callback.onError(e.getMessage());
            	/*if(loopCount>10||activeClose){
            		close();
            		break;
            	}*/
            	loopCount++;
            	
            	LogUtil.info(AotoConnect.class,"连接服务端" + host + ":" + port + "失败" + ",,时间:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + ", 连接MSG异常,请检查MSG端口、IP是否正确,MSG服务是否启动,异常内容:" + e.getMessage());  
                try {
					Thread.sleep(5000);
					// 连接失败后,重连间隔5s 
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				} 
            }  
		}
		
		
		//}
	}
	
	public void  sendPacket(final AotoPacket packet,int requestId)throws AotoNetBreakException{
		
			if(session==null){
				for(int i=0;i<5;i++){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if(session!=null)
						break;
					
				}
				
			}
		if(session.isConnected()){
			synchronized (packets) {
				packets.put(AbstractPacket.Message_Type_ACK| requestId, packet);
				session.write(packet);
			}
			
		}else{
			if(GlobalParams.netEnable){
				reConnect(new ReConnectListener() {
					
					@Override
					public void onReConnect(boolean isConnect) throws AotoNetBreakException{
						if(isConnect)
							session.write(packet);
						else
							throw new AotoNetBreakException(AotoBangApp.mPreLoad.appContext.getString(R.string.network_anomalies));
					}
				});
				
				
			}
			//throw new AotoNetBreakException(AotoBangApp.mPreLoad.appContext.getString(R.string.network_anomalies));
		}
		
		
	}
	public void close(){
		activeClose=true;
		if(session!=null)
		session.close(true);// 等待连接断开
		LogUtil.info(AotoConnect.class, "session调用关闭");
	/*	if(connector!=null)
		connector.dispose(); 
		connectInstance=null;
		LogUtil.info(AotoConnect.class, "调用dispose");*/
		
	}
	
	public boolean isConnect(){
		if(session!=null)
		return session.isConnected();
		else 
			return false;
	}
	public void setOnNTMessageCallBacklistener(INTMessageCallBack  ntListener){
		
		this.ntListener=ntListener;
		
		
	}
public void registReConnectListener(ReConnectListener  reConnectListener){
	this.reConnectListener=reConnectListener;
	
	
}
	private void handleServerMessage(Object message){
		AotoPacket packet=(AotoPacket)message;
		switch(packet.getPacketId()){
		case AbstractPacket.Message_Type_NTF|InterfaceIds.AGREE_FRIENT_REQUEST:
		case AbstractPacket.Message_Type_NTF|InterfaceIds.CHART:
		case AbstractPacket.Message_Type_NTF|InterfaceIds.ADD_FRIENT_REQUEST:
		case AbstractPacket.Message_Type_NTF|InterfaceIds.UPLOAD_FILE_COMPLETE:
		case AbstractPacket.Message_Type_NTF|InterfaceIds.MACTH_FRIEND_LIKE:
		case AbstractPacket.Message_Type_NTF|InterfaceIds.INTERACTION_BLUETOOTH:
		case AbstractPacket.Message_Type_NTF|InterfaceIds.LOGIN:
		case AbstractPacket.Message_Type_NTF|InterfaceIds.INTERACT_APPLY:
			ntListener.handleNTMessgae(ParseDataFactory.parseNotifiResponse(packet));
			break;
		default:
			synchronized (packets) {
				AotoPacket cache=packets.get(packet.getPacketId());
				if(cache!=null){
					AotoResponse response=new AotoResponse();
					response.setResponseId(packet.getPacketId());
					response.setData(packet.getContent());
					cache.getOnResponse().handleServiceResult(response);
					packets.remove(cache);
				}else{
					LogUtil.info(AotoConnect.class, "无法找到回调对象接口："+InterfaceUtil.getInterfaceName(packet.getPacketId())+"----");
					
				}
			}
			break;
		
		
		
		
		}
	}
	public class AotoClientHandler extends IoHandlerAdapter {  
		  
		  
		  @Override  
		  public void exceptionCaught(IoSession session, Throwable cause) throws Exception {  
		    LogUtil.info(AotoConnect.class, "客户端出现异常"+cause.getMessage()+cause.getLocalizedMessage());
		    //close();
		   
		    
		  }  
		  
		  @Override  
		  public void messageReceived(IoSession session, Object message) throws Exception {  
			  LogUtil.info(AotoConnect.class, "来自server的信息:"+session.getRemoteAddress()+message.toString());
			  handleServerMessage(message);
		  }  
		  
		  @Override  
		  public void messageSent(IoSession session, Object message) throws Exception {  
			  LogUtil.info(AotoConnect.class, "发送的信息:"+session.getRemoteAddress()+message.toString());
		  }  
		  
		  @Override  
		  public void sessionClosed(IoSession session) throws Exception {  
		    LogUtil.info(AotoConnect.class, "连接断开!:"+session.getRemoteAddress());
		    	if(session!=null){
		    		session.close(true);
		    		AotoConnect.this.session=null;
		    	}
		  }  
		  
		  @Override  
		  public void sessionCreated(IoSession session) throws Exception {
			  reConnect=false;
			
			  LogUtil.info(AotoConnect.class,  "建立连接!:"+session.getRemoteAddress());
			  session.getConfig().setIdleTime(IdleStatus.WRITER_IDLE, 1);  
			  SocketSessionConfig cfg = (SocketSessionConfig) session.getConfig();   
			  	cfg.setReceiveBufferSize(2 * 1024 * 1024);   
		        cfg.setReadBufferSize(2 * 1024 * 1024);   
		        cfg.setKeepAlive(true);   
		        cfg.setSoLinger(0); 
		       
		  }  
		  
		  @Override  
		  public void sessionOpened(IoSession session) throws Exception { 
			  LogUtil.info(AotoConnect.class,"打开连接!:"+session.getRemoteAddress());
		
		    
		  }  
		  
		  @Override  
		  public void sessionIdle(IoSession session, IdleStatus status) throws Exception {  
			  LogUtil.info(AotoConnect.class,"Client空闲!");
		  }  
		}  
	
	
}
