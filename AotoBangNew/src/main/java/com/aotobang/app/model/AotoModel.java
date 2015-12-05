package com.aotobang.app.model;

import com.aotobang.app.model.DefaultModelImp.PathType;

import java.util.Set;

public abstract class AotoModel {
	 public abstract void setSettingMsgNotification(boolean paramBoolean);
	    
	    // 震动和声音总开关，来消息时，是否允许此开关打开
	    // the vibrate and sound notification are allowed or not?
	    public abstract boolean getSettingMsgNotification();

	    public abstract void setSettingMsgSound(boolean paramBoolean);
	    
	    // 是否打开声音
	    // sound notification is switched on or not?
	    public abstract boolean getSettingMsgSound();

	    public abstract void setSettingMsgVibrate(boolean paramBoolean);
	    
	    // 是否打开震动
	    // vibrate notification is switched on or not?
	    public abstract boolean getSettingMsgVibrate();

	    public abstract void setSettingMsgSpeaker(boolean paramBoolean);
	    
	    // 是否打开扬声器
	    // the speaker is switched on or not?
	    public abstract boolean getSettingMsgSpeaker();
	   
	    public abstract boolean saveId(String id);
	    public abstract String getId();
	    public abstract boolean savePassword(String pwd);
	    public abstract String getPwd();
	    public abstract boolean saveUserName(String userName);
	    public abstract String getUserName();
	    public abstract boolean saveUserNick(String userNick);
	    public abstract String getUserNick();
	    public abstract boolean saveCachePath(PathType type,String path);
	    public abstract String getCachePath(PathType type);
	    public abstract boolean saveSessionId(String sessionId);
	    public abstract String getSessionId();
		public abstract boolean saveHavePhoto(boolean have);
		public abstract boolean havePhoto();

	    /**
	     * 返回application所在的process name,默认是包名
	     * @return
	     */
	    public abstract String getAppProcessName();
	    /**
	     * 是否总是接收好友邀请
	     * @return
	     */
	    public boolean getAcceptInvitationAlways(){
	        return false;
	    }
	    
	    
	    /**
	     * 是否需要已读回执
	     * @return
	     */
	    public boolean getRequireReadAck(){
	        return true;
	    }
	    
	    /**
	     * 是否需要已送达回执
	     * @return
	     */
	    public boolean getRequireDeliveryAck(){
	        return false;
	    }
	    
	    
	    /**
	     * 是否设置debug模式
	     * @return
	     */
	    public boolean isDebugMode(){
	        return false;
	    }
}
