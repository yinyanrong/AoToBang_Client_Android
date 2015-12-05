package com.aotobang.app.manager;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.Bitmap;

import com.alibaba.sdk.android.oss.callback.GetFileCallback;
import com.alibaba.sdk.android.oss.callback.SaveCallback;
import com.alibaba.sdk.android.oss.model.OSSException;
import com.alibaba.sdk.android.oss.storage.OSSData;
import com.alibaba.sdk.android.oss.storage.OSSFile;
import com.aotobang.app.applaction.AotoBangApp;
import com.aotobang.app.callback.ChatCallBack;
import com.aotobang.app.callback.InteractCallBack;
import com.aotobang.app.callback.RemoteLoginListener;
import com.aotobang.app.entity.ChatComMsgEntity;
import com.aotobang.app.entity.ChatMsgBody;
import com.aotobang.app.entity.FileMsgBody;
import com.aotobang.app.entity.ImgMsgBody;
import com.aotobang.app.entity.LikeEntity;
import com.aotobang.app.entity.TextMsgBody;
import com.aotobang.app.entity.VoiceMsgBody;
import com.aotobang.app.model.DefaultModelImp.PathType;
import com.aotobang.local.bean.Conversation;
import com.aotobang.local.bean.Photo;
import com.aotobang.local.dao.ConversationDao;
import com.aotobang.local.dao.FriendDao;
import com.aotobang.local.dao.LocalMsgDao;
import com.aotobang.local.dao.PhotoDao;
import com.aotobang.net.AotoConnect;
import com.aotobang.net.AotoNetEngineProxy;
import com.aotobang.net.InterfaceIds;
import com.aotobang.net.callback.IDataCallBack;
import com.aotobang.net.callback.INTMessageCallBack;
import com.aotobang.net.entity.AotoNotifiEvent;
import com.aotobang.net.entity.AotoRequest;
import com.aotobang.utils.ImageUtils;
import com.aotobang.utils.LogUtil;
import com.aotobang.utils.PathUtil;
import com.aotobang.utils.PictureUtil;
/*聊天管理类。负责发送。和接受消息。
 * 
 * 
 */
public class ChatManager {
	public static final String NEW_MESSAGE_ACTION="com.aotobang.app.NEWMESSAGE";
	public static final String NEW_FRIEND_ACTION="com.aotobang.app.NEWFRIEND";
	public static final int ERROR_OSS=-1;
	int type=-1;
	private RemoteLoginListener  remoteLoginListener;
	private InteractCallBack  interactCallBack;
	private InteractCallBack  interactApply;
	private LocalMsgDao msgDao;
	private ConversationDao conDao;
	private FriendDao friendDao;
	private PhotoDao  photoDao;
	private static ChatManager  instance;
	private Hashtable<String, AotoConversation> conversations=new Hashtable<String, AotoConversation>();
	public Hashtable<String, AotoConversation> getAllAotoConversations(){
		
		return conversations;
		
	}
	public void init(){



		initDao();
		loadConversations();
		registReceiveMsgListener();



	}
	public void loadConversations(){
		conversations.clear();
		//List<Conversation>  list=conDao.findAll();
		List<Conversation>  list=getConDao().queryAll();
		for(Conversation c:list){
			if(conversations.get(c.getId())==null){
			conversations.put(c.getUser_id(), getConDao().localConToAotoCon(c));
			}
		}
		
		
	}
	public FriendDao getFriendDao(){
		if(friendDao==null)
			friendDao=new FriendDao();
		return friendDao;
	}
	public LocalMsgDao getMsgDao(){
		if(msgDao==null)
			msgDao=new LocalMsgDao();
		return msgDao;
	}
	public ConversationDao getConDao(){
		if(conDao==null)
			conDao=new ConversationDao();
		return conDao;
	}

