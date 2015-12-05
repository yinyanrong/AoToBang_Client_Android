package com.aotobang.bluetooth.bean;

public class Vibrate {
	public Vibrate(int rate,long time){
		this.rate=rate;
		this.time=time;
		
		
	}
	public int getRate() {
		return rate;
	}
	public void setRate(int rate) {
		this.rate = rate;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	private int rate;
	private long time;

}
