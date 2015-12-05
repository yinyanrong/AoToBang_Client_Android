package com.aotobang.app.model;

import java.util.HashSet;
import java.util.Locale;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.provider.MediaStore.Audio;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.aotobang.app.entity.ChatComMsgEntity;
import com.aotobang.utils.AppUtil;

public class AotoNotifier {
	 private final static String TAG = "notify";
	    Ringtone ringtone = null;

	    protected final static String[] msg_eng = { "sent a message", "sent a picture", "sent a voice",
	                                                "sent location message", "sent a video", "sent a file", "%1 contacts sent %2 messages"
	                                              };
	    protected final static String[] msg_ch = { "发来一条消息", "发来一张图片", "发来一段语音", "发来位置信息", "发来一个视频", "发来一个文件",
	                                               "%1个联系人发来%2条消息"
	                                             };

	    protected static int notifyID = 0525; // start notification id
	    protected static int foregroundNotifyID = 0555;

	    protected NotificationManager notificationManager = null;

	    protected HashSet<String> fromUsers = new HashSet<String>();
	    protected int notificationNum = 0;

	    protected Context appContext;
	    protected String packageName;
	    protected String[] msgs;
	    protected long lastNotifiyTime;
	    protected AudioManager audioManager;
	    protected Vibrator vibrator;
	    protected AotoNotificationInfoProvider notificationInfoProvider;

	    public AotoNotifier() {
	    }
	    
	    /**
	     * 可以重载此函数
	     * this function can be override
	     * @param context
	     * @return
	     */
	    public AotoNotifier init(Context context){
	        appContext = context;
	        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

	        packageName = appContext.getApplicationInfo().packageName;
	        if (Locale.getDefault().getLanguage().equals("zh")) {
	            msgs = msg_ch;
	        } else {
	            msgs = msg_eng;
	        }

	     //   audioManager = (AudioManager) appContext.getSystemService(Context.AUDIO_SERVICE);
	     //   vibrator = (Vibrator) appContext.getSystemService(Context.VIBRATOR_SERVICE);
	        
	        return this;
	    }
	    
	    /**
	     * 可以重载此函数
	     * this function can be override
	     */
	    public void reset(){
	        resetNotificationCount();
	        cancelNotificaton();
	    }

	    void resetNotificationCount() {
	        notificationNum = 0;
	        fromUsers.clear();
	    }
	    
	    void cancelNotificaton() {
	        if (notificationManager != null)
	            notificationManager.cancel(notifyID);
	    }

	    /**
	     * 处理新收到的消息，然后发送通知
	     * 
	     * 可以重载此函数
	     * this function can be override
	     * 
	     * @param message
	     */
	    public synchronized void onNewMsg(final ChatComMsgEntity message) {
	      /*  if(EMChatManager.getInstance().isSlientMessage(message)){
	            return;
	        }*/
	        
	        // 判断app是否在后台
	        if (!AppUtil.isAppRunningForeground(appContext)) {
	            Log.d(TAG, "app is running in backgroud");
	            sendNotification(message, false);
	        } else {
	            sendNotification(message, true);

	        }
	        
	      //  viberateAndPlayTone(message);
	    }

