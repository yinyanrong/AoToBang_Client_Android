package com.aotobang.local.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;

import com.aotobang.app.applaction.AotoBangApp;
import com.aotobang.app.entity.ChatComMsgEntity;
import com.aotobang.app.entity.ChatComMsgEntity.Direct;
import com.aotobang.app.entity.FileMsgBody;
import com.aotobang.app.entity.ImgMsgBody;
import com.aotobang.app.entity.TextMsgBody;
import com.aotobang.app.entity.VoiceMsgBody;
import com.aotobang.app.manager.ChatManager;
import com.aotobang.local.bean.LocalMsg;
import com.aotobang.local.db.DAOSupport;
import com.aotobang.local.db.DBHelper;

public class LocalMsgDao extends DAOSupport<LocalMsg> {

	public  int deleteByUUID(String id){
		
		
		return database.delete(getTableName(), DBHelper.TABLE_CHAT_MSG_ID + "=?", new String[]{id});
		
		
	}
	/**
	 * 获取一个好友的所有消息条目
	 * @param userId
	 * @return
	 */
	public long getAllMsgCountByUserId(String userId){
		String sql="select count(*)from "+DBHelper.TABLE_CHAT+" where "+DBHelper.TABLE_CHAT_FROM+"=?";
		Cursor	cursor=database.rawQuery(sql, new String[]{userId});
		 cursor.moveToFirst();
		 long i=cursor.getLong(0);
		 cursor.close();
		 return i;
		
	}
	/**
	 * 更改voice的状态
	 * @param userId
	 * @return
	 */
	public void updateListend(ChatComMsgEntity entity){
		VoiceMsgBody body=(VoiceMsgBody) entity.getMsgBody();
		ContentValues values=new ContentValues();
		values.put(DBHelper.TABLE_CHAT_LISTEND,body.isListen()?1:0 );
		database.update(getTableName(), values,  DBHelper.TABLE_CHAT_MSG_ID + "=?", new String[]{entity.getMsgId()});
	}
	/**
	 * 更改msg的状态  包含本地，远程url 
	 * @param userId
	 * @return
	 */
	public void updateMsgStatus(ChatComMsgEntity entity){
		ContentValues values=new ContentValues();
	
		//updataMsgStatus(entity);
		if(entity.direct==Direct.SEND&&entity.getMsgBody()instanceof FileMsgBody){
		FileMsgBody body=(FileMsgBody) entity.getMsgBody();
		values.put(DBHelper.TABLE_CHAT_MSG_REMOTE_URL,body.getRemoteUrl() );
		}
		if(entity.direct==Direct.RECEIVE &&entity.getMsgBody()instanceof FileMsgBody){
			FileMsgBody body=(FileMsgBody) entity.getMsgBody();
			values.put(DBHelper.TABLE_CHAT_MSG_LOCAL_URL,body.getLocalUrl() );
			
		}
		switch(entity.status){
		case CREATE:
			values.put(DBHelper.TABLE_CHAT_STATUS,0 );
			break;
		case INPROGRESS:
			values.put(DBHelper.TABLE_CHAT_STATUS,1 );
			break;
		case SUCCESS:
			values.put(DBHelper.TABLE_CHAT_STATUS,2 );
			break;
		case FAIL:
			values.put(DBHelper.TABLE_CHAT_STATUS,3 );
			break;
		
		
		}
		database.update(getTableName(), values,  DBHelper.TABLE_CHAT_MSG_ID + "=?", new String[]{entity.getMsgId()});
		
		
	}
	/**
	 * 插入一个msg
	 * @param entity
	 */
	
