package com.aotobang.app.entity;

public class UnionPay {
	//订单号
	private String merOrderId;
	//通知连接
	private String notifyURL;
	//商户号
	private String merId;
	//商户中端号
	private String merTermId;
	public String getMerOrderId() {
		return merOrderId;
	}
	public void setMerOrderId(String merOrderId) {
		this.merOrderId = merOrderId;
	}
	public String getNotifyURL() {
		return notifyURL;
	}
	public void setNotifyURL(String notifyURL) {
		this.notifyURL = notifyURL;
	}
	public String getMerId() {
		return merId;
	}
	public void setMerId(String merId) {
		this.merId = merId;
	}
	public String getMerTermId() {
		return merTermId;
	}
	public void setMerTermId(String merTermId) {
		this.merTermId = merTermId;
	}
	
}
