package com.aotobang.net.callback;

import com.aotobang.exception.AotoNetBreakException;

public interface ReConnectListener {
	public void  onReConnect(boolean isConnect) throws AotoNetBreakException;
}
