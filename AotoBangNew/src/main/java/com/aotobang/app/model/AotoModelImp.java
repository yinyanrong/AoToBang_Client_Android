package com.aotobang.app.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.aotobang.app.entity.Friend;
import com.aotobang.local.bean.LocalFriend;
import com.aotobang.local.dao.FriendDao;
import com.aotobang.utils.AlphaUtil;

public class AotoModelImp extends DefaultModelImp{

	public AotoModelImp(Context ctx) {
		super(ctx);
	}
	 public boolean saveContactList(List<Friend> contactList) {
		 boolean complete=true;
	        FriendDao dao = new FriendDao();
	        for(Friend friend :contactList){
	        	LocalFriend mfirend =new LocalFriend();
	        	mfirend.setLocal_avatar(friend.getAvatar());
	        	mfirend.setNickname(friend.getName());
	        	mfirend.setUserid(friend.getUuid());
	        	long i=dao.insert(mfirend);
	        	if(i==-1)
	        		complete=false;
	        	
	        }
	        
	        
	        return complete;
	    }

	    public Map<String, LocalFriend> getContactList() {
	    	Map<String, LocalFriend> map =new HashMap<String, LocalFriend>();
	    	   FriendDao dao = new FriendDao();
	    	   List<LocalFriend> friends=dao.findAll();
	    	   for(LocalFriend friend:friends){
	    		   map.put(friend.getUserid(), friend);
	    		   
	    	   }
	        return map;
	    }

	    public void closeDB() {
	    	
	    	
	    	
	    	
	    }
	
	
	
@Override
public String getAppProcessName() {
	
	return context.getPackageName();
}
}
