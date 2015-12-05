package com.aotobang.net.callback;

import com.aotobang.net.entity.AotoResponse;

public interface IServerResponse {
	public void handleServiceResult(AotoResponse response);
}
