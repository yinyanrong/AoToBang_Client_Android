package com.aotobang.app.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.aotobang.utils.AotoPreferenceUtils;
public class DefaultModelImp extends AotoModel {
	private static final String PREF_USERID = "userid";
	private static final String PREF_USERNAME = "username";
	private static final String PREF_USERNICK = "nick";
    private static final String PREF_PWD = "pwd";
    public static final String PREF_INIT_AUDIO_CONFIG = "audioConfig";
    private static final String PREF_SESSION_ID = "sessionId";
	private static final String PREF_HAVE_PHOTO = "havePhoto";
    public static final String PREF_SHOW_CHAT_TOAST = "showChatToast";
    private static final String PREF_PATH_IMG = "imgPath";
    private static final String PREF_PATH_PHOTO = "photoPath";
    private static final String PREF__PATH_AUDIO = "audioPath";
    private static final String PREF_PATH_FILE = "filePath";
    private static final String PREF_PATH_VIDEO = "vodiePath";
    
    protected Context context = null;
    protected Map<Key,Object> valueCache = new HashMap<Key,Object>();
    public DefaultModelImp(Context ctx){
        context = ctx;
        AotoPreferenceUtils.init(context);
    }
	/**
	 * 是否接受消息通知
	 */
	@Override
	public void setSettingMsgNotification(boolean paramBoolean) {
		AotoPreferenceUtils.getInstance().setSettingMsgNotification(paramBoolean);
	        valueCache.put(Key.VibrateAndPlayToneOn, paramBoolean);
	}

	@Override
	public boolean getSettingMsgNotification() {

		 Object val = valueCache.get(Key.VibrateAndPlayToneOn);

	        if(val == null){
	            val = AotoPreferenceUtils.getInstance().getSettingMsgNotification();
	            valueCache.put(Key.VibrateAndPlayToneOn, val);
	        }
	       
	        return (Boolean) (val != null?val:true);
	}
	/**
	 * 是否开启声音提醒
	 */
	@Override
	public void setSettingMsgSound(boolean paramBoolean) {
		AotoPreferenceUtils.getInstance().setSettingMsgSound(paramBoolean);
	        valueCache.put(Key.PlayToneOn, paramBoolean);
	}

	@Override
	public boolean getSettingMsgSound() {
		Object val = valueCache.get(Key.PlayToneOn);

        if(val == null){
            val = AotoPreferenceUtils.getInstance().getSettingMsgSound();
            valueCache.put(Key.PlayToneOn, val);
        }
       
        return (Boolean) (val != null?val:true);
	}
	/**
	 * 是否开启震动
	 */
	@Override
	public void setSettingMsgVibrate(boolean paramBoolean) {
		AotoPreferenceUtils.getInstance().setSettingMsgVibrate(paramBoolean);
	        valueCache.put(Key.VibrateOn, paramBoolean);
	}

	@Override
	public boolean getSettingMsgVibrate() {
		Object val = valueCache.get(Key.VibrateOn);

        if(val == null){
            val = AotoPreferenceUtils.getInstance().getSettingMsgVibrate();
            valueCache.put(Key.VibrateOn, val);
        }
       
        return (Boolean) (val != null?val:true);
	}
	/**
	 * 是否开启扬声器
	 */
	@Override
	public void setSettingMsgSpeaker(boolean paramBoolean) {
		AotoPreferenceUtils.getInstance().setSettingMsgSpeaker(paramBoolean);
	        valueCache.put(Key.SpakerOn, paramBoolean);
		
	}

	@Override
	public boolean getSettingMsgSpeaker() {

		 Object val = valueCache.get(Key.SpakerOn);

	        if(val == null){
	            val = AotoPreferenceUtils.getInstance().getSettingMsgSpeaker();
	            valueCache.put(Key.SpakerOn, val);
	        }
	       
	        return (Boolean) (val != null?val:true);
	}


	@Override
	public boolean savePassword(String pwd) {

		 SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
	        return preferences.edit().putString(PREF_PWD, pwd).commit();  
	}

	@Override
	public String getPwd() {
		  SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
	        return preferences.getString(PREF_PWD, null);
	}

	@Override
	public String getAppProcessName() {

		return "com.aotobang.app";
	}
	 enum Key{
	        VibrateAndPlayToneOn,
	        VibrateOn,
	        PlayToneOn,
	        SpakerOn,
	        DisabledGroups,
	        DisabledIds
	    }
public static	enum PathType{
	       Audio,
	        Img,
	        File,
	        Photo,
	        Video
	    }
	@Override
	public boolean saveId(String id) {
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.edit().putString(PREF_USERID, id).commit();
	}
	@Override
	public String getId() {
		 SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
	        return preferences.getString(PREF_USERID, null);
	}
	@Override
	public boolean saveUserName(String userName) {

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.edit().putString(PREF_USERNAME, userName).commit();
	}
	@Override
	public String getUserName() {
		
		 SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
	        return preferences.getString(PREF_USERNAME, null);
	}
	@Override
	public boolean saveCachePath(PathType type,String path) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		switch(type){
		case Audio:
			  return preferences.edit().putString(PREF__PATH_AUDIO, path).commit();
		case File:
			 return preferences.edit().putString(PREF_PATH_FILE, path).commit();
		case Img:
			return preferences.edit().putString(PREF_PATH_IMG, path).commit();
		case Video:
			return preferences.edit().putString(PREF_PATH_VIDEO, path).commit();
		
		case Photo:
			return preferences.edit().putString(PREF_PATH_PHOTO, path).commit();
		
		
		}
		return false;
	}
	@Override
	public String getCachePath(PathType type) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		switch(type){
		case Audio:
			  return preferences.getString(PREF__PATH_AUDIO, null);
		case File:
			 return preferences.getString(PREF_PATH_FILE, null);
		case Img:
			return preferences.getString(PREF_PATH_IMG, null);
		case Video:
			return preferences.getString(PREF_PATH_VIDEO, null);
			case Photo:
				return preferences.getString(PREF_PATH_PHOTO, null);
		}
		return null;
	}
	@Override
	public boolean saveSessionId(String sessionId) {
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.edit().putString(PREF_SESSION_ID, sessionId).commit();
	}
	@Override
	public String getSessionId() {
		
		 SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
	        return preferences.getString(PREF_SESSION_ID, null);
	}

	@Override
	public boolean saveHavePhoto(boolean have) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.edit().putBoolean(PREF_HAVE_PHOTO, have).commit();
	}

	@Override
	public boolean havePhoto() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.getBoolean(PREF_HAVE_PHOTO, false);
	}


	@Override
	public boolean saveUserNick(String userNick) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.edit().putString(PREF_USERNICK, userNick).commit();
	}
	@Override
	public String getUserNick() {

		 SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
	        return preferences.getString(PREF_USERNICK, null);
	}

}