	public void insertMsgEntity(ChatComMsgEntity entity){
		
		insert(entityToLocalMsg(entity));
		
	}
	private LocalMsg entityToLocalMsg(ChatComMsgEntity entity){
		
		LocalMsg msg =new LocalMsg();
		msg.setSend(entity.direct==ChatComMsgEntity.Direct.SEND?0:1);
		msg.setParticipant(entity.direct==ChatComMsgEntity.Direct.SEND?entity.getTo():entity.getFrom());
		msg.setMsg_from(entity.getFrom());
		msg.setMsg_to(entity.getTo());
		msg.setMsg_id(entity.getMsgId());
		msg.setMsg_time(entity.getMsgTime()+"");
		msg.setMsg_type(entity.getContentType());
		switch(entity.status){
		case CREATE:
			msg.setStatus(0);
			break;
		case INPROGRESS:
			msg.setStatus(1);
			break;
		case SUCCESS:
			msg.setStatus(2);
			break;
		case FAIL:
			msg.setStatus(3);
			break;
		}
		switch(entity.getContentType()){
		case ChatComMsgEntity.TYPE_TEXT:
			TextMsgBody tb=(TextMsgBody) entity.getMsgBody();
			msg.setMsg_body(tb.getContent());
			break;
		case ChatComMsgEntity.TYPE_IMG:
			ImgMsgBody ib=(ImgMsgBody) entity.getMsgBody();
			msg.setLocal_url(ib.getLocalUrl());
			msg.setRemote_url(ib.getRemoteUrl());
			msg.setThumbnail_url(ib.getThumbnailUrl());
			break;
		case ChatComMsgEntity.TYPE_VOICE:
			VoiceMsgBody vb=(VoiceMsgBody) entity.getMsgBody();
			msg.setLocal_url(vb.getLocalUrl());
			msg.setRemote_url(vb.getRemoteUrl());
			msg.setListend(vb.isListen()?1:0);
			msg.setVoice_time(vb.getLength());
			break;
		
		}
		return msg;
		
	}
	/**
	 * 通过msgid查一条msg
	 * @param msgId
	 * @return
	 */
	public ChatComMsgEntity findMsgByUUID(String msgId){
		ChatComMsgEntity entity=null;
		List<LocalMsg>	list=ChatManager.getInstance().getMsgDao().findByCondition(DBHelper.TABLE_CHAT_MSG_ID + "=?", new String[]{msgId}, null);
		if(list!=null&&list.size()>0){
			
			LocalMsg  msg=list.get(0);
			entity=localMsgToEntity(msg);
			
		}
		
		return entity;
		
	}
	/**
	 * 通过msgid查本地id
	 * @param msgId
	 * @return
	 */
	public long  getMsgLocalId(String msgId){
		String sql="select _id from "+getTableName()+" where "+DBHelper.TABLE_CHAT_MSG_ID+"=?" ;
		Cursor cursor =	database.rawQuery(sql, new String[]{msgId});
		 cursor.moveToFirst();
		 long i=cursor.getLong(0);
		 cursor.close();
		 return i;
		
	}
	/**
	 * 根据条目数和本地id 查找更多msg
	 * @param entity
	 */
	public List<ChatComMsgEntity>  findMsgList(String userId,int pagerSize,long dbMsgId){
		String sql="select * from (select * from "+getTableName()+" where "+DBHelper.TABLE_CHAT_MSG_PARTICIPANT+"=? and _id < ?"+"order by _id desc limit "+pagerSize+" offset "+0+") order by _id asc";
		List<LocalMsg> result = null;
		List<ChatComMsgEntity> entitys=new ArrayList<ChatComMsgEntity>();
		Cursor cursor =	database.rawQuery(sql, new String[]{userId,dbMsgId+""});
		if (cursor != null) {
			result = new ArrayList<LocalMsg>();
			while (cursor.moveToNext()) {
				LocalMsg m = new LocalMsg();

				fillFields(cursor, m);

				result.add(m);

			}

			cursor.close();
		}
		if(result!=null){
			for(LocalMsg msg:result){
				entitys.add(localMsgToEntity(msg));
				
			}
			
			return entitys;
		}else{
			return null;
			
		}
		
	}
	public List<ChatComMsgEntity>  findMsgList2(String userId,int pager,int pagerSize){
		
		
		
		String sql="select * from (select * from "+getTableName()+" where "+DBHelper.TABLE_CHAT_MSG_PARTICIPANT+"=? "+"order by _id desc limit "+pagerSize+" offset "+pager+") order by _id asc";
		List<LocalMsg> result = null;
		List<ChatComMsgEntity> entitys=new ArrayList<ChatComMsgEntity>();
		Cursor cursor =	database.rawQuery(sql, new String[]{userId});
		if (cursor != null) {
			result = new ArrayList<LocalMsg>();
			while (cursor.moveToNext()) {
				LocalMsg m = new LocalMsg();

				fillFields(cursor, m);

				result.add(m);

			}

			cursor.close();
		}
		if(result!=null){
			for(LocalMsg msg:result){
				entitys.add(localMsgToEntity(msg));
				
			}
			
			return entitys;
		}else{
			return null;
			
		}
		
	}
	private ChatComMsgEntity localMsgToEntity(LocalMsg msg){
		//id.direct.type.body.time.status/
		ChatComMsgEntity entity=new ChatComMsgEntity();
		entity.setFrom(msg.getMsg_from());
		entity.setTo(msg.getMsg_to());
		entity.setMsgId(msg.getMsg_id());
		entity.setContentType(msg.getMsg_type());
		entity.setMsgTime(Long.valueOf(msg.getMsg_time()));
		entity.direct=msg.getSend()==0? ChatComMsgEntity.Direct.SEND: ChatComMsgEntity.Direct.RECEIVE;
		switch(msg.getStatus()){
		case LocalMsg.STATUS_CREATE:
			entity.status=ChatComMsgEntity.Status.CREATE;
			break;
		case LocalMsg.STATUS_INPROGRESS:
			entity.status=ChatComMsgEntity.Status.INPROGRESS;
			break;
		case LocalMsg.STATUS_SUCCESS:
			entity.status=ChatComMsgEntity.Status.SUCCESS;
			break;
		case LocalMsg.STATUS_FAIL:
			entity.status=ChatComMsgEntity.Status.FAIL;
			break;
		
		}
		switch(msg.getMsg_type()){
		case ChatComMsgEntity.TYPE_TEXT:
			TextMsgBody tBody=new TextMsgBody();
			tBody.setContent(msg.getMsg_body());
			entity.setMsgBody(tBody);
			break;
		case ChatComMsgEntity.TYPE_IMG:
			ImgMsgBody  iBody=new ImgMsgBody();
			iBody.setLocalUrl(msg.getLocal_url());
			iBody.setRemoteUrl(msg.getRemote_url());
			iBody.setThumbnailUrl(msg.getThumbnail_url());
			entity.setMsgBody(iBody);
			break;
		case ChatComMsgEntity.TYPE_VOICE:
			VoiceMsgBody vBody=new VoiceMsgBody();
			vBody.setLocalUrl(msg.getLocal_url());
			vBody.setRemoteUrl(msg.getRemote_url());
			vBody.setListen(msg.getListend()==0?false:true);
			vBody.setLength(msg.getVoice_time());
			entity.setMsgBody(vBody);
			break;
		
		}
		
		return entity;
		
		
	}
	
}
