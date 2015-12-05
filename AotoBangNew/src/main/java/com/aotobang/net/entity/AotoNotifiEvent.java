package com.aotobang.net.entity;

import com.aotobang.app.entity.Interact;
import com.aotobang.app.entity.LikeEntity;


public class AotoNotifiEvent {
	private Interact  interact;
	public Interact getInteract() {
		return interact;
	}
	public void setInteract(Interact interact) {
		this.interact = interact;
	}
	private LikeEntity like;
	public LikeEntity getLike() {
		return like;
	}
	public void setLike(LikeEntity like) {
		this.like = like;
	}
	private Type type;
	private  int reponseId;
	private Object Data;
	private String from;
	private String to;
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	private int voice_time;
	public int getVoice_time() {
		return voice_time;
	}
	public void setVoice_time(int voice_time) {
		this.voice_time = voice_time;
	}
	private int chat_type;
	public int getChat_type() {
		return chat_type;
	}
	public void setChat_type(int chat_type) {
		this.chat_type = chat_type;
	}
	
	public int getReponseId() {
		return reponseId;
	}
	public void setReponseId(int reponseId) {
		this.reponseId = reponseId;
	}
	public Object getData() {
		return Data;
	}
	public void setData(Object data) {
		Data = data;
	}
	
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	
	 public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public static enum Type {
	    	CHAT,
	    	REMOTE_LOGIN,
	    	INTERACT,
	    	MATCH_LIKE,
	    	COMMON,
	    	Interact_Apply;
	    	
	    }
}
