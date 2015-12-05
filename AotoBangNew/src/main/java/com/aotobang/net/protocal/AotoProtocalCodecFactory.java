package com.aotobang.net.protocal;

import java.nio.charset.Charset;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class AotoProtocalCodecFactory implements ProtocolCodecFactory{
	private final AotoProtocalEncoder encoder;     
    private final AotoProtocalDecoder decoder;     
    public AotoProtocalCodecFactory(Charset charset){
    	
    	  this.decoder=new AotoProtocalDecoder(charset);  
    	  this.encoder=new AotoProtocalEncoder(charset);  
    	
    }
	
	
	

	@Override
	public ProtocolDecoder getDecoder(IoSession paramIoSession) throws Exception {
		
		return decoder;
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession paramIoSession) throws Exception {
		
		return encoder;
	}

}
