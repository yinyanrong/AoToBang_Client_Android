package com.aotobang.local.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aotobang.app.model.AotoPreLoadImp;

public class DBHelper extends SQLiteOpenHelper {

	private static final int START_VERSION = 1;
	private static final int HISTORY_VERSION = 2;
	private static final int CURRENT_VERSION = 3;

	public static final String TABLE_ID = "_id";
	//联系人
	public static final String TABLE_CONTACT="contact";
	public static final String TABLE_CONTACT_SEX = "sex";// 用户id
	public static final String TABLE_CONTACT_UUID = "userid";// 用户id
	public static final String TABLE_CONTACT_NICKNAME = "nickname";// 
	public static final String TABLE_CONTACT_USERNAME = "username";// 用户名
	public static final String TABLE_CONTACT_LOCAL_AVATAR = "local_avatar";// 头像url
	public static final String TABLE_CONTACT_REMOTE_AVATAR = "remote_avatar";// 头像url
	
	//消息表
	public static final String TABLE_CHAT = "chat_msg";
	public static final String TABLE_CHAT_SEND = "send";
	public static final String TABLE_CHAT_MSG_ID = "msg_id";// 消息id
	public static final String TABLE_CHAT_MSG_TIME = "msg_time";// 时间
	public static final String TABLE_CHAT_ACKED= "acked";// 是否发送了消息已读回执
	public static final String TABLE_CHAT_DELIVERED= "delivered";// 是否发送了送达回执
	public static final String TABLE_CHAT_STATUS = "status";// 状态
	public static final String TABLE_CHAT_FROM = "msg_from";// 对方uuid
	public static final String TABLE_CHAT_TO = "msg_to";// 对方uuid
	public static final String TABLE_CHAT_LISTEND = "listend";// 是否听了
	public static final String TABLE_CHAT_VOICE_TIME = "voice_time";// 是否听了
	public static final String TABLE_CHAT_MSG_BODY = "msg_body";// 消息内容
	public static final String TABLE_CHAT_MSG_REMOTE_URL = "remote_url";// 远程url
	public static final String TABLE_CHAT_MSG_LOCAL_URL = "local_url";// 本地url
	public static final String TABLE_CHAT_MSG_THUMBNAIL_URL = "thumbnail_url";// 缩略图url
	public static final String TABLE_CHAT_MSG_TYPE = "msg_type";// 消息类型
	public static final String TABLE_CHAT_MSG_PARTICIPANT = "participant";//参与者
	
	//会话表
	public static final String TABLE_CONVERSATION = "conversation";
	public static final String TABLE_CONVERSATION_UUID = "user_id";// 好友id
	public static final String TABLE_CONVERSATION_TYPE = "con_type";// 类型
	public static final String TABLE_CONVERSATION_NICKNAME= "nickname";// 昵称
	public static final String TABLE_CONVERSATION_USERNAME= "username";// 用户名
	public static final String TABLE_CONVERSATION_AVATAR_URL= "avatar_url";// 头像地址
	//public static final String TABLE_CONVERSATION_AVATAR_URL_LOCAL= "avatar_local";// 头像地址
	public static final String TABLE_CONVERSATION_CREATE_TIME= "time";// 第一次创建时间
	public static final String TABLE_CONVERSATION_UNREAD_COUNT = "unread_count";//未读消息数
	//相册表
	public static final String TABLE_PHOTO = "photo";
	public static final String TABLE_PHOTO_LOCAL_URL = "local_url";
	public static final String TABLE_PHOTO_REMOTE_URL = "remote_url";
	public static final String TABLE_PHOTO_INDEX = "photo_index";

	
	public DBHelper(Context context) {
		super(context, getUserDatabaseName(), null, START_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 联系人
		db.execSQL("CREATE TABLE  if not exists " + TABLE_CONTACT + " (" + //
				TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + //
						TABLE_CONTACT_SEX + " varchar(10), "+
						TABLE_CONTACT_UUID + " varchar(50), "+
				TABLE_CONTACT_LOCAL_AVATAR + " varchar(50), "+ 
				TABLE_CONTACT_REMOTE_AVATAR + " varchar(50), "+ 
				TABLE_CONTACT_USERNAME + " varchar(50), "+ 
				TABLE_CONTACT_NICKNAME + " varchar(50)) "
		);
		// 消息表
				db.execSQL("CREATE TABLE  if not exists " + TABLE_CHAT + " (" + //
						TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + //
						TABLE_CHAT_MSG_ID + " varchar(50), "+ 
						TABLE_CHAT_SEND + " varchar(50), "+ 
						TABLE_CHAT_ACKED + " varchar(50), "+ 
						TABLE_CHAT_DELIVERED + " varchar(50), "+ 
						TABLE_CHAT_STATUS + " varchar(50), "+ 
						TABLE_CHAT_MSG_TIME + " varchar(50), "+ 
						TABLE_CHAT_LISTEND + " varchar(50), "+ 
						TABLE_CHAT_VOICE_TIME + " varchar(50), "+ 
						TABLE_CHAT_TO + " varchar(50), "+ 
						TABLE_CHAT_FROM + " varchar(50), "+ 
						TABLE_CHAT_MSG_REMOTE_URL + " varchar(50), "+ 
						TABLE_CHAT_MSG_LOCAL_URL + " varchar(50), "+ 
						TABLE_CHAT_MSG_THUMBNAIL_URL + " varchar(50), "+ 
						TABLE_CHAT_MSG_TYPE + " varchar(50), "+ 
						TABLE_CHAT_MSG_PARTICIPANT + " varchar(50), "+ 
						TABLE_CHAT_MSG_BODY + " varchar(50))"
				);
		// 会话表
		db.execSQL("CREATE TABLE  if not exists " + TABLE_CONVERSATION + " (" + //
						TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + //
						TABLE_CONVERSATION_UUID + " varchar(50), "+ 
						TABLE_CONVERSATION_TYPE + " varchar(50), "+ 
						TABLE_CONVERSATION_UNREAD_COUNT + " varchar(50), "+ 
						TABLE_CONVERSATION_CREATE_TIME + " varchar(50), "+ 
						TABLE_CONVERSATION_USERNAME + " varchar(50), "+ 
						TABLE_CONVERSATION_AVATAR_URL + " varchar(50), "+ 
						//TABLE_CONVERSATION_AVATAR_URL_LOCAL + " varchar(50), "+ 
						TABLE_CONVERSATION_NICKNAME + " varchar(50))"
				);

		// photo
		db.execSQL("CREATE TABLE  if not exists " + TABLE_PHOTO + " (" + //
						TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + //
						TABLE_PHOTO_LOCAL_URL + " varchar(50), "+
						TABLE_PHOTO_REMOTE_URL + " varchar(50), "+
						TABLE_PHOTO_INDEX + " varchar(10))"
		);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		
		
	}
	private static String getUserDatabaseName() {
        return  AotoPreLoadImp.getInstance().getId() + "data.db";
    }

}