	    /**
	     * 发送通知栏提示
	     * This can be override by subclass to provide customer implementation
	     * @param message
	     */
	    protected void sendNotification(ChatComMsgEntity message, boolean isForeground) {
	        String username = message.getFrom();
	        try {
	            String notifyText = username + " ";
	            switch (message.getContentType()) {
	            case ChatComMsgEntity.TYPE_TEXT:
	                notifyText += msgs[0];
	                break;
	            case ChatComMsgEntity.TYPE_IMG:
	                notifyText += msgs[1];
	                break;
	            case ChatComMsgEntity.TYPE_VOICE:

	                notifyText += msgs[2];
	                break;
	            case ChatComMsgEntity.TYPE_LOCATION:
	                notifyText += msgs[3];
	                break;
	            case ChatComMsgEntity.TYPE_VIDEO:
	                notifyText += msgs[4];
	                break;
	            case ChatComMsgEntity.TYPE_FILE:
	                notifyText += msgs[5];
	                break;
	            }
	            
	            PackageManager packageManager = appContext.getPackageManager();
	            String appname = (String) packageManager.getApplicationLabel(appContext.getApplicationInfo());
	            
	            // notification titile
	            String contentTitle = appname;
	            if (notificationInfoProvider != null) {
	                String customNotifyText = notificationInfoProvider.getDisplayedText(message);
	                String customCotentTitle = notificationInfoProvider.getTitle(message);
	                if (customNotifyText != null){
	                    // 设置自定义的状态栏提示内容
	                    notifyText = customNotifyText;
	                }
	                    
	                if (customCotentTitle != null){
	                    // 设置自定义的通知栏标题
	                    contentTitle = customCotentTitle;
	                }   
	            }

	            // create and send notificaiton
	            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(appContext)
	                                                                        .setSmallIcon(appContext.getApplicationInfo().icon)
	                                                                        .setWhen(System.currentTimeMillis())
	                                                                        .setAutoCancel(true);

	            Intent msgIntent = appContext.getPackageManager().getLaunchIntentForPackage(packageName);
	            if (notificationInfoProvider != null) {
	                // 设置自定义的notification点击跳转intent
	                msgIntent = notificationInfoProvider.getLaunchIntent(message);
	            }

	            PendingIntent pendingIntent = PendingIntent.getActivity(appContext, notifyID, msgIntent,PendingIntent.FLAG_UPDATE_CURRENT);

	            // prepare latest event info section
	            notificationNum++;
	            fromUsers.add(message.getFrom());

	            int fromUsersNum = fromUsers.size();
	            String summaryBody = msgs[6].replaceFirst("%1", Integer.toString(fromUsersNum)).replaceFirst("%2",Integer.toString(notificationNum));
	            
	            if (notificationInfoProvider != null) {
	                // lastest text
	                String customSummaryBody = notificationInfoProvider.getLatestText(message, fromUsersNum,notificationNum);
	                if (customSummaryBody != null){
	                    summaryBody = customSummaryBody;
	                }
	                
	                // small icon
	                int smallIcon = notificationInfoProvider.getSmallIcon(message);
	                if (smallIcon != 0){
	                    mBuilder.setSmallIcon(smallIcon);
	                }
	            }

	            mBuilder.setContentTitle(contentTitle);
	            mBuilder.setTicker(notifyText);
	            mBuilder.setContentText(summaryBody);
	            mBuilder.setContentIntent(pendingIntent);
	            //mBuilder.setNumber(notificationNum);
	            Notification notification = mBuilder.build();
	            AotoModel model = AotoPreLoad.getInstance().getModel();
	            if(model.getSettingMsgSound()){
	            	notification.defaults=Notification.DEFAULT_SOUND; 
	  	            notification.sound=Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI,"6"); 
	               }
	          
	            if (isForeground) {
	                notificationManager.notify(foregroundNotifyID, notification);
	                notificationManager.cancel(foregroundNotifyID);
	            } else {
	                notificationManager.notify(notifyID, notification);
	            }

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    
	    /**
	     * 手机震动和声音提示
	     */
	    public void viberateAndPlayTone(ChatComMsgEntity message) {
	       /* if(EMChatManager.getInstance().isSlientMessage(message)){
	            return;
	        }*/
	        
	        AotoModel model = AotoPreLoad.getInstance().getModel();
	        if(!model.getSettingMsgNotification()){
	            return;
	        }
	        
	        if (System.currentTimeMillis() - lastNotifiyTime < 1000) {
	            // received new messages within 2 seconds, skip play ringtone
	            return;
	        }
	        
	        try {
	            lastNotifiyTime = System.currentTimeMillis();
	            
	            // 判断是否处于静音模式
	            if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
	            	Log.e(TAG, "in slient mode now");
	                return;
	            }
	            
	            if(model.getSettingMsgVibrate()){
	                long[] pattern = new long[] { 0, 180, 80, 120 };
	                vibrator.vibrate(pattern, -1);
	           }

	            if(model.getSettingMsgSound()){
	                if (ringtone == null) {
	                    Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

	                    ringtone = RingtoneManager.getRingtone(appContext, notificationUri);
	                    if (ringtone == null) {
	                        Log.d(TAG, "cant find ringtone at:" + notificationUri.getPath());
	                        return;
	                    }
	               }
	                
	                if (!ringtone.isPlaying()) {
	                    String vendor = Build.MANUFACTURER;
	                    
	                    ringtone.play();
	                    // for samsung S3, we meet a bug that the phone will
	                    // continue ringtone without stop
	                    // so add below special handler to stop it after 3s if
	                    // needed
	                    if (vendor != null && vendor.toLowerCase().contains("samsung")) {
	                        Thread ctlThread = new Thread() {
	                            public void run() {
	                                try {
	                                    Thread.sleep(3000);
	                                    if (ringtone.isPlaying()) {
	                                        ringtone.stop();
	                                    }
	                                } catch (Exception e) {
	                                }
	                            }
	                        };
	                        ctlThread.run();
	                    }
	                }
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }


	    /**
	     * 设置NotificationInfoProvider
	     * 
	     * @param provider
	     */
	    public void setNotificationInfoProvider(AotoNotificationInfoProvider provider) {
	        notificationInfoProvider = provider;
	    }

	    public interface AotoNotificationInfoProvider {
	        /**
	         * 设置发送notification时状态栏提示新消息的内容(比如Xxx发来了一条图片消息)
	         * 
	         * @param message
	         *            接收到的消息
	         * @return null为使用默认
	         */
	        String getDisplayedText(ChatComMsgEntity message);

	        /**
	         * 设置notification持续显示的新消息提示(比如2个联系人发来了5条消息)
	         * 
	         * @param message
	         *            接收到的消息
	         * @param fromUsersNum
	         *            发送人的数量
	         * @param messageNum
	         *            消息数量
	         * @return null为使用默认
	         */
	        String getLatestText(ChatComMsgEntity message, int fromUsersNum, int messageNum);

	        /**
	         * 设置notification标题
	         * 
	         * @param message
	         * @return null为使用默认
	         */
	        String getTitle(ChatComMsgEntity message);

	        /**
	         * 设置小图标
	         * 
	         * @param message
	         * @return 0使用默认图标
	         */
	        int getSmallIcon(ChatComMsgEntity message);

	        /**
	         * 设置notification点击时的跳转intent
	         * 
	         * @param message
	         *            显示在notification上最近的一条消息
	         * @return null为使用默认
	         */
	        Intent getLaunchIntent(ChatComMsgEntity message);
	    }
}
