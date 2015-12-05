package com.aotobang.app.manager;

import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneAddress.TransportType;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreFactory;

import android.content.Context;
import android.text.TextUtils;

import com.aotobang.app.R;
import com.aotobang.app.phone.LinphoneManager;
import com.aotobang.app.phone.LinphonePreferences;
import com.aotobang.app.phone.LinphonePreferences.AccountBuilder;
import com.aotobang.net.AotoConnect;
import com.aotobang.utils.LogUtil;


public class PhoneManager {
	private LinphoneAddress address;
	private LinphonePreferences mPrefs;
	private static PhoneManager instance;
	private PhoneManager(){
		 mPrefs = LinphonePreferences.instance();
	}
	public static PhoneManager getPhoneManager(){
		if(instance==null){
			synchronized (AotoConnect.class) {
				if (instance == null) {
					instance = new PhoneManager();
				}
			}
			
		}
		return instance;
		
	}
	public  void storeAccount(String username, String password, String domain,Context context){
		LogUtil.info(PhoneManager.class, mPrefs.getAccountCount()+"---");
		if(mPrefs.getAccountCount()!=0){
			mPrefs.setAccountUsername(0, username);
			mPrefs.setAccountPassword(0, password);
			return;
		}else{
		String identity = "sip:" + username + "@" + domain;
		try {
			address = LinphoneCoreFactory.instance().createLinphoneAddress(identity);
		} catch (LinphoneCoreException e) {
			e.printStackTrace();
		}
		boolean isMainAccountLinphoneDotOrg = domain.equals(context.getString(R.string.default_domain));
		boolean useLinphoneDotOrgCustomPorts = context.getResources().getBoolean(R.bool.use_linphone_server_ports);
		AccountBuilder builder = new AccountBuilder(LinphoneManager.getLc())
		.setUsername(username)
		.setDomain(domain)
		.setPassword(password);
		
		if (isMainAccountLinphoneDotOrg && useLinphoneDotOrgCustomPorts) {
			if (context.getResources().getBoolean(R.bool.disable_all_security_features_for_markets)) {
				builder.setProxy(domain + ":5228")
				.setTransport(TransportType.LinphoneTransportTcp);
			}
			else {
				builder.setProxy(domain + ":5223")
				.setTransport(TransportType.LinphoneTransportTls);
			}
			
			builder.setExpires("604800")
			.setOutboundProxyEnabled(true)
			.setAvpfEnabled(true)
			.setAvpfRRInterval(3)
			.setQualityReportingCollector("sip:voip-metrics@sip.linphone.org")
			.setQualityReportingEnabled(true)
			.setQualityReportingInterval(180)
			.setRealm("sip.linphone.org");
			
			
			mPrefs.setStunServer(context.getString(R.string.default_stun));
			mPrefs.setIceEnabled(true);
		} else {
			String forcedProxy =context. getResources().getString(R.string.setup_forced_proxy);
			if (!TextUtils.isEmpty(forcedProxy)) {
				builder.setProxy(forcedProxy)
				.setOutboundProxyEnabled(true)
				.setAvpfRRInterval(5);
			}
		}
		
		if (context.getResources().getBoolean(R.bool.enable_push_id)) {
			String regId = mPrefs.getPushNotificationRegistrationID();
			String appId = context.getString(R.string.push_sender_id);
			if (regId != null && mPrefs.isPushNotificationEnabled()) {
				String contactInfos = "app-id=" + appId + ";pn-type=google;pn-tok=" + regId;
				builder.setContactParameters(contactInfos);
			}
		}
		
		try {
			builder.saveNewAccount();
			//accountCreated = true;
		} catch (LinphoneCoreException e) {
			e.printStackTrace();
		}
		
		}
		
	}
	private void  checkAccount() {
		if(LinphoneManager.getLc().getProxyConfigList().length==0){
			
		}else{
			
			
			
		}
		
		
	}
}
