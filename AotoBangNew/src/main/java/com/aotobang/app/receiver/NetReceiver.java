package com.aotobang.app.receiver;

import static android.content.Intent.ACTION_MAIN;

import java.util.HashMap;
import java.util.Map;

import com.aotobang.app.ConstantValues;
import com.aotobang.app.GlobalParams;
import com.aotobang.app.activity.LaunchActivity;
import com.aotobang.app.applaction.AotoBangApp;
import com.aotobang.app.model.AotoPreLoadImp;
import com.aotobang.app.phone.LinphoneService;
import com.aotobang.app.service.AotoNetService;
import com.aotobang.net.AotoConnect;
import com.aotobang.net.AotoNetEngine;
import com.aotobang.net.AotoNetEngineProxy;
import com.aotobang.net.InterfaceIds;
import com.aotobang.net.callback.IDataCallBack;
import com.aotobang.net.callback.LongConnCallBack;
import com.aotobang.net.entity.AotoRequest;
import com.aotobang.utils.AppUtil;
import com.aotobang.utils.LogUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetReceiver extends BroadcastReceiver {
    public static int lastType = -2;
    
	@Override
	public void onReceive(Context context, Intent intent) {
	     ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	     NetworkInfo info = connectivityManager.getActiveNetworkInfo();
	     if (info == null || !connectivityManager.getBackgroundDataSetting()) {
	    	 GlobalParams.netEnable=false;
	    	 lastType = -1;
             LogUtil.info(NetReceiver.class,"您的网络连接已中断");
            AotoConnect.getInstance().setReConnect(false);
     } else {
             int netType = info.getType();
             GlobalParams.netEnable=true;
             if (netType != lastType) {
                     if (info.isConnected()) {
                             //delay 5seconds
                    	 LogUtil.info(NetReceiver.class,"new connection was create.........type:" + info.getTypeName() + " status"
                                             + info.getDetailedState());
                    	 if(lastType==-1){
                    		 AotoNetEngine.getInstance().reConnectLongConnection();
                    		 if(!AppUtil.isServiceWork(context, LinphoneService.class.getName())){
                    			  LogUtil.info(NetReceiver.class,"linphone服务重启");
                    			 context.startService(new Intent(ACTION_MAIN).setClass(context, LinphoneService.class));
                    			 
                    		 }
                    		 
                    		 
                    		 
                    	 }
                    		
                     } else {
                             //delay 5seconds
                    	 LogUtil.info(NetReceiver.class,"the connection was broken...........type:" + info.getTypeName() + " status"
                                             + info.getDetailedState());
                     }
                     lastType = netType;
             }

     }  
	        /*if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
	          // Toast.makeText(context, "不可用", 0).show();
	        	AotoNetEngine.getInstance().closeLongConnect();
	            //改变背景或者 处理网络的全局变量
	        }else {
	            //改变背景或者 处理网络的全局变量
	        	AotoNetEngine.getInstance().openLongConnect(ConstantValues.LONG_CONNECT_HOST, ConstantValues.LONG_CONNECT_PORT, new LongConnCallBack() {
					
					@Override
					public void onSucceed() {
						reLogin();
					}
					
					@Override
					public void onError(String errorMsg) {
					}
				});
	        	  // Toast.makeText(context, "可用", 0).show();
	        }*/
		
		
	}
}
