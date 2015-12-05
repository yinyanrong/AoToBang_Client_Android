package com.aotobang.net;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.aotobang.app.R;
import com.aotobang.app.applaction.AotoBangApp;
import com.aotobang.exception.AotoNetBreakException;
import com.aotobang.net.callback.IServerResponse;
import com.aotobang.net.entity.AotoResponse;
import com.aotobang.net.protocal.AotoProtocalCodecFactory;
import com.aotobang.utils.InterfaceUtil;
import com.aotobang.utils.LogUtil;
/**
 * 短连接类
 * @author hhj
 *
 */
public class AotoShortConnect {
	private IServerResponse  shortListener;
	private NioSocketConnector connector;
	private IoSession  session;
	private ConnectFuture cf;
	public AotoShortConnect(){
		connector = new NioSocketConnector();  
	    DefaultIoFilterChainBuilder chain=connector.getFilterChain();  
	    chain.addLast("ProtocolCodecFilter", new ProtocolCodecFilter(new AotoProtocalCodecFactory(Charset.forName("utf-8")))); 
	    connector.setHandler(new ShortClientHandler());  
		connector.setConnectTimeoutMillis(15000);  
		
		
	}
	public void connectServer(String host ,int port){
		for (;;) {  
            try {  
            	cf = connector.connect(new InetSocketAddress(host, port));
        		cf.awaitUninterruptibly();
        		session=cf.getSession();
        		if(session.isConnected()){
        			LogUtil.info(AotoShortConnect.class,"连接服务端" + host + ":" + port + "[成功]" + ",,时间:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));  
        			break;  
                
        		}
            } catch (RuntimeIoException e) {  
            	LogUtil.info(AotoShortConnect.class,"连接服务端" + host + ":" + port + "失败" + ",,时间:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + ", 连接MSG异常,请检查MSG端口、IP是否正确,MSG服务是否启动,异常内容:" + e.getMessage());  
                try {
					Thread.sleep(5000);
					// 连接失败后,重连间隔5s 
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				} 
            }  
		}
	}
	public void  sendPacket(AotoPacket packet)throws AotoNetBreakException{
		if(session.isConnected()){
			
				session.write(packet);
			
		}else{
			throw new AotoNetBreakException(AotoBangApp.mPreLoad.appContext.getString(R.string.the_current_network));
			
		}
		
		
	}
	public void close(){
			
			session.close(true);
			 connector.dispose(); 
			
			
		}
	public void setShortConnHandleListener(IServerResponse listener){
		this.shortListener=listener;
		
		
		
		
		
	}
	public class ShortClientHandler extends IoHandlerAdapter {  
		  
		  
		  @Override  
		  public void exceptionCaught(IoSession session, Throwable cause) throws Exception {  
		    LogUtil.info(AotoShortConnect.class, "客户端出现异常"+cause.getMessage()+cause.getLocalizedMessage());
		   
		    
		  }  
		  
		  @Override  
		  public void messageReceived(IoSession session, Object message) throws Exception {  
			 
				AotoPacket packet=(AotoPacket)message;
				 LogUtil.info(AotoShortConnect.class, "来自server的信息:"+session.getRemoteAddress()+"接口："+InterfaceUtil.getInterfaceName(packet.getPacketId())+message.toString());
					AotoResponse response=new AotoResponse();
					response.setResponseId(packet.getPacketId());
					response.setData(packet.getContent());
					if(shortListener!=null)
						shortListener.handleServiceResult(response);
					 LogUtil.info(AotoShortConnect.class, "回调了");	
					 close();		
			  
			  
			  
		  }  
		  
		  @Override  
		  public void messageSent(IoSession session, Object message) throws Exception {  
			  LogUtil.info(AotoShortConnect.class, "发送的信息:"+session.getRemoteAddress()+message.toString());
		  }  
		  
		  @Override  
		  public void sessionClosed(IoSession session) throws Exception {  
		    LogUtil.info(AotoShortConnect.class, "连接断开!:"+session.getRemoteAddress());
		  }  
		  
		  @Override  
		  public void sessionCreated(IoSession session) throws Exception {  
			  LogUtil.info(AotoShortConnect.class,  "建立连接!:"+session.getRemoteAddress());
			  session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);  
			  SocketSessionConfig cfg = (SocketSessionConfig) session.getConfig();   
		        cfg.setReceiveBufferSize(2 * 1024 * 1024);   
		        cfg.setReadBufferSize(2 * 1024 * 1024);   
		        cfg.setSoLinger(0); //这个是根本解决问题的设置   
		  }  
		  
		  @Override  
		  public void sessionOpened(IoSession session) throws Exception { 
			  LogUtil.info(AotoShortConnect.class,"打开连接!:"+session.getRemoteAddress());
		    
		  }  
		  
		  @Override  
		  public void sessionIdle(IoSession session, IdleStatus status) throws Exception {  
			  LogUtil.info(AotoShortConnect.class,"Client空闲!");
		  }  
		}  
}
