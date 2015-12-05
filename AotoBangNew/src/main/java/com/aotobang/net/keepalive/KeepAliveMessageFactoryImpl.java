package com.aotobang.net.keepalive;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.json.JSONException;
import org.json.JSONObject;

import com.aotobang.net.AbstractPacket;
import com.aotobang.net.AotoPacket;
import com.aotobang.net.InterfaceIds;

public class KeepAliveMessageFactoryImpl implements KeepAliveMessageFactory {
	

	@Override
	public Object getRequest(IoSession session) {
		AotoPacket heart=new AotoPacket();
		heart.setConnType(1);
		heart.setPacketId(AbstractPacket.Message_Type_REQ|InterfaceIds.HEART_BEAT);
		JSONObject  job=new JSONObject();
		try {
			job.put("state", 1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		heart.setContent(job.toString());
		return heart;
	}

	@Override
	public Object getResponse(IoSession session, Object message) {
		
		
		

		return null;
	}

	@Override
	public boolean isRequest(IoSession session, Object message) {
		AotoPacket  p=(AotoPacket)message;
		
		return (AbstractPacket.Message_Type_REQ|InterfaceIds.HEART_BEAT)==p.getPacketId();
	}

	@Override
	public boolean isResponse(IoSession session, Object message) {
		AotoPacket  p=(AotoPacket)message;
		return (AbstractPacket.Message_Type_ACK|InterfaceIds.HEART_BEAT)==p.getPacketId();
	}

}
