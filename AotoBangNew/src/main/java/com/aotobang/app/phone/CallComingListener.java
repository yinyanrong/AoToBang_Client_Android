package com.aotobang.app.phone;

import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCore;

public interface CallComingListener {
	public void onCallComing(LinphoneCore lc, LinphoneCall call, LinphoneCall.State state, String message);
}
