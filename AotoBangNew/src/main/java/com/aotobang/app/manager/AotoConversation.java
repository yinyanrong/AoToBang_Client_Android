package com.aotobang.app.manager;

import java.util.ArrayList;
import java.util.List;

import com.aotobang.app.entity.ChatComMsgEntity;
import com.aotobang.app.entity.ChatComMsgEntity.Direct;
import com.aotobang.local.bean.Conversation;
import com.aotobang.local.dao.LocalMsgDao;
import com.aotobang.utils.LogUtil;

public class AotoConversation {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AotoConversation other = (AotoConversation) obj;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}
	private List<ChatComMsgEntity> msgs=new ArrayList<ChatComMsgEntity>();
	private int unreadMsgCount;
	private String avatar_remote;
	private LocalMsgDao dao=new LocalMsgDao();

	public String getAvatar_remote() {
		return avatar_remote;
	}

	public void setAvatar_remote(String avatar_remote) {
		this.avatar_remote = avatar_remote;
	}
	
	
	
	public AotoConversation(String userId) {
		this.userId=userId;
		//默认加载10条记录到内存
		msgs=dao.findMsgList2(userId, 0, 10);
		
	}
	
	public void setUnreadMsgCount(int unreadMsgCount) {
		this.unreadMsgCount = unreadMsgCount;
	}
	private String userId;
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	private String userNick;
	private String userName;
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		LogUtil.info(AotoConversation.class, userName);
		this.userName = userName;
	}

	public String getUserNick() {
		return userNick;
	}

	public void setUserNick(String userNick) {
		this.userNick = userNick;
	}

	public int getUnreadMsgCount() {
		return unreadMsgCount;
	}
	/**
	 * 获取内存消息数
	 * @return
	 */
	public int getCacheMessagesCount(){
		
		return msgs.size();
	}
	/**
	 * 获取本地消息数
	 * @return
	 */
	public long getAllMessagesCount(){
		
		return dao.getAllMsgCountByUserId(userId);
	}
	/**
	 * 清除内存消息
	 * @return
	 */
	public void clear(){
		msgs.clear();
		
		
	}
	public List<ChatComMsgEntity> loadMoreMsgFromDB(String msgId,int pagerSize){
		List<ChatComMsgEntity> moreList;
		if(msgId!=null){
		long localId=dao.getMsgLocalId(msgId);
		 moreList=dao.findMsgList(userId, pagerSize, localId);
		}
		else{
			 moreList=dao.findMsgList2(userId, 0, 10);
		}
		moreList.addAll(msgs);
		msgs=moreList;
		return msgs;
	}
	public ChatComMsgEntity getMsgById(String msgId){
		/*ChatComMsgEntity result=null;
		for(int i=0;i<msgs.size();i++){
			ChatComMsgEntity msg=msgs.get(i);
			if(msg.getMsgId().equals(msgId)){
				result=msg;
				
			}
			
		}*/
	
			
		
		return ChatManager.getInstance().getMsgDao().findMsgByUUID(msgId);
		
	}
	/**
	 * 删除一条消息
	 * @return
	 */
	public void removeMessage(String msgId){
		for(int i=0;i<msgs.size();i++){
			ChatComMsgEntity msg=msgs.get(i);
			if(msg.getMsgId().equals(msgId)){
				msgs.remove(msg);
				i--;
				
			}
			
		}
		ChatManager.getInstance().getMsgDao().deleteByUUID(msgId);
		
	}
	/**
	 * 添加一个消息
	 * @param message
	 */
	public void addMessage(ChatComMsgEntity message){
		msgs.add(message);
		if(message.direct==Direct.RECEIVE){
		unreadMsgCount++;
		ChatManager.getInstance().getConDao().updataByUUID(userId,unreadMsgCount);
		}
	}
	public ChatComMsgEntity getLastMessage(){
		
		return msgs.size()>0?msgs.get(msgs.size()-1):null;
	}
	public int getMsgCount(){
		return msgs.size();
	}
	public List<ChatComMsgEntity> getAllMessages(){
		return msgs;
		
	}
	public void resetUnreadMsgCount(){
		unreadMsgCount=0;
		ChatManager.getInstance().getConDao().updataByUUID(userId,unreadMsgCount);
		
	}
	
	
}
