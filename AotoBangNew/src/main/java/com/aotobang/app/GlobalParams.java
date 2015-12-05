package com.aotobang.app;

import org.linphone.core.LinphoneCall;

import com.aotobang.app.activity.HomeActivity;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;

public class GlobalParams {
	public static boolean  netEnable=false;
	/**
	 * 代理的ip   
	 */
	public static String PROXY_IP;
	/**
	 * 代理的端口号  
	 */
	public static int PROXY_PORT;
	/**
	 * 长连接host
	 */
	public static String LONG_CONNECT_HOST="123.56.119.217";
	/**
	 * 长连接port
	 */
	public static int LONG_CONNECT_PORT=7789;
	/**
	 * sessionId
	 */
	//public static String LONG_CONNECT_SESSIONID;
	/**
	 * username
	 */
	//public static String userName;
//	public static String userId="unkown";
	//public static Context applicationContext;
	public static LinphoneCall mCall;
	public static HomeActivity homeActivity;
	public static String userIcon;
	
	public static Activity mainActivity;
	/**
	 * cachedir
	 */
	//public static String CACHE_PATH;
	//public static String CACHE_IMG;
	//public static String CACHE_AUDIO;
	//public static String CACHE_VIDEO;
	//public static String CACHE_FILE;
}
