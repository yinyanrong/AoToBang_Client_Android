package com.aotobang.app.entity;

public class AliPay {
	//商戶id
	private String partner;
	//商户网站唯一订单号
	private String out_trade_no;
	//下单使用的返回通知链接
	private String notifyURL;
	public String getPartner() {
		return partner;
	}
	public void setPartner(String partner) {
		this.partner = partner;
	}
	public String getOut_trade_no() {
		return out_trade_no;
	}
	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}
	public String getNotifyURL() {
		return notifyURL;
	}
	public void setNotifyURL(String notifyURL) {
		this.notifyURL = notifyURL;
	}
	
}
