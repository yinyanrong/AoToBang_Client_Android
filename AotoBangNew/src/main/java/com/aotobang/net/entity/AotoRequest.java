package com.aotobang.net.entity;

import java.util.Map;


import com.aotobang.net.callback.IDataCallBack;

public class AotoRequest {
	
	private IDataCallBack callBack;
	
	public IDataCallBack getCallBack() {
		return callBack;
	}
	public void setCallBack(IDataCallBack callBack) {
		this.callBack = callBack;
	}
	private Map<String,Object> parameters;
	public Map<String, Object> getParameters() {
		return parameters;
	}
	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}
	public int getRequestId() {
		return requestId;
	}
	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}
	private int requestId;
	public static AotoRequest createRequest(int requestId){
		AotoRequest request=new AotoRequest();
		request.setRequestId(requestId);
		return request;
		
	}
}
