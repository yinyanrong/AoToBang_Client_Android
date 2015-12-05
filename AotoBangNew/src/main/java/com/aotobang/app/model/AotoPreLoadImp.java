package com.aotobang.app.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.aotobang.app.activity.ChatActivity;
import com.aotobang.app.activity.HomeActivity;
import com.aotobang.app.callback.RemoteLoginListener;
import com.aotobang.app.entity.ChatComMsgEntity;
import com.aotobang.app.entity.Friend;
import com.aotobang.app.manager.ChatManager;
import com.aotobang.app.model.AotoNotifier.AotoNotificationInfoProvider;
import com.aotobang.local.bean.LocalFriend;

public class AotoPreLoadImp extends AotoPreLoad {
	private Map<String, LocalFriend> contactList;
	 /**
	     * 用来记录foreground Activity
	     */
	    private List<Activity> activityList = new ArrayList<Activity>();
	    
	    public void pushActivity(Activity activity){
	        if(!activityList.contains(activity)){
	            activityList.add(0,activity); 
	        }
	    }
	    
	    public void popActivity(Activity activity){
	        activityList.remove(activity);
	    }
private  BroadcastReceiver  newMessageReceiver =new BroadcastReceiver(){

	@Override
	public void onReceive(Context context, Intent intent) {
		String msgId=intent.getStringExtra("msgid");
		ChatComMsgEntity message=ChatManager.getInstance().getMsgDao().findMsgByUUID(msgId);
		if(activityList.size() <= 0){
			AotoPreLoad.getInstance().getNotifier().onNewMsg(message);
        }
		
	}};

	@Override
	public AotoNotificationInfoProvider createNotificationListener() {

		return new AotoNotificationInfoProvider() {
            
            @Override
            public String getTitle(ChatComMsgEntity message) {
              //修改标题,这里使用默认
                return null;
            }
            
            @Override
            public int getSmallIcon(ChatComMsgEntity message) {
              //设置小图标，这里为默认
                return 0;
            }
            
            @Override
            public String getDisplayedText(ChatComMsgEntity message) {
                // 设置状态栏的消息提示，可以根据message的类型做相应提示
             //   String ticker = CommonUtils.getMessageDigest(message, appContext);
                if(message.getContentType() == ChatComMsgEntity.TYPE_TEXT){
                  //  ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
                }
                
                return null;//message.getFrom() + ": " + ticker;
            }
            
            @Override
            public String getLatestText(ChatComMsgEntity message, int fromUsersNum, int messageNum) {
                return null;
                // return fromUsersNum + "个基友，发来了" + messageNum + "条消息";
            }
            
            @Override
            public Intent getLaunchIntent(ChatComMsgEntity message) {
                //设置点击通知栏跳转事件
                Intent intent = new Intent(appContext, ChatActivity.class);
                intent.putExtra("userId", message.getFrom());
                return intent;
            }
        };
    }
    
    @Override
    public void initListener() {
    	super.initListener();
    	IntentFilter intentFilter = new IntentFilter();
    	intentFilter.addAction(ChatManager.NEW_MESSAGE_ACTION);
    	intentFilter.addAction(ChatManager.NEW_FRIEND_ACTION);
    	intentFilter.setPriority(20);
    	appContext.registerReceiver(newMessageReceiver, intentFilter);
    	ChatManager.getInstance().registRomoteLoginListener(new RemoteLoginListener() {
			
			@Override
			public void onRemoteLogin() {
				Intent intent=new Intent();
				intent.setClass(appContext, HomeActivity.class);
				 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			     intent.putExtra("remoteLogin", true);
			     appContext.startActivity(intent);
			}
		});
    	
    }

	@Override
	public AotoModel createModel() {

		return new AotoModelImp(appContext);
	}

	@Override
	public AotoNotifier createNotifier() {

		return new AotoNotifier();
	}
	  /**
     * 获取内存中好友user list
     *
     * @return
     */
    public Map<String, LocalFriend> getContactList() {
        if (getId() != null && contactList == null) {
            contactList = ((AotoModelImp) getModel()).getContactList();
        }
        
        return contactList;
    }

    /**
     * 设置好友user list到内存中
     *
     * @param contactList
     */
    public void setContactList(Map<String, LocalFriend> contactList) {
        this.contactList = contactList;
    }

}
