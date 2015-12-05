package com.aotobang.app.entity;

public class Friend implements Comparable<Friend>{
	private String uuid;
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	private String avatar;
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	private String name;
	@Override
	public String toString() {
		return "Friend [name=" + name + ", alpha=" + alpha + "]";
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAlpha() {
		return alpha;
	}
	public void setAlpha(String alpha) {
		this.alpha = alpha;
	}
	private String alpha;
	@Override
	public int compareTo(Friend another) {
		if(alpha.charAt(0)<another.getAlpha().charAt(0))
			return -1;
		else
			return 1;
		
	}
	
}
