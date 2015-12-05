package com.aotobang.local.bean;

import com.aotobang.local.db.DBHelper;
import com.aotobang.local.db.annotation.Column;
import com.aotobang.local.db.annotation.ID;
import com.aotobang.local.db.annotation.TableName;

@TableName(DBHelper.TABLE_CHAT)
public class LocalMsg {
	public static final int STATUS_CREATE=0;
	public static final int STATUS_INPROGRESS=1;
	public static final int STATUS_SUCCESS=2;
	public static final int STATUS_FAIL=3;
	
	
	@ID(autoincrement = true)
	@Column(DBHelper.TABLE_ID)
	private int id;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	@Column(DBHelper.TABLE_CHAT_FROM)
	private String msg_from;
	@Column(DBHelper.TABLE_CHAT_SEND)
	private int send;
	@Column(DBHelper.TABLE_CHAT_MSG_ID)
	private String msg_id;
	@Column(DBHelper.TABLE_CHAT_MSG_TIME)
	private String msg_time;
	@Column(DBHelper.TABLE_CHAT_ACKED)
	private  int acked;
	@Column(DBHelper.TABLE_CHAT_DELIVERED)
	private int delivered;
	@Column(DBHelper.TABLE_CHAT_STATUS)
	private int status;
	@Column(DBHelper.TABLE_CHAT_TO)
	private String msg_to;
	@Column(DBHelper.TABLE_CHAT_LISTEND)
	private int listend;
	@Column(DBHelper.TABLE_CHAT_MSG_BODY)
	private String msg_body;
	@Column(DBHelper.TABLE_CHAT_MSG_REMOTE_URL)
	private String remote_url;
	@Column(DBHelper.TABLE_CHAT_MSG_LOCAL_URL)
	private String local_url;
	@Column(DBHelper.TABLE_CHAT_MSG_TYPE)
	private int msg_type;
	@Column(DBHelper.TABLE_CHAT_MSG_THUMBNAIL_URL)
	private String thumbnail_url;
	@Column(DBHelper.TABLE_CHAT_VOICE_TIME)
	private int voice_time;
	@Column(DBHelper.TABLE_CHAT_MSG_PARTICIPANT)
	private String participant;
	
	public String getMsg_from() {
		return msg_from;
	}
	public void setMsg_from(String msg_from) {
		this.msg_from = msg_from;
	}
	public String getParticipant() {
		return participant;
	}
	public void setParticipant(String participant) {
		this.participant = participant;
	}
	public int getVoice_time() {
		return voice_time;
	}
	public void setVoice_time(int voice_time) {
		this.voice_time = voice_time;
	}
	public String getThumbnail_url() {
		return thumbnail_url;
	}
	public void setThumbnail_url(String thumbnail_url) {
		this.thumbnail_url = thumbnail_url;
	}
	public String getRemote_url() {
		return remote_url;
	}
	public void setRemote_url(String remote_url) {
		this.remote_url = remote_url;
	}
	public String getLocal_url() {
		return local_url;
	}
	public void setLocal_url(String local_url) {
		this.local_url = local_url;
	}
	public int getMsg_type() {
		return msg_type;
	}
	public void setMsg_type(int msg_type) {
		this.msg_type = msg_type;
	}
	
	public int getSend() {
		return send;
	}
	public void setSend(int send) {
		this.send = send;
	}
	public String getMsg_id() {
		return msg_id;
	}
	public void setMsg_id(String msg_id) {
		this.msg_id = msg_id;
	}
	public String getMsg_time() {
		return msg_time;
	}
	public void setMsg_time(String msg_time) {
		this.msg_time = msg_time;
	}
	public int getAcked() {
		return acked;
	}
	public void setAcked(int acked) {
		this.acked = acked;
	}
	public int getDelivered() {
		return delivered;
	}
	public void setDelivered(int delivered) {
		this.delivered = delivered;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getListend() {
		return listend;
	}
	public void setListend(int listend) {
		this.listend = listend;
	}
	public String getMsg_body() {
		return msg_body;
	}
	public void setMsg_body(String msg_body) {
		this.msg_body = msg_body;
	}

	public String getMsg_to() {
		return msg_to;
	}
	public void setMsg_to(String msg_to) {
		this.msg_to = msg_to;
	}

}
