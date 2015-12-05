package com.aotobang.net.entity;


public class AotoNetEvent {
private	int responseId;
private int errorCode;
private Object data;
public Object getData() {
	return data;
}
public void setData(Object data) {
	this.data = data;
}
public int getResponseId() {
	return responseId;
}
public void setResponseId(int responseId) {
	this.responseId = responseId;
}
public int getErrorCode() {
	return errorCode;
}
public void setErrorCode(int errorCode) {
	this.errorCode = errorCode;
}

	
	
}
