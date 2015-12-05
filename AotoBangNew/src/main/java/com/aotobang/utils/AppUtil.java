package com.aotobang.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class AppUtil {
	
	public static boolean isAppRunningForeground(Context context) {  
        ActivityManager activityManager = (ActivityManager) context  
                .getSystemService(Context.ACTIVITY_SERVICE);  
        List<RunningAppProcessInfo> appProcesses = activityManager  
                .getRunningAppProcesses();  
        for (RunningAppProcessInfo appProcess : appProcesses) {  
            if (appProcess.processName.equals(context.getPackageName())) {  
                /* 
                BACKGROUND=400 EMPTY=500 FOREGROUND=100 
                GONE=1000 PERCEPTIBLE=130 SERVICE=300 ISIBLE=200 
                 */  
                LogUtil.info(AppUtil.class, "此appimportace ="  
                        + appProcess.importance  
                        + ",context.getClass().getName()="  
                        + context.getClass().getName());  
                if (appProcess.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {  
                	 LogUtil.info(AppUtil.class, "处于后台"  
                            + appProcess.processName);  
                    return true;  
                } else {  
                	 LogUtil.info(AppUtil.class, "处于前台"  
                            + appProcess.processName);  
                    return false;  
                }  
            }  
        }  
        return false;  
    }  
	/** 
	 * 判断某个服务是否正在运行的方法 
	 *  
	 * @param mContext 
	 * @param serviceName 
	 *            是包名+服务的类名（例如：net.loonggg.testbackstage.TestService） 
	 * @return true代表正在运行，false代表服务没有正在运行 
	 */  
	public static boolean isServiceWork(Context mContext, String serviceName) {  
	    boolean isWork = false;  
	    ActivityManager myAM = (ActivityManager) mContext  
	            .getSystemService(Context.ACTIVITY_SERVICE);  
	    List<RunningServiceInfo> myList = myAM.getRunningServices(40);  
	    if (myList.size() <= 0) {  
	        return false;  
	    }  
	    for (int i = 0; i < myList.size(); i++) {  
	        String mName = myList.get(i).service.getClassName().toString();  
	        if (mName.equals(serviceName)) {  
	            isWork = true;  
	            break;  
	        }  
	    }  
	    return isWork;  
	}  
}
