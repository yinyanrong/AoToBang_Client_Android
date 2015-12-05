package com.aotobang.app.service;




import java.util.HashMap;
import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.aotobang.app.GlobalParams;
import com.aotobang.app.R;
import com.aotobang.app.applaction.AotoBangApp;
import com.aotobang.app.model.AotoPreLoadImp;
import com.aotobang.app.receiver.NetReceiver;
import com.aotobang.net.AotoConnect;
import com.aotobang.net.AotoNetEngineProxy;
import com.aotobang.net.InterfaceIds;
import com.aotobang.net.callback.IDataCallBack;
import com.aotobang.net.callback.LongConnCallBack;
import com.aotobang.net.callback.ReConnectListener;
import com.aotobang.net.entity.AotoRequest;
import com.aotobang.utils.LogUtil;
import android.os.HandlerThread;


public class AotoNetService extends Service {
	private NetReceiver receiver;
	private final IBinder localBinder = new LocalBinder();
	private  Handler uiHandler=new Handler();
	public class LocalBinder extends Binder{
	private HandlerThread mHandlerThread = new HandlerThread("HandlerThread");

		/**
		 * 获取Service实例
		 * 
		 * @return SystemBasicLocalDataService实例
		 */
		public AotoNetService getService(){
			
			return AotoNetService.this;
		}
		
	}
	@Override
	public void onCreate() {
	//registerReceiver();
		super.onCreate();
		
		
		
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(AotoConnect.getInstance().isConnect()){
			LogUtil.info(AotoNetService.class, "已经开启过服务");
			return START_STICKY;
		}
		else{
		AotoNetEngineProxy.getInstance(AotoNetService.this).openLongConnect(GlobalParams.LONG_CONNECT_HOST, GlobalParams.LONG_CONNECT_PORT,new LongConnCallBack() {
			
			@Override
			public void onSucceed() {
				registerReceiver();
				AotoConnect.getInstance().registReConnectListener(new ReConnectListener() {
					
					@Override
					public void onReConnect(boolean isConnect) {
						if(isConnect)
						reLogin();
						else 
							LogUtil.info(AotoNetService.class, "重连失败");
							
						
					}
				});
				
			}
			
			@Override
			public void onError(String errorMsg) {
				uiHandler.post(new Runnable() {
					
					@Override
					public void run() {
						LogUtil.info(AotoNetService.class, getResources().getString(R.string.the_current_network));
					}
				});
			
				
				
			}
		});
		}
		return START_STICKY;
	}
	public void sendRequest(AotoRequest request){
		
		AotoNetEngineProxy.getInstance(getApplicationContext()).sendReqeust(request);
	}
	@Override
	public IBinder onBind(Intent intent) {
		
		return localBinder;
	}
	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
	    stopForeground(true);
	    Intent intent = new Intent(getApplicationContext(), 
                AotoNetService.class); 
       startService(intent);
		super.onDestroy();
	}
	private  void registerReceiver(){
        IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver=new NetReceiver();
        this.registerReceiver(receiver, filter);
    }
	private void reLogin() {
	    AotoRequest request=new AotoRequest();
  		request.setRequestId(InterfaceIds.LOGIN);
  		Map<String,Object> map=new HashMap<String, Object>();
  		map.put("uuid", AotoBangApp.getInstance().getUserId());
  		map.put("sessionId", AotoBangApp.getInstance().getSessionId());
		map.put("state", 0);
		map.put("type", 0);
		map.put("mode", 1);
		map.put("macAddress",  AotoBangApp.getInstance().getLocalMacAddress());
  		request.setParameters(map);
  		request.setCallBack(new IDataCallBack() {
			
			@Override
			public void handleDataResultOnSuccee(int responseId, Object data) {
				 LogUtil.info(AotoNetService.class,"重新登录成功");
			}
			
			@Override
			public void handleDataResultOnError(int responseId, int errCode, Object error) {
			}
		});
  		AotoNetEngineProxy.getInstance(AotoPreLoadImp.getInstance().appContext).sendReqeust(request);
	}
}
