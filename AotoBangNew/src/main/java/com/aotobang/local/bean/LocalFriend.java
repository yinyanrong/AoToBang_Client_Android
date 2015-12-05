package com.aotobang.local.bean;

import com.aotobang.local.db.DBHelper;
import com.aotobang.local.db.annotation.Column;
import com.aotobang.local.db.annotation.ID;
import com.aotobang.local.db.annotation.TableName;

@TableName(DBHelper.TABLE_CONTACT)
public class LocalFriend {
	@ID(autoincrement = true)
	@Column(DBHelper.TABLE_ID)
	private int id;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	@Column(DBHelper.TABLE_CONTACT_NICKNAME)
	private String nickname;
	@Column(DBHelper.TABLE_CONTACT_LOCAL_AVATAR)
	private String local_avatar;
	@Column(DBHelper.TABLE_CONTACT_UUID)
	private String userid;
	@Column(DBHelper.TABLE_CONTACT_REMOTE_AVATAR)
	private String remote_avatar;
	@Column(DBHelper.TABLE_CONTACT_USERNAME)
	private String username;
	@Column(DBHelper.TABLE_CONTACT_SEX)
	private int sex;

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}


	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getLocal_avatar() {
		return local_avatar;
	}
	public void setLocal_avatar(String local_avatar) {
		this.local_avatar = local_avatar;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getRemote_avatar() {
		return remote_avatar;
	}
	public void setRemote_avatar(String remote_avatar) {
		this.remote_avatar = remote_avatar;
	}
	
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
}
