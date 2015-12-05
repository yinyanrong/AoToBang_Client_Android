package com.aotobang.net.protocal;

import java.nio.ByteOrder;
import java.nio.charset.Charset;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.aotobang.net.AbstractPacket;
import com.aotobang.net.AotoPacket;
import com.aotobang.utils.Commonutils;
import com.aotobang.utils.InterfaceUtil;
import com.aotobang.utils.LogUtil;


public class AotoProtocalDecoder extends CumulativeProtocolDecoder    {
	private Charset charset;
	public AotoProtocalDecoder(Charset charset){
		this.charset=charset;
		
	}
	
	

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		in.order(ByteOrder.LITTLE_ENDIAN);
		   if(in.remaining() > 12){//前12字节是包头
	            //标记当前position的快照标记mark，以便后继的reset操作能恢复position位置
	            in.mark(); 
	            byte[] l = new byte[12];
	            in.get(l);
	            //包体数据长度
	            int len = Commonutils.bytesToInt(l, 8);//将byte转成int
	           

	            //注意上面的get操作会导致下面的remaining()值发生变化
	            if(in.remaining() < len){
	                //如果消息内容不够，则重置恢复position位置到操作前,进入下一轮, 接收新数据，以拼凑成完整数据
	                in.reset();   
	                LogUtil.info(AotoProtocalDecoder.class, "消息内容不够：接收新数据，以拼凑成完整数据");
	                return false;
	            }else{
	            	AotoPacket packet=null;
	            	  LogUtil.info(AotoProtocalDecoder.class, "消息内容足够");
	                //消息内容足够
	                in.reset();//重置恢复position位置到操作前
	                int sumlen = 12+len;//总长 = 包头+包体
	                byte[] packArr = new byte[sumlen];
	                LogUtil.info(AotoProtocalDecoder.class, "sumlen:"+sumlen);
	                in.get(packArr, 0 , sumlen);
	                IoBuffer buffer = IoBuffer.allocate(sumlen).setAutoExpand(true);
	                buffer.order(ByteOrder.LITTLE_ENDIAN);
	                buffer.put(packArr);
	                buffer.flip();
	              
	               int connType=buffer.getInt();
	              // LogUtil.info(AotoProtocalDecoder.class, "connTypeRE:"+buffer.remaining());
		            LogUtil.info(AotoProtocalDecoder.class, "connType："+connType);
		           int  packetId=buffer.getInt();
		          // LogUtil.info(AotoProtocalDecoder.class, "packetIdRE:"+buffer.remaining());
		            LogUtil.info(AotoProtocalDecoder.class, "packetId："+InterfaceUtil.getInterfaceName(packetId));
		            //包体数据长度
		            len =buffer.getInt();//将byte转成int
		           // LogUtil.info(AotoProtocalDecoder.class, "lenRE:"+buffer.remaining());
		            LogUtil.info(AotoProtocalDecoder.class, "length："+len);
		            
		        	byte[] bodyData=new byte[len];  
		        	buffer.get(bodyData);
		    		IoBuffer bodyBuf=IoBuffer.allocate(len).setAutoExpand(true);  
		  		    bodyBuf.put(bodyData);  
		  		    bodyBuf.flip();  
		  		    packet=new AotoPacket(); 
				    packet.setConnType(connType);
				    packet.setPacketId(packetId);
				    packet.setContentLength(len);
				    packet.setContent(bodyBuf.getString(charset.newDecoder()));
				    out.write(packet);
	                buffer.free();
	            
	       		    
	                
	                if(in.remaining() > 0){//如果读取一个完整包内容后还粘了包，就让父类再调用一次，进行下一次解析
	                	  LogUtil.info(AotoProtocalDecoder.class, "读取一个完整包内容后还粘了包");
	                    return true;
	                }
	            }
	        }
	        return false;//处理成功，让父类进行接收下个包
		
	}  
	
	

}
