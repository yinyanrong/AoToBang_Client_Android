package com.aotobang.net.callback;

public interface IDataCallBack {
	public void handleDataResultOnSuccee(int responseId,  Object data);
	public void handleDataResultOnError(int responseId, int errCode, Object error);
	
}
