package com.aotobang.app.model;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;

import com.aotobang.app.model.AotoNotifier.AotoNotificationInfoProvider;
import com.aotobang.app.model.DefaultModelImp.PathType;
import com.aotobang.utils.LogUtil;


public abstract class AotoPreLoad {
	/**
     * application context
     */
	public Context appContext = null;
    /**
     *  UUID 
     */
	protected String userId = null;
	 /**
     *  sessionId 
     */
	protected String sessionId = null;
	  /**
     *  username
     */
	protected String userName = null;
	protected String userNick = null;
    
  
	/**
     * password 
     */
	protected String password = null;
    /**
     * 是否保存照片
     */
	protected  boolean havePhoto;

	/**
	 * 缓存路径
	 */
	protected String path_img = null;
	protected String path_photo = null;
	protected String path_audio = null;
	protected String path_file = null;
	protected String path_video = null;
    /**
     * init flag: test if has been inited before, we don't need to init again
     */
    private boolean inited = false;
    /**
     * the global  instance
     */
    private static AotoPreLoad instance = null;
    /**
     * the notifier
     */
    protected AotoNotifier notifier = null;
    
    protected    AotoModel aotoModel = null;
    
    public AotoPreLoad(){
    	instance = this;
    }
    public synchronized boolean onInit(Context context){
        if(inited){
            return true;
        }

        appContext = context;
        aotoModel=createModel();
        if(aotoModel == null){
        	aotoModel = new DefaultModelImp(appContext);
        }
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        
        LogUtil.info(AotoPreLoad.class, "process app name : " + processAppName);
        
        // 如果app启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process name就立即返回
        if (processAppName == null || !processAppName.equalsIgnoreCase(aotoModel.getAppProcessName())) {
        	 LogUtil.info(AotoPreLoad.class, "enter the service process!");
            
            // 则此application::onCreate 是被service 调用的，直接返回
            return false;
        }

        
        initListener();  
        notifier = createNotifier();
        notifier.init(appContext);
        notifier.setNotificationInfoProvider(createNotificationListener());
        inited = true;
        return true;
    }
    public void initListener() {
    	
    	
    	
    	
	}
	public abstract AotoNotificationInfoProvider createNotificationListener() ;
    public abstract AotoModel createModel() ;
    public abstract AotoNotifier createNotifier() ;
    public AotoModel getModel(){
        return aotoModel;
    }	

    public AotoNotifier getNotifier(){
        return notifier;
    }
    public static AotoPreLoad getInstance(){
        return instance;
    }
    
    public String getSessionId(){
    	if(sessionId==null){
    		sessionId=aotoModel.getSessionId();
    		
    	}
    	return sessionId;
    	
    }
    public void setSessionId(String sessionId){
    	if(sessionId!=null){
    		if(aotoModel.saveSessionId(sessionId)){
    			this.sessionId=sessionId;
    			
    		}
    		
    	}
    	
    	
    }
    public String getUserNick() {
    	if(userNick == null){
    		userNick = aotoModel.getUserNick();
        }
    	
  		return userNick;
  	}
  	public void setUserNick(String userNick) {
  		 if (userNick != null) {
             if(aotoModel.saveUserNick(userNick)){
                 this.userNick = userNick;
             }
         }
  		this.userNick = userNick;
  	}
    public String getId(){
        if(userId == null){
        	userId = aotoModel.getId();
        }
        return userId;
    }
    
    public String getPassword(){
        if(password == null){
            password = aotoModel.getPwd();
        }
        return password;    
    }
    
    public void setId(String id){
        if (id != null) {
            if(aotoModel.saveId(id)){
                this.userId = id;
            }
        }
    }
    public void setUserName(String userName){
        if (userName != null) {
            if(aotoModel.saveUserName(userName)){
                this.userName = userName;
            }
        }
    }
    public String getUserName(){
        if(userName == null){
        	userName = aotoModel.getUserName();
        }
        return userName;    
    }
    
    public void setPassword(String password){
        if(aotoModel.savePassword(password)){
            this.password = password;
        }
    }
    public boolean isHavePhoto(){

        return aotoModel.havePhoto();

    }

    public  void setHavePhoto(boolean have){
        if(aotoModel.saveHavePhoto(have)){
            this.havePhoto=have;
        }



    }
    public void setCachePath(PathType type,String path){
    	//必须先校验文件夹是否存在。否则下载无效
    	File file=new File(path);
    	if(!file.exists()){
    		file.mkdir();
    	}
    
    	switch(type){
    	case Audio:
    		if(aotoModel.saveCachePath(type, path))
    			this.path_audio=path;
    		break;
    	case Img:
    		if(aotoModel.saveCachePath(type, path))
    			this.path_img=path;
    		break;
    	case Photo:
    		if(aotoModel.saveCachePath(type, path))
    			this.path_photo=path;
    		break;
    	case File:
    		if(aotoModel.saveCachePath(type, path))
    			this.path_file=path;
    		break;
    	case Video:
    		if(aotoModel.saveCachePath(type, path))
    			this.path_video=path;
    		break;
    	
    	}
    	
    }
    public String getCachePath(PathType type){
    	switch(type){
    	case Audio:
    		 if(path_audio == null){
    			 path_audio = aotoModel.getCachePath(type);
    	        }
    		 return path_audio;
    	case Img:
    	 if(path_img == null){
    		 path_img = aotoModel.getCachePath(type);
	        }
		 return path_img;
    	case Photo:
       	 if(path_photo == null){
       		path_photo = aotoModel.getCachePath(type);
   	        }
   		 return path_photo;
    	case File:
    		 if(path_file == null){
    			 path_file = aotoModel.getCachePath(type);
    	        }
    		 return path_file;
    	case Video:
    		 if(path_video == null){
    			 path_video = aotoModel.getCachePath(type);
    	        }
    		 return path_video;
    		
    		
    		
    		
    	
    	}
    	return null;
    	
    }
	/**
     * check the application process name if process name is not qualified, then we think it is a service process and we will not init SDK
     * @param pID
     * @return
     */
    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = appContext.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    // Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
                    // info.processName +"  Label: "+c.toString());
                    // processName = c.toString();
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }
    
}
