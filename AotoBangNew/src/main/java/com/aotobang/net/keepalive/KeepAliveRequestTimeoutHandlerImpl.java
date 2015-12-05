package com.aotobang.net.keepalive;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;

import com.aotobang.utils.LogUtil;


public class KeepAliveRequestTimeoutHandlerImpl implements KeepAliveRequestTimeoutHandler {

	@Override
	public void keepAliveRequestTimedOut(KeepAliveFilter filter, IoSession session) throws Exception {
		LogUtil.info(KeepAliveRequestTimeoutHandlerImpl.class, "心跳超时");
		
	}

}
