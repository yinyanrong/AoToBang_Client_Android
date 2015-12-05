package com.aotobang.app.activity;

import static android.content.Intent.ACTION_MAIN;

import java.util.HashMap;
import java.util.Map;

import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.PayloadType;

import android.content.Intent;
import android.os.Handler;
import android.view.View;

import com.aotobang.annotation.BaseActivity;
import com.aotobang.annotation.ContentView;
import com.aotobang.app.R;
import com.aotobang.app.applaction.AotoBangApp;
import com.aotobang.app.manager.ChatManager;
import com.aotobang.app.model.DefaultModelImp;
import com.aotobang.app.phone.LinphoneManager;
import com.aotobang.app.phone.LinphonePreferences;
import com.aotobang.app.phone.LinphoneService;
import com.aotobang.net.AotoPacket;
import com.aotobang.net.InterfaceIds;
import com.aotobang.net.entity.AotoRequest;
import com.aotobang.utils.LogUtil;
@ContentView(R.layout.activity_launch)
public class LaunchActivity extends BaseActivity {
	private Handler mHandler=new Handler();
	private ServiceWaitThread mThread;
	@Override
	public void onClick(View v) {
	}

	@Override
	public void handleDataResultOnSuccee(int responseId, Object data) {
		switch(responseId){
		case AotoPacket.Message_Type_ACK|InterfaceIds.LOGIN:
			startActivity(new Intent(LaunchActivity.this, HomeActivity.class));
			finish();
			break;
		
		}
	}

	@Override
	public void initView() {
				
				if (LinphoneService.isReady()) {
					onServiceReady();
				} else {
					// start linphone as background  
					startService(new Intent(ACTION_MAIN).setClass(LaunchActivity.this, LinphoneService.class));
					mThread = new ServiceWaitThread();
					mThread.start();
				}
		
		
	}
	private void onServiceReady() {
		LinphonePreferences.instance().useRandomPort(true);
		if(!sp.getBoolean(DefaultModelImp.PREF_INIT_AUDIO_CONFIG, false)){
			LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
			for( PayloadType pt : lc.getAudioCodecs()){
				try {
				if(pt.getMime().equals("G729")||pt.getMime().equals("iLBC")||pt.getMime().equals("PCMU")||pt.getMime().equals("PCMA"))
						LinphoneManager.getLcIfManagerNotDestroyedOrNull().enablePayloadType(pt, true);
				else if(pt.getMime().equals("SILK")&&pt.getRate()==8000)
					LinphoneManager.getLcIfManagerNotDestroyedOrNull().enablePayloadType(pt, true);
				else 
					LinphoneManager.getLcIfManagerNotDestroyedOrNull().enablePayloadType(pt, false);
				
					} catch (LinphoneCoreException e) {
						e.printStackTrace();
					}
			}
			sp.edit().putBoolean(DefaultModelImp.PREF_INIT_AUDIO_CONFIG, true).commit();
			
		}
		
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				
				if(AotoBangApp.getInstance().getUserId()!=null){
					LogUtil.info(LaunchActivity.class, AotoBangApp.getInstance().getUserId());
					 ChatManager.getInstance().init();
					 login();
				}else{
					Intent intent=new Intent();
					intent.setClass(LaunchActivity.this, MainActivity.class);
					startActivity(intent);
					finish();
				
				}
			}
		},1000);
		
	}
	private class ServiceWaitThread extends Thread {
		public void run() {
			while (!LinphoneService.isReady()) {
				try {
					sleep(30);
				} catch (InterruptedException e) {
					throw new RuntimeException("waiting thread sleep() has been interrupted");
				}
			}

			mHandler.post(new Runnable() {
				@Override
				public void run() {
					onServiceReady();
				}
			});
			mThread = null;
		}
	}
	private void login(){
		 AotoRequest request=new AotoRequest();
   		request.setRequestId(InterfaceIds.LOGIN);
   		Map<String,Object> map=new HashMap<String, Object>();
   		map.put("uuid", AotoBangApp.getInstance().getUserId());
   		map.put("sessionId", AotoBangApp.getInstance().getSessionId());
 		map.put("state", 0);
 		map.put("type", 0);
		map.put("mode", 0);
		map.put("macAddress",  AotoBangApp.getInstance().getLocalMacAddress());
   		request.setParameters(map);
   		request.setCallBack(LaunchActivity.this);
         sendRequest(request); 
	}
}
