package com.aotobang.net;

import com.aotobang.net.callback.IServerResponse;

public class AotoPacket extends AbstractPacket{
	
	private int packetId;
	public int getPacketId() {
		return packetId;
	}
	public void setPacketId(int packetId) {
		this.packetId = packetId;
	}
	private int contentLength;
	private String content;
	private int interfaceId;
	private IServerResponse onResponse;
	
	
	
	public IServerResponse getOnResponse() {
		return onResponse;
	}
	public void setOnResponse(IServerResponse onResponse) {
		this.onResponse = onResponse;
	}
	public int getInterfaceId() {
		return interfaceId;
	}
	public void setInterfaceId(int interfaceId) {
		this.interfaceId = interfaceId;
	}
	private int connType=1;
	public int getConnType() {
		return connType;
	}
	public void setConnType(int connType) {
		this.connType = connType;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getContentLength() {
		return content==null?0:content.getBytes().length;
	}
	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}
	@Override
	public String toString() {
		
		return "AotoPacket"+content;
	}
	@Override
	public byte[] getBytes() {
		
		return content.getBytes();
	}
	
}
