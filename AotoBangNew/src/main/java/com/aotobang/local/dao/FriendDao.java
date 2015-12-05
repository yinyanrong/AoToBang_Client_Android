package com.aotobang.local.dao;

import android.content.ContentValues;

import java.util.List;

import com.aotobang.local.bean.LocalFriend;
import com.aotobang.local.db.DAOSupport;
import com.aotobang.local.db.DBHelper;

public class FriendDao extends DAOSupport<LocalFriend>{

	public FriendDao() {
		super();
	}
	
	public int deleteByUUID(String userId){
		return database.delete(getTableName(), DBHelper.TABLE_CONTACT_UUID + "=?", new String[] { userId });
		
	}
	public LocalFriend findByUserId(String userId){
		
		List<LocalFriend>  result=findByCondition( DBHelper.TABLE_CONTACT_UUID + "=?", new String[]{userId}, null);
		if(result.size()>0)
			return result.get(0);
		else
			return null;
		
	}
	public int updataUserInfo(LocalFriend user){

		ContentValues values=new ContentValues();
		values.put(DBHelper.TABLE_CONTACT_SEX,user.getSex());
		values.put(DBHelper.TABLE_CONTACT_NICKNAME,user.getNickname());
		return database.update(getTableName(), values, DBHelper.TABLE_CONTACT_UUID + "=?", new String[]{user.getUserid() + ""});


	}

}
