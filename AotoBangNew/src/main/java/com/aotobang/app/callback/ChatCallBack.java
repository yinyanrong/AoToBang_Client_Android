package com.aotobang.app.callback;

public interface ChatCallBack {
	public void onSuccess() ;
	public void onSuccess(String objectKey) ;
	public void onError(int code, String error);
	public void onProgress(int progress, int  totalSize);

}
