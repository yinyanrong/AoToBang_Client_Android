package com.aotobang.app.applaction;

import java.util.Map;
import java.util.UUID;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;

import com.aotobang.app.model.AotoPreLoadImp;
import com.aotobang.app.model.DefaultModelImp.PathType;
import com.aotobang.app.service.AotoNetService;
import com.aotobang.local.bean.LocalFriend;
import com.aotobang.utils.AotoPreferenceUtils;
import com.aotobang.utils.LogUtil;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class AotoBangApp extends Application {
	public static final String REMOTE_LOGIN_ACTION="com.aotobang.remote.login";
	private static AotoBangApp instance;
	public static AotoPreLoadImp mPreLoad = new AotoPreLoadImp();


	@Override
	public void onCreate() {
		super.onCreate();
		CrashHandler crashHandler = CrashHandler.getInstance();
        // 注册crashHandler  
        crashHandler.init(getApplicationContext());
		 instance = this;
		 mPreLoad.onInit(getApplicationContext());
		 Intent intent = new Intent(getApplicationContext(), 
                 AotoNetService.class); 
        startService(intent);
        LogUtil.info(AotoBangApp.class, "AotoBangApp创建");
		// initImageLoader();
	}
	public  void initCachePath(){
		//GlobalParams.CACHE_PATH=getApplicationContext().getExternalFilesDir(mPreLoad.getId()).getAbsolutePath()+"/";
		mPreLoad.setCachePath(PathType.Img, getApplicationContext().getExternalFilesDir(mPreLoad.getId()).getAbsolutePath()+"/"+"image/");
		mPreLoad.setCachePath(PathType.Audio, getApplicationContext().getExternalFilesDir(mPreLoad.getId()).getAbsolutePath()+"/"+"audio/");
		mPreLoad.setCachePath(PathType.Video, getApplicationContext().getExternalFilesDir(mPreLoad.getId()).getAbsolutePath() + "/" + "video/");
		mPreLoad.setCachePath(PathType.File, getApplicationContext().getExternalFilesDir(mPreLoad.getId()).getAbsolutePath() + "/" + "file/");
		mPreLoad.setCachePath(PathType.Photo, getApplicationContext().getExternalFilesDir(mPreLoad.getId()).getAbsolutePath() + "/" + "photo/");
		
		
	}
	private void initImageLoader() {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
		.threadPriority(Thread.NORM_PRIORITY - 2)
		.denyCacheImageMultipleSizesInMemory()
		.discCacheFileNameGenerator(new Md5FileNameGenerator())
		.tasksProcessingOrder(QueueProcessingType.LIFO)
		.writeDebugLogs() // Remove for release app
		.build();
// Initialize ImageLoader with configuration.
//ImageLoader.getInstance().init(config);
		
		
	}
	public static AotoBangApp getInstance() {
		return instance;
	}
	/**
	 * 获取内存中好友user list
	 *
	 * @return
	 */
	public Map<String, LocalFriend> getUserList() {
	    return mPreLoad.getContactList();
	}

	/**
	 * 设置好友user list到内存中
	 *
	 * @param contactList
	 */
	public void setUsertList(Map<String, LocalFriend> contactList) {
		mPreLoad.setContactList(contactList);
	}
	public String getCachePath(PathType type){
		
		return mPreLoad.getCachePath(type);
	}
	/**
	 * 获取当前登陆用户名
	 *
	 * @return
	 */
	public String getUserName() {
	    return mPreLoad.getUserName();
	}
	/**
	 * 设置用户名
	 *
	 * @param user
	 */
	public void setUserName(String username) {
		mPreLoad.setUserName(username);
	}
	public void setUserId(String id){
		
		mPreLoad.setId(id);
		
	}
public String getUserId(){
		
		
		return mPreLoad.getId();
	}
	public void setUserNick(String userNick) {
		mPreLoad.setUserNick(userNick);
	}
	public String getUserNick(){
		return mPreLoad.getUserNick();
	}
	
	/**
	 * 获取密码
	 *
	 * @return
	 */
	public String getPassword() {
		return mPreLoad.getPassword();
	}
	
	/**
	 * 
	 * 内部的自动登录需要的密码，已经加密存储了
	 *
	 * @param pwd
	 */
	public void setPassword(String pwd) {
		mPreLoad.setPassword(pwd);
	}
	public void setSessionId(String sessionId){
		
		mPreLoad.setSessionId(sessionId);
	}
	public String getSessionId(){
		
		return mPreLoad.getSessionId();
	}


	public void setHavePhoto(boolean have){
		mPreLoad.setHavePhoto(have);


	}
	public boolean isHavePhoto(){
		return mPreLoad.isHavePhoto();
	}
	/**
	 * 退出登录,清空数据
	 */
	public void logOut(){
		LogUtil.info(AotoBangApp.class, "清空数据");
	//	AotoPreferenceUtils.getInstance().clear();
		PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().clear().commit();
		
	}
	public    String getLocalMacAddress() {
		WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		String MAC=info.getMacAddress();
		//如果无法获取设备的MAC地址，使用UUID生成一个唯一的标示
		if(MAC==null||MAC.equals(""))
			MAC= UUID.randomUUID().toString();
		if(MAC.length()>32)
			MAC=(String) MAC.subSequence(0, 32);
		return MAC;
	}
	public static void startLogin(Context context){
		//销毁数据 通知 用户 连接 服务
	}
	public static void exitLogin(Context context) {
//		Intent intent = new Intent(context, LoginActivity.class);
//		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		context.startActivity(intent);
	}
}
