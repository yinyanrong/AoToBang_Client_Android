package com.aotobang.local.bean;

import com.aotobang.local.db.DBHelper;
import com.aotobang.local.db.annotation.Column;
import com.aotobang.local.db.annotation.ID;
import com.aotobang.local.db.annotation.TableName;

@TableName(DBHelper.TABLE_CONVERSATION)
public class Conversation {
	public static final int TYPE_CHAT=0;
	@ID(autoincrement = true)
	@Column(DBHelper.TABLE_ID)
	private int id;
	@Column(DBHelper.TABLE_CONVERSATION_UUID)
	private String user_id;
	@Column(DBHelper.TABLE_CONVERSATION_UNREAD_COUNT)
	private int unread_count;
	@Column(DBHelper.TABLE_CONVERSATION_NICKNAME)
	private String nickname;
	@Column(DBHelper.TABLE_CONVERSATION_TYPE)
	private int con_type;
	@Column(DBHelper.TABLE_CONVERSATION_AVATAR_URL)
	private String avatar_url;
	@Column(DBHelper.TABLE_CONVERSATION_CREATE_TIME)
	private String time;
	@Column(DBHelper.TABLE_CONVERSATION_USERNAME)
	private String username;
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	/*@Column(DBHelper.TABLE_CONVERSATION_AVATAR_URL_LOCAL)
	private String avatar_local;
	public String getAvatar_local() {
		return avatar_local;
	}

	public void setAvatar_local(String avatar_local) {
		this.avatar_local = avatar_local;
	}*/

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getCon_type() {
		return con_type;
	}

	public void setCon_type(int con_type) {
		this.con_type = con_type;
	}
	public String getAvatar_url() {
		return avatar_url;
	}

	public void setAvatar_url(String avatar_url) {
		this.avatar_url = avatar_url;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	public int getUnread_count() {
		return unread_count;
	}

	public void setUnread_count(int unread_count) {
		this.unread_count = unread_count;
	}
}