	public PhotoDao getPhotoDao(){
		if(photoDao==null)
			photoDao=new PhotoDao();
		return photoDao;
	}
	public AotoConversation getConversationByUUID(String userId){
		if(conversations.get(userId)==null){
			AotoConversation  con=	getConDao().findConversionById(userId);
			if(con!=null)
			conversations.put(userId, con);
			return con;
		}else{
			
			return conversations.get(userId);
		}
		
		
	}
	public int getAllUnreadMsgCount(){
		int i=0;
		for (Map.Entry<String, AotoConversation> entry : conversations.entrySet()) {
					AotoConversation con =entry.getValue();
					i+=con.getUnreadMsgCount();
			   
			  }
		
		return i;
		
	}
	public void addNewMessage(String userId,ChatComMsgEntity msg,boolean addCache){
		if(addCache){
		AotoConversation mAotoConversation=getConversationByUUID(userId);
		mAotoConversation.addMessage(msg);
		}
		LogUtil.info(ChatManager.class, "发送新消息广播");
		Intent intent = new Intent(); 
		intent.setAction(NEW_MESSAGE_ACTION);  
		intent.putExtra("msgid", msg.getMsgId());
		intent.putExtra("userid",msg.getFrom());
		AotoBangApp.mPreLoad.appContext.sendOrderedBroadcast(intent, null);  
		
	}
	private ChatManager(){
	
	}
	public void initDao(){
		msgDao=new LocalMsgDao();
		conDao=new ConversationDao();
		friendDao=new FriendDao();

		photoDao=new PhotoDao();
		if(photoDao.findAll().size()==0){
			for(int i=0;i<6;i++){
				Photo p=new Photo();
				p.setPhoto_index(i);
				photoDao.insert(p);
			}
		}



	}
	public  static  ChatManager getInstance(){
		if(instance==null){
			synchronized (ChatManager.class) {
				if(instance==null)
					instance=new ChatManager();
			}
			
		}
		return instance;
	}
	public void registRomoteLoginListener(RemoteLoginListener  remoteLoginListener){
		this.remoteLoginListener=remoteLoginListener;
		
	}
	public void registReceiveInteractListener(InteractCallBack interactCallBack){
		this.interactCallBack=interactCallBack;
		
		
	}
public void unRegistReceiveInteractListener(){
		interactCallBack=null;
		
		
	}

public void registInteractApplyListener(InteractCallBack interactCallBack){
	this.interactApply=interactCallBack;
	
	
}
public void unRegistInteractApplyListener(){
	interactApply=null;
	
	
}
	public  void registReceiveMsgListener(){
		AotoConnect.getInstance().setOnNTMessageCallBacklistener(new INTMessageCallBack() {
			@Override
			public void handleNTMessgae(AotoNotifiEvent event) {
				ChatComMsgEntity entity=ChatComMsgEntity.createReciveEntity(event.getChat_type());
				ChatMsgBody body=null;
				switch(event.getType()){
				case CHAT:
				
					entity.setMsgTime(System.currentTimeMillis());
					entity.setFrom(event.getFrom());
					entity.setTo(event.getTo());
				
					switch(event.getChat_type()){
					case ChatComMsgEntity.TYPE_TEXT:
						 body=new TextMsgBody();
						((TextMsgBody)body).setContent((String)event.getData());
						break;
					case ChatComMsgEntity.TYPE_FILE:
						break;
					case ChatComMsgEntity.TYPE_IMG:
						 body=new ImgMsgBody();
						 ((ImgMsgBody)body).setRemoteUrl((String)event.getData());
						break;
					case ChatComMsgEntity.TYPE_VOICE:
						body=new VoiceMsgBody();
						 ((VoiceMsgBody)body).setRemoteUrl((String)event.getData());
						 ((VoiceMsgBody)body).setLength(event.getVoice_time());
						break;
					
					case ChatComMsgEntity.TYPE_VIDEO:
						break;
					}
					entity.setMsgBody(body);
					msgDao.insertMsgEntity(entity);
					addNewMessage(entity.getFrom(), entity,true);
					break;
				case MATCH_LIKE:
					LikeEntity like=event.getLike();
					entity.setContentType(ChatComMsgEntity.TYPE_TEXT);
					entity.setMsgTime(System.currentTimeMillis());
					entity.setFrom(like.getFrom());
					entity.setTo(event.getTo());
					 body=new TextMsgBody();
					((TextMsgBody)body).setContent("我们现在可以聊天了");
					entity.setMsgBody(body);
					msgDao.insertMsgEntity(entity);
					AotoConversation  mAotoConversation=getConversationByUUID(like.getFrom());
					if(mAotoConversation==null){
					mAotoConversation=new AotoConversation(like.getFrom());
					mAotoConversation.setUserNick(like.getNick());
					mAotoConversation.setUnreadMsgCount(1);
					mAotoConversation.setUserId(like.getFrom());
					mAotoConversation.setAvatar_remote(like.getAvatar());
					mAotoConversation.setUserName(like.getUserName());
					conversations.put(like.getFrom(), mAotoConversation);
					Conversation local=new Conversation();
					local.setAvatar_url(like.getAvatar());
					local.setNickname(like.getNick());
					local.setUsername(like.getUserName());
					local.setUnread_count(1);
					local.setTime(like.getTime());
					local.setUser_id(like.getFrom());
					conDao.insert(local);
					addNewMessage(like.getFrom(), entity,false);
					}else{
						addNewMessage(like.getFrom(), entity,true);	
					}
					
					break;
				case Interact_Apply:
					if(interactApply!=null)
						interactApply.OnInteractMsg(event.getInteract());
					break;
				case INTERACT:
					if(interactCallBack!=null)
						interactCallBack.OnInteractMsg(event.getInteract());
					break;
				case REMOTE_LOGIN:
					if(remoteLoginListener!=null)
						remoteLoginListener.onRemoteLogin();
					break;
				
				}
				
				
				
				
				
			}
		});
		
		
	}
	public void downLoadFile(final ChatComMsgEntity message,final ChatCallBack callback){
		message.status=ChatComMsgEntity.Status.INPROGRESS;
		final FileMsgBody body=(FileMsgBody) message.getMsgBody();
		String savePath=null;
		switch(message.getContentType()){
		case ChatComMsgEntity.TYPE_IMG:
			savePath=AotoBangApp.getInstance().getCachePath(PathType.Img)+PathUtil.getFileName(body.getRemoteUrl());
			break;
		case ChatComMsgEntity.TYPE_VOICE:
			savePath=AotoBangApp.getInstance().getCachePath(PathType.Audio)+PathUtil.getFileName(body.getRemoteUrl());
			break;
		/*case ChatComMsgEntity.TYPE_FILE:
			savePath=GlobalParams.CACHE_FILE+PathUtil.getFileName(body.getRemoteUrl());
			break;*/
		case ChatComMsgEntity.TYPE_VIDEO:
			savePath=AotoBangApp.getInstance().getCachePath(PathType.Video)+PathUtil.getFileName(body.getRemoteUrl());
			break;
		
		}
		
		
		OSSFile file=OssManager.getInstance().downloadFile(body.getRemoteUrl());
		LogUtil.info(ChatManager.class, "远程路径---"+body.getRemoteUrl());
		LogUtil.info(ChatManager.class, "本地路径---"+savePath);
		file.downloadToInBackground(savePath, new GetFileCallback() {
			
			@Override
			public void onProgress(String objectKey, int byteCount, int totalSize) {
				callback.onProgress(byteCount, totalSize);
			}
			
			@Override
			public void onFailure(String objectKey, OSSException ossException) {
				message.status=ChatComMsgEntity.Status.FAIL;
				msgDao.updateMsgStatus(message);
				callback.onError(0, objectKey+ossException.getMessage());
			}
			
			@Override
			public void onSuccess(String objectKey, String filePath) {
				message.status=ChatComMsgEntity.Status.SUCCESS;
				LogUtil.info(ChatManager.class, objectKey+"---"+filePath);
				body.setLocalUrl(filePath);
				msgDao.updateMsgStatus(message);
				callback.onSuccess();
			}
		});
		
		
		
	}
	public  void sendMessage(final ChatComMsgEntity message,final ChatCallBack callback){
		msgDao.insertMsgEntity(message);
		message.status=ChatComMsgEntity.Status.INPROGRESS;
		if(message.getContentType()==ChatComMsgEntity.TYPE_TEXT){
			TextMsgBody body=(TextMsgBody) message.getMsgBody();
			AotoRequest request =AotoRequest.createRequest(InterfaceIds.CHART);
			Map<String,Object> map=new HashMap<String, Object>();
			map.put("content", body.getContent());
			map.put("to", message.getTo());
			map.put("sessionId", AotoBangApp.getInstance().getSessionId());
			request.setParameters(map);
      		request.setCallBack(new IDataCallBack() {
				
				@Override
				public void handleDataResultOnSuccee(int responseId, Object data) {
					message.status=ChatComMsgEntity.Status.SUCCESS;
					msgDao.updateMsgStatus(message);
					callback.onSuccess();
					
				}
				
				@Override
				public void handleDataResultOnError(int responseId, int errCode, Object error) {
					message.status=ChatComMsgEntity.Status.FAIL;
					msgDao.updateMsgStatus(message);
					callback.onError(errCode, (String)error);
					
				}
			});
			AotoNetEngineProxy.getInstance(AotoBangApp.mPreLoad.appContext).sendReqeust(request);
			return;
		}else if(message.getContentType()==ChatComMsgEntity.TYPE_IMG ){
			final ImgMsgBody body=(ImgMsgBody) message.getMsgBody();
			File file=new File(body.getLocalUrl());
			long length=file.length();
			if(length>1024*100&&!body.isSendOriginalImage()){
			final Bitmap bm=PictureUtil.getSmallBitmap(body.getLocalUrl());
			new Thread(){
				public void run() {
					OSSData data=OssManager.getInstance().uploadData(AotoBangApp.getInstance().getUserId(), "image",body.getFileName(), ImageUtils.bitmap2Bytes(bm,true));
					data.uploadInBackground(new SaveCallback() {
					    @Override
					    public void onSuccess( String objectKey) {
					    	body.setRemoteUrl(objectKey);
					    	LogUtil.info(ChatManager.class, objectKey);
					    	message.status=ChatComMsgEntity.Status.SUCCESS;
					    	callback.onSuccess();
					    	AotoRequest request =AotoRequest.createRequest(InterfaceIds.UPLOAD_FILE_COMPLETE);
							Map<String,Object> map=new HashMap<String, Object>();
							map.put("type", 0);
							map.put("vTime", 0);
							map.put("photoArr", new ArrayList<Object>());
							map.put("url", objectKey);
							map.put("to", message.getTo());
							map.put("sessionId", AotoBangApp.getInstance().getSessionId());
							request.setParameters(map);
							request.setCallBack(new IDataCallBack() {
								
								@Override
								public void handleDataResultOnSuccee(int responseId, Object data) {
									
									msgDao.updateMsgStatus(message);
								}
								
								@Override
								public void handleDataResultOnError(int responseId, int errCode, Object error) {
									callback.onError(errCode, (String)error);
									message.status=ChatComMsgEntity.Status.FAIL;
									msgDao.updateMsgStatus(message);
									
								}
							});
							AotoNetEngineProxy.getInstance(AotoBangApp.mPreLoad.appContext).sendReqeust(request);
					    }

					    @Override
					    public void onProgress(String objectKey, int byteCount, int totalSize) {
					    	callback.onProgress(byteCount, totalSize);
					    }

					    @Override
					    public void onFailure(String objectKey, OSSException ossException) {
					    	message.status=ChatComMsgEntity.Status.FAIL;
					    	callback.onError(ERROR_OSS, ossException.getMessage());

					    }
					});
				};
				
			}.start();
			
			return;
			}
			
		}
		
		String dirType="unkonw";
		switch (message.getContentType()) {
		case ChatComMsgEntity.TYPE_IMG:
			dirType="image";
			type=0;
			break;
		case ChatComMsgEntity.TYPE_FILE:
			dirType="file";
			type=3;
			break;
		case ChatComMsgEntity.TYPE_VOICE:
			dirType="audio";
			type=1;
			break;
		case ChatComMsgEntity.TYPE_VIDEO:
			dirType="video";
			type=2;
			break;
		default:
			break;
		}
		final FileMsgBody body=(FileMsgBody) message.getMsgBody();
		OSSFile uploadFile =OssManager.getInstance().uploadFile(AotoBangApp.getInstance().getUserId(),dirType,body.getFileName(),body.getLocalUrl());
		uploadFile.uploadInBackground(new SaveCallback() {
		    @Override
		    public void onSuccess(String objectKey) {
		    	body.setRemoteUrl(objectKey);
		    	message.status=ChatComMsgEntity.Status.SUCCESS;
		    	callback.onSuccess();
		    	AotoRequest request =AotoRequest.createRequest(InterfaceIds.UPLOAD_FILE_COMPLETE);
				Map<String,Object> map=new HashMap<String, Object>();
				if(message.getContentType()==ChatComMsgEntity.TYPE_VOICE){
					VoiceMsgBody body=(VoiceMsgBody) message.getMsgBody();
					map.put("vTime",body.getLength() );
					
				}else{
					map.put("vTime", 0);
				}
				LogUtil.info(ChatManager.class, objectKey);
				map.put("type", type);
				map.put("url",objectKey);
				map.put("to", message.getTo());
				map.put("photoArr", new ArrayList<Object>());
				map.put("sessionId",AotoBangApp.getInstance().getSessionId());
				request.setParameters(map);
				request.setCallBack(new IDataCallBack() {
					
					@Override
					public void handleDataResultOnSuccee(int responseId, Object data) {
							msgDao.updateMsgStatus(message);
					}
					
					@Override
					public void handleDataResultOnError(int responseId, int errCode, Object error) {
						callback.onError(errCode, (String)error);
							msgDao.updateMsgStatus(message);
						
					}
				});
				AotoNetEngineProxy.getInstance(AotoBangApp.mPreLoad.appContext).sendReqeust(request);
		    	
		    	
		    	
		    }

		    @Override
		    public void onProgress(String objectKey, int byteCount, int totalSize) {
		    	callback.onProgress(byteCount, totalSize);
		    }

		    @Override
		    public void onFailure(String objectKey, OSSException ossException) {
		    	message.status=ChatComMsgEntity.Status.FAIL;
		    	callback.onError(ERROR_OSS, ossException.getMessage());

		    }
		});
		
		
		
	}
	
	
}
