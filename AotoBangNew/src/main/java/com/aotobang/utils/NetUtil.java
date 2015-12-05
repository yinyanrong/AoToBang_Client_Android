package com.aotobang.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;

import com.aotobang.app.GlobalParams;

/**
 *  获得当前手机的联网类型。如果为WAP方式，则设置代理
 * @author huanyu
 *
 */
public class NetUtil {
	/** CMWAP连接 */
	public static int OPTION_NET_CMWAP_PROXY = 1;
	/** http互联网连接 */
	public static int OPTION_NET_HTTP = 0;
	/** 当前网络连接类型 */
	public static int systemConnection = -1;
	//1.判断WIFI和MOBILE是否处于连接状态״̬
	public static boolean checkNetWork(Context context){
		boolean wifiConnection=isConnection(context,ConnectivityManager.TYPE_WIFI);
		boolean mobileConnection=isConnection(context,ConnectivityManager.TYPE_MOBILE);
		if(wifiConnection==false&&mobileConnection==false){
			//2判断有没有可用渠道。如没有，则提醒用户
		//	PromptManager.showNoNetWork(context);
			return false;
		}
	/*	if(mobileConnection==true){
			//3.如是MOBILE方式，则为其设置代理
			setAonParame(context);
		}*/
		return true;
	}
	/**
	 * 检测网络
	 */
	public static boolean checkNetwork(Activity activity,
			TelephonyManager phoneMgr) {

		// 获取当前网络
		ConnectivityManager cm = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo info = cm.getActiveNetworkInfo();

		// 没有可用网络
		if (info == null || !info.isAvailable()) {
			// isCheckNetwork = false;
			return false;
		} else {

			String typeName = info.getTypeName();
			if (typeName.equalsIgnoreCase("WIFI")) {

				NetUtil.systemConnection = NetUtil.OPTION_NET_HTTP;
				return true;
			}
			if (phoneMgr == null) {
				return false;
			}
			int networkType = phoneMgr.getNetworkType();
			String extraInfo = info.getExtraInfo();
			// Log.v("", "networkType = "+networkType);
			// 2G网络
			if (networkType == TelephonyManager.NETWORK_TYPE_GPRS
					|| networkType == TelephonyManager.NETWORK_TYPE_EDGE
					|| networkType == TelephonyManager.NETWORK_TYPE_CDMA) {

				// Utility.systemConnection = Utility.OPTION_NET_HTTP;

				if (null != extraInfo && !"".equals(extraInfo)) {

					if ("3gwap".equalsIgnoreCase(extraInfo)
							|| "cmwap".equalsIgnoreCase(extraInfo)) {

						NetUtil.systemConnection = NetUtil.OPTION_NET_CMWAP_PROXY;

					} /*
					 * else if("3gnet".equalsIgnoreCase(extraInfo)){
					 * 
					 * Utility.systemConnection = Utility.OPTION_NET_HTTP; }
					 */else {

						 NetUtil.systemConnection = -1;
					}
				} else {

					NetUtil.systemConnection = -1;
				}

			} else {

				NetUtil.systemConnection = -1;
			}
			//3G网络	
//			else if(networkType == TelephonyManager.NETWORK_TYPE_UMTS 
//					|| networkType == TelephonyManager.NETWORK_TYPE_HSDPA
//					|| networkType == TelephonyManager.NETWORK_TYPE_EVDO_0 
//					|| networkType == TelephonyManager.NETWORK_TYPE_EVDO_A){
//				
//				Utility.systemConnection = Utility.OPTION_NET_HTTP;
//			
//			//其它	
//			} else {
//				
//				Utility.systemConnection = Utility.OPTION_NET_HTTP;
//			}
			
			// 3G网络
//			else if (networkType == TelephonyManager.NETWORK_TYPE_UMTS
//					|| networkType == TelephonyManager.NETWORK_TYPE_HSDPA
//					|| networkType == TelephonyManager.NETWORK_TYPE_EVDO_0
//					|| networkType == TelephonyManager.NETWORK_TYPE_EVDO_A) {
//
//				Utility.systemConnection = Utility.OPTION_NET_HTTP;
//
//				// 其它
//			} else {
//
//				Utility.systemConnection = Utility.OPTION_NET_HTTP;
//			}

			// Log.v("", "type = "+info.getType());
			// Log.v("", "typeName = "+typeName);
			// Log.v("", "Extra = "+info.getExtraInfo());
			// Log.v("", "reason = "+info.getReason());
			// Log.v("", "subtype = "+info.getSubtype());
			// Log.v("", "subName = "+info.getSubtypeName());
			// Log.v("", "toSTRING = "+info.toString());
			return true;
		}
	}

	/**
	 * 判断WIFI和MOBILE是否处于连接状态״̬
	 * @param context
	 * @param typeMobile
	 * @return
	 */
	private static boolean isConnection(Context context, int type) {
		ConnectivityManager manager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getNetworkInfo(type);
		if(networkInfo!=null&&networkInfo.isConnected()){
			return true;
		}
		return false;
	}
	/**
	 *如是MOBILE方式，则为其设置代理
	 * @param context
	 */
	private static void setAonParame(Context context) {
		//Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");
				//使用类似于查询联系人的方法，也就是使用内容提供者查询
		ContentResolver resolver = context.getContentResolver();
		Uri uri=Uri.parse("content://telephony/carriers/preferapn");
		Cursor cursor = resolver.query(uri, null, null, null, null);
		if(cursor!=null&&cursor.moveToNext()){
			GlobalParams.PROXY_IP=cursor.getString(cursor.getColumnIndex("proxy"));
			GlobalParams.PROXY_PORT=cursor.getInt(cursor.getColumnIndex("port"));
			
		}
		cursor.close();
	}
	
}