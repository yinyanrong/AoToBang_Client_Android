package com.aotobang.net.protocal;

import java.nio.ByteOrder;
import java.nio.charset.Charset;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.aotobang.net.AbstractPacket;
import com.aotobang.net.AotoPacket;

public class AotoProtocalEncoder extends ProtocolEncoderAdapter {
	 private Charset charset;  
	  
	  public AotoProtocalEncoder(Charset charset) {  
	    this.charset=charset;  
	  }  
	  /** 
	   * 编码类 
	   */  
	@Override
	public void encode(IoSession session, Object object, ProtocolEncoderOutput out) throws Exception {
		AotoPacket value = (AotoPacket) object;     
	        IoBuffer buf = IoBuffer.allocate(value.getContentLength()+AbstractPacket.HEADER_LENGTH);   
	        buf.order(ByteOrder.LITTLE_ENDIAN);
	        buf.setAutoExpand(true); 
	        buf.putInt(value.getConnType());
	        buf.putInt(value.getPacketId());  
	        buf.putInt(value.getContentLength()); 
	        if (value.getContent() != null)     
	            buf.put(value.getBytes());    
	        buf.flip();     
	        out.write(buf);     
	}
}
