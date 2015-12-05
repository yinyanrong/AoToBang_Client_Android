
package com.aotobang.app.entity;

import java.util.UUID;

/**
 * 建立时需要指定类型和direct
 * @author Admin
 *
 */
public class ChatComMsgEntity {
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((msgId == null) ? 0 : msgId.hashCode());
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
		ChatComMsgEntity other = (ChatComMsgEntity) obj;
		if (msgId == null) {
			if (other.msgId != null)
				return false;
		} else if (!msgId.equals(other.msgId))
			return false;
		return true;
	}


	private static final String TAG = ChatComMsgEntity.class.getSimpleName();
    public static final int TYPE_IMG=0;
    public static final int TYPE_VOICE=1;
    public static final int TYPE_VIDEO=2;
    public static final int TYPE_FILE=3;
    public static final int TYPE_TEXT=4;
    public static final int TYPE_LOCATION=5;
   
    private long msgTime;


	public long getMsgTime() {
		return msgTime;
	}

	public void setMsgTime(long msgTime) {
		this.msgTime = msgTime;
	}


	private String from;
    private String to;

    private ChatMsgBody MsgBody;
  
	public ChatMsgBody getMsgBody() {
		return MsgBody;
	}

	public void setMsgBody(ChatMsgBody msgBody) {
		MsgBody = msgBody;
	}


	public Status status=Status.CREATE;
	public Direct direct;
	//对接收到的消息，标示系统是否发送了消息回执 对发送的消息，表示系统是否收到对方的已读回执
	public boolean isAcked=false;
	//对接收到的消息，标示系统是否发送了送达回执 对发送的消息，表示系统是否收到对方的送达回执
	public boolean isDelivered=false;
	//语音图片等消息的下载进展，用于UI 展示
	public int progress;
	private int contentType;
	
    private String msgId;//id 创建时的uuid

    public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public int getContentType() {
		return contentType;
	}

	public void setContentType(int contentType) {
		this.contentType = contentType;
	}
	public static ChatComMsgEntity createSendEntity(int contentType){
		ChatComMsgEntity entity=new ChatComMsgEntity();
		entity.setContentType(contentType);
		entity.setMsgId(UUID.randomUUID().toString());
		entity.direct=Direct.SEND;
		return entity;
	}
	public static ChatComMsgEntity createReciveEntity(int contentType){
		ChatComMsgEntity entity=new ChatComMsgEntity();
		entity.setContentType(contentType);
		entity.direct=Direct.RECEIVE;
		entity.setMsgId(UUID.randomUUID().toString());
		return entity;
	}

	 public String getFrom() {
			return from;
		}

		public void setFrom(String from) {
			this.from = from;
		}

		public String getTo() {
			return to;
		}

		public void setTo(String to) {
			this.to = to;
		}



    public ChatComMsgEntity() {
    	
    	
    }
    public static enum Direct {
    	SEND,
    	RECEIVE;
    	
    }
    public static enum Status {
    	CREATE,
    	INPROGRESS, 
    	FAIL,
    	SUCCESS;
    }
}
