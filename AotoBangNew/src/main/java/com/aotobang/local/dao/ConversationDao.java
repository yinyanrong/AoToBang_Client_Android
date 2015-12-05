package com.aotobang.local.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;

import com.aotobang.app.entity.ChatComMsgEntity;
import com.aotobang.app.manager.AotoConversation;
import com.aotobang.local.bean.Conversation;
import com.aotobang.local.bean.LocalMsg;
import com.aotobang.local.db.DAOSupport;
import com.aotobang.local.db.DBHelper;

public class ConversationDao extends DAOSupport<Conversation> {

	public ConversationDao() {
		super();
	}

	public  int deletByUUID(String id){
		
		return database.delete(getTableName(), DBHelper.TABLE_CONVERSATION_UUID + "=?", new String[] { id });
		
	}
	public  void updataByUUID(String userId,int count){
		ContentValues values = new ContentValues();
		values.put(DBHelper.TABLE_CONVERSATION_UNREAD_COUNT, count);
		database.update(getTableName(), values,  DBHelper.TABLE_CONVERSATION_UUID + "=?", new String[]{userId});
		
	
		
	/*	Conversation c=new Conversation();
		c.setUser_id(userId);
		c.setUnread_count(count);
		update(c);*/
		
		
	}
	
public  List<Conversation> queryAll(){
	List<Conversation> result = null;

		String sql="select * from "+DBHelper.TABLE_CONVERSATION;
		Cursor	cursor =database.rawQuery(sql, null);
		
		if (cursor != null) {
			result = new ArrayList<Conversation>();
			while (cursor.moveToNext()) {
			
				Conversation m = new Conversation();
				
				fillFields(cursor, m);

				result.add(m);

			}

			cursor.close();
		}
		
		
		
		return result;
		
	}
	
	
	public AotoConversation findConversionById(String userId){
	List<Conversation> result=	findByCondition(DBHelper.TABLE_CONVERSATION_UUID + "=?", new String[]{userId}, null);
		if(result.size()>0){
			return localConToAotoCon(result.get(0));
		}
		
		return null;
	}
	
	public  AotoConversation  localConToAotoCon(Conversation local){
		AotoConversation  aotoCon=new AotoConversation(local.getUser_id());
		aotoCon.setUnreadMsgCount(local.getUnread_count());
		aotoCon.setUserNick(local.getNickname());
		aotoCon.setAvatar_remote(local.getAvatar_url());
		aotoCon.setUserName(local.getUsername());
		return aotoCon;
		
	}
	
}
