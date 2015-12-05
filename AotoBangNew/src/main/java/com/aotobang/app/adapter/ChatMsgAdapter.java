package com.aotobang.app.adapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Handler;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.aotobang.app.GlobalParams;
import com.aotobang.app.R;
import com.aotobang.app.activity.ChatActivity;
import com.aotobang.app.activity.ShowBigImgActivity;
import com.aotobang.app.applaction.AotoBangApp;
import com.aotobang.app.asyntask.LoadAvatarTask;
import com.aotobang.app.asyntask.LoadLocalImgTask;
import com.aotobang.app.callback.ChatCallBack;
import com.aotobang.app.entity.ChatComMsgEntity;
import com.aotobang.app.entity.ChatComMsgEntity.Direct;
import com.aotobang.app.entity.ImgMsgBody;
import com.aotobang.app.entity.TextMsgBody;
import com.aotobang.app.entity.VoiceMsgBody;
import com.aotobang.app.manager.AotoConversation;
import com.aotobang.app.manager.ChatManager;
import com.aotobang.app.model.DefaultModelImp.PathType;
import com.aotobang.app.view.listener.VoicePlayClickListener;
import com.aotobang.utils.DateUtils;
import com.aotobang.utils.ImageCache;
import com.aotobang.utils.ImageUtils;
import com.aotobang.utils.LogUtil;
import com.aotobang.utils.SmileUtils;
import com.aotobang.utils.UserUtils;

public class ChatMsgAdapter extends BaseAdapter {
	private static final int MESSAGE_TYPE_RECV_TXT = 0;
	private static final int MESSAGE_TYPE_SENT_TXT = 1;
	private static final int MESSAGE_TYPE_SENT_IMAGE = 2;
	private static final int MESSAGE_TYPE_SENT_LOCATION = 3;
	private static final int MESSAGE_TYPE_RECV_LOCATION = 4;
	private static final int MESSAGE_TYPE_RECV_IMAGE = 5;
	private static final int MESSAGE_TYPE_SENT_VOICE = 6;
	private static final int MESSAGE_TYPE_RECV_VOICE = 7;
	private static final int MESSAGE_TYPE_SENT_VIDEO = 8;
	private static final int MESSAGE_TYPE_RECV_VIDEO = 9;
	private static final int MESSAGE_TYPE_SENT_FILE = 10;
	private static final int MESSAGE_TYPE_RECV_FILE = 11;
	private Activity activity;
	private Context ctx;
	//private List<ChatComMsgEntity> data;
	private LayoutInflater mInflater;
	private ChatComMsgEntity[] msgEntitys;
	private AotoConversation conversation;
	
	private static final int HANDLER_MESSAGE_REFRESH_LIST = 0;
	private static final int HANDLER_MESSAGE_SELECT_LAST = 1;
	private static final int HANDLER_MESSAGE_SEEK_TO = 2;
	public ChatMsgAdapter(Context context, List<ChatComMsgEntity> data,String userId) {
		ctx = context;
		activity=(Activity)ctx;
	//	this.data = data;
		this.conversation=ChatManager.getInstance().getConversationByUUID(userId);
		mInflater = LayoutInflater.from(context);
	}
	Handler handler = new Handler() {
		private void refreshList() {
			// UI线程不能直接使用conversation.getAllMessages()
			// 否则在UI刷新过程中，如果收到新的消息，会导致并发问题
			msgEntitys = (ChatComMsgEntity[]) conversation.getAllMessages().toArray(new ChatComMsgEntity[conversation.getAllMessages().size()]);
		/*	for (int i = 0; i < msgEntitys.length; i++) {
				// getMessage will set message as read status
				conversation.getMessage(i);
			}*/
			notifyDataSetChanged();
		}
		
		@Override
		public void handleMessage(android.os.Message message) {
			switch (message.what) {
			case HANDLER_MESSAGE_REFRESH_LIST:
				refreshList();
				break;
			case HANDLER_MESSAGE_SELECT_LAST:
				if (activity instanceof ChatActivity) {
					ListView listView = ((ChatActivity)activity).getListView();
					if (msgEntitys.length > 0) {
						listView.setSelection(msgEntitys.length - 1);
					}
				}
				break;
			case HANDLER_MESSAGE_SEEK_TO:
				int position = message.arg1;
				if (activity instanceof ChatActivity) {
					ListView listView = ((ChatActivity)activity).getListView();
					listView.setSelection(position);
				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	public int getCount() {

		return msgEntitys == null ? 0 : msgEntitys.length;
	}

	@Override
	public Object getItem(int position) {

		if (msgEntitys != null && position < msgEntitys.length) {
			return msgEntitys[position];
		}
		return null;
	}

	@Override
	public long getItemId(int position) {

		return position;
	}
	/**
	 * 获取item类型数
	 */
	public int getViewTypeCount() {
        return 12;
    }
	/**
	 * 获取item类型
	 */
	public int getItemViewType(int position) {
		ChatComMsgEntity message = (ChatComMsgEntity) getItem(position); 
		if (message == null) {
			return -1;
		}
		if (message.getContentType() == ChatComMsgEntity.TYPE_TEXT) {
			/*
			if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false))
			    return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE_CALL : MESSAGE_TYPE_SENT_VOICE_CALL;
			else if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false))
			    return message.direct == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO_CALL : MESSAGE_TYPE_SENT_VIDEO_CALL;*/
			return message.direct== ChatComMsgEntity.Direct.RECEIVE? MESSAGE_TYPE_RECV_TXT : MESSAGE_TYPE_SENT_TXT;
		}
		if (message.getContentType() == ChatComMsgEntity.TYPE_IMG) {
			return  message.direct== ChatComMsgEntity.Direct.RECEIVE  ? MESSAGE_TYPE_RECV_IMAGE : MESSAGE_TYPE_SENT_IMAGE;

		}
		if (message.getContentType() == ChatComMsgEntity.TYPE_LOCATION) {
			return  message.direct== ChatComMsgEntity.Direct.RECEIVE  ? MESSAGE_TYPE_RECV_LOCATION : MESSAGE_TYPE_SENT_LOCATION;
		}
		if (message.getContentType() == ChatComMsgEntity.TYPE_VOICE) {
			return  message.direct== ChatComMsgEntity.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE : MESSAGE_TYPE_SENT_VOICE;
		}
		if (message.getContentType() == ChatComMsgEntity.TYPE_VIDEO) {
			return  message.direct== ChatComMsgEntity.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO : MESSAGE_TYPE_SENT_VIDEO;
		}
		if (message.getContentType() == ChatComMsgEntity.TYPE_FILE) {
			return  message.direct== ChatComMsgEntity.Direct.RECEIVE  ? MESSAGE_TYPE_RECV_FILE : MESSAGE_TYPE_SENT_FILE;
		}

		return -1;// invalid
	}
	private View createViewByMessage(ChatComMsgEntity message, int position) {
		
		switch (message.getContentType()) {
		case ChatComMsgEntity.TYPE_LOCATION:
			return  message.direct== ChatComMsgEntity.Direct.RECEIVE? mInflater.inflate(R.layout.row_received_location, null) : mInflater.inflate(
					R.layout.row_sent_location, null);
		case ChatComMsgEntity.TYPE_IMG:
			return  message.direct== ChatComMsgEntity.Direct.RECEIVE  ? mInflater.inflate(R.layout.row_received_picture, null) : mInflater.inflate(
					R.layout.row_sent_picture, null);
		case ChatComMsgEntity.TYPE_VOICE:
			return  message.direct== ChatComMsgEntity.Direct.RECEIVE ? mInflater.inflate(R.layout.row_received_voice, null) : mInflater.inflate(
					R.layout.row_sent_voice, null);
		case ChatComMsgEntity.TYPE_VIDEO:
			return  message.direct== ChatComMsgEntity.Direct.RECEIVE ? mInflater.inflate(R.layout.row_received_video, null) : mInflater.inflate(
					R.layout.row_sent_video, null);
		case ChatComMsgEntity.TYPE_FILE:
			return  message.direct== ChatComMsgEntity.Direct.RECEIVE ? mInflater.inflate(R.layout.row_received_file, null) : mInflater.inflate(
					R.layout.row_sent_file, null);
		default:
			return  message.direct== ChatComMsgEntity.Direct.RECEIVE  ? mInflater.inflate(R.layout.row_received_message, null) : mInflater.inflate(
					R.layout.row_sent_message, null);
			/*// 语音通话
			if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false))
				return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.row_received_voice_call, null) : inflater
						.inflate(R.layout.row_sent_voice_call, null);
			// 视频通话
			else if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false))
			    return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.row_received_video_call, null) : inflater
                        .inflate(R.layout.row_sent_video_call, null);
			return message.direct == EMMessage.Direct.RECEIVE ? inflater.inflate(R.layout.row_received_message, null) : inflater.inflate(
					R.layout.row_sent_message, null);*/
		}
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ChatComMsgEntity message=(ChatComMsgEntity) getItem(position); 
		int contentType=message.getContentType();
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = createViewByMessage(message, position);
			if (contentType == ChatComMsgEntity.TYPE_IMG) {
					holder.iv = ((ImageView) convertView.findViewById(R.id.iv_sendPicture));
					holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_userhead);
					holder.tv = (TextView) convertView.findViewById(R.id.percentage);
					holder.pb = (ProgressBar) convertView.findViewById(R.id.progressBar);
					holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
					holder.tv_usernick = (TextView) convertView.findViewById(R.id.tv_userid);

			} else if (contentType == ChatComMsgEntity.TYPE_TEXT) {

					holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
					holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
					holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_userhead);
					// 这里是文字内容
					holder.tv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
					holder.tv_usernick = (TextView) convertView.findViewById(R.id.tv_userid);

				/*// 语音通话及视频通话
				if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)
				        || message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
					holder.iv = (ImageView) convertView.findViewById(R.id.iv_call_icon);
					holder.tv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
				}*/

			} else if (contentType == ChatComMsgEntity.TYPE_VOICE) {
					holder.frm=(FrameLayout) convertView.findViewById(R.id.frm_voice);
					holder.iv = ((ImageView) convertView.findViewById(R.id.iv_voice));
					holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_userhead);
					holder.tv = (TextView) convertView.findViewById(R.id.tv_length);
					holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
					holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
					holder.tv_usernick = (TextView) convertView.findViewById(R.id.tv_userid);
					holder.iv_read_status = (ImageView) convertView.findViewById(R.id.iv_unread_voice);
			} else if (contentType == ChatComMsgEntity.TYPE_LOCATION) {
					holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_userhead);
					holder.tv = (TextView) convertView.findViewById(R.id.tv_location);
					holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
					holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
					holder.tv_usernick = (TextView) convertView.findViewById(R.id.tv_userid);
			} else if (contentType == ChatComMsgEntity.TYPE_VIDEO) {
					holder.iv = ((ImageView) convertView.findViewById(R.id.chatting_content_iv));
					holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_userhead);
					holder.tv = (TextView) convertView.findViewById(R.id.percentage);
					holder.pb = (ProgressBar) convertView.findViewById(R.id.progressBar);
					holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
					holder.size = (TextView) convertView.findViewById(R.id.chatting_size_iv);
					holder.timeLength = (TextView) convertView.findViewById(R.id.chatting_length_iv);
					holder.playBtn = (ImageView) convertView.findViewById(R.id.chatting_status_btn);
					holder.container_status_btn = (LinearLayout) convertView.findViewById(R.id.container_status_btn);
					holder.tv_usernick = (TextView) convertView.findViewById(R.id.tv_userid);
			} else if (contentType == ChatComMsgEntity.TYPE_FILE) {
					holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_userhead);
					holder.tv_file_name = (TextView) convertView.findViewById(R.id.tv_file_name);
					holder.tv_file_size = (TextView) convertView.findViewById(R.id.tv_file_size);
					holder.pb = (ProgressBar) convertView.findViewById(R.id.pb_sending);
					holder.staus_iv = (ImageView) convertView.findViewById(R.id.msg_status);
					holder.tv_file_download_state = (TextView) convertView.findViewById(R.id.tv_file_state);
					holder.ll_container = (LinearLayout) convertView.findViewById(R.id.ll_file_container);
					// 这里是进度值
					holder.tv = (TextView) convertView.findViewById(R.id.percentage);
					holder.tv_usernick = (TextView) convertView.findViewById(R.id.tv_userid);
			
			}

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(holder.iv_avatar.getTag()==null||!holder.iv_avatar.getTag().equals("set")){
		//设置用户头像
		setUserAvatar(message, holder.iv_avatar);
		}
		switch (contentType) {
		case ChatComMsgEntity.TYPE_IMG:
			handleImageMessage(message, holder, position, convertView);
			break;
		case ChatComMsgEntity.TYPE_TEXT:
			   handleTextMessage(message, holder, position);
			break;
		case ChatComMsgEntity.TYPE_VOICE:
			handleVoiceMessage(message, holder, position, convertView);
			break;
		case ChatComMsgEntity.TYPE_LOCATION:
			
			break;
		case ChatComMsgEntity.TYPE_VIDEO:
			
			break;
		case ChatComMsgEntity.TYPE_FILE:
			
			break;
		default:
			break;
		}
		/*TextView timestamp = (TextView) convertView.findViewById(R.id.timestamp);
		if(message.getMsgTime()!=0)
		timestamp.setText(message.getMsgTime()+"");
		*/
		
		TextView timestamp = (TextView) convertView.findViewById(R.id.timestamp);

		if (position == 0) {
			timestamp.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(message.getMsgTime())));
			timestamp.setVisibility(View.VISIBLE);
		} else {
			// 两条消息时间离得如果稍长，显示时间
			ChatComMsgEntity prevMessage = (ChatComMsgEntity) getItem(position - 1);
			if (prevMessage != null && DateUtils.isCloseEnough(message.getMsgTime(), prevMessage.getMsgTime())) {
				timestamp.setVisibility(View.GONE);
			} else {
				timestamp.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(message.getMsgTime())));
				timestamp.setVisibility(View.VISIBLE);
			}
		}
		
		
		return convertView;
	}
	private void handleVoiceMessage(ChatComMsgEntity message, final ViewHolder holder, int position, View convertView) {
		VoiceMsgBody body=(VoiceMsgBody) message.getMsgBody();
		holder.tv.setText(body.getLength() + "\"");
		if(holder.iv!=null)
		holder.frm.setOnClickListener(new VoicePlayClickListener(message, holder.iv, holder.iv_read_status, this, (Activity)ctx));
		
		if (((ChatActivity)ctx).playMsgId != null
				&& ((ChatActivity)ctx).playMsgId.equals(message.getMsgId())&&VoicePlayClickListener.isPlaying) {
			AnimationDrawable voiceAnimation;
			if ( message.direct== ChatComMsgEntity.Direct.RECEIVE) {
				holder.iv.setImageResource(R.anim.voice_from_icon);
			} else {
				holder.iv.setImageResource(R.anim.voice_to_icon);
			}
			voiceAnimation = (AnimationDrawable) holder.iv.getDrawable();
			voiceAnimation.start();
		} else {
			if ( message.direct== ChatComMsgEntity.Direct.RECEIVE) {
				holder.iv.setImageResource(R.drawable.left_voice3);
			} else {
				holder.iv.setImageResource(R.drawable.right_voice3);
			}
		}
		
		
		if (message.direct== ChatComMsgEntity.Direct.RECEIVE) {
			if (body.isListen()) {
				// 隐藏语音未听标志
				holder.iv_read_status.setVisibility(View.INVISIBLE);
			} else {
				holder.iv_read_status.setVisibility(View.VISIBLE);
			}
			System.err.println("it is receive msg");
			if (message.status == ChatComMsgEntity.Status.CREATE) {
				holder.pb.setVisibility(View.VISIBLE);
				System.err.println("!!!! back receive");
				ChatManager.getInstance().downLoadFile(message, new ChatCallBack() {
					
					@Override
					public void onSuccess() {
						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								holder.pb.setVisibility(View.INVISIBLE);
								notifyDataSetChanged();
							}
						});
					}
					
					@Override
					public void onProgress(int progress, int totalSize) {
					}
					
					@Override
					public void onError(int code, String error) {
						LogUtil.info(getClass(), error);
						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								holder.pb.setVisibility(View.INVISIBLE);
							}
						});
					}

					@Override
					public void onSuccess(String objectKey) {
					}
				});
			} else {
				holder.pb.setVisibility(View.INVISIBLE);

			}
			return;
		}

		// until here, deal with send voice msg
		switch (message.status) {
		case SUCCESS:
			holder.pb.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.GONE);
			break;
		case FAIL:
			holder.pb.setVisibility(View.GONE);
			holder.staus_iv.setVisibility(View.VISIBLE);
			break;
		case INPROGRESS:
			holder.pb.setVisibility(View.VISIBLE);
			holder.staus_iv.setVisibility(View.GONE);
			break;
		default:
			sendMsgInBackground(message, holder);
		}
	}

	private void handleTextMessage(ChatComMsgEntity message, ViewHolder holder, int position) {
		TextMsgBody body=(TextMsgBody) message.getMsgBody();
		Spannable span = SmileUtils.getSmiledText(ctx, body.getContent());
		// 设置内容
		holder.tv.setText(span, BufferType.SPANNABLE);
		holder.tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		
		if ( message.direct== ChatComMsgEntity.Direct.SEND) {
			switch (message.status) {
			case SUCCESS: // 发送成功
				holder.pb.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.GONE);
				break;
			case FAIL: // 发送失败
				holder.pb.setVisibility(View.GONE);
				holder.staus_iv.setVisibility(View.VISIBLE);
				break;
			case INPROGRESS: // 发送中
				holder.pb.setVisibility(View.VISIBLE);
				holder.staus_iv.setVisibility(View.GONE);
				break;
			default:
				// 发送消息
				sendMsgInBackground(message, holder);
			}
		}
		
	}

	private void sendMsgInBackground(final ChatComMsgEntity message, final ViewHolder holder) {
		holder.staus_iv.setVisibility(View.GONE);
		holder.pb.setVisibility(View.VISIBLE);
				ChatManager.getInstance().sendMessage(message, new ChatCallBack() {
					
					@Override
					public void onSuccess() {
						updateSendedView(message, holder);
					}
					
					@Override
					public void onProgress(int progress, int totalSize) {
					}
					
					@Override
					public void onError(int code, String error) {
						updateSendedView(message, holder);
						LogUtil.info(ChatMsgAdapter.class, error);
					}

					@Override
					public void onSuccess(String objectKey) {
					}
				});
				
		
		
	}

	private void handleImageMessage(final ChatComMsgEntity message, final ViewHolder holder, int position, View convertView) {
		holder.pb.setTag(position);
		holder.iv.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Toast.makeText(ctx, "OnLongClick", Toast.LENGTH_SHORT).show();
			/*	ctx.startActivityForResult(
						(new Intent(ctx, ContextMenu.class)).putExtra("position", position).putExtra("type",
											EMMessage.Type.IMAGE.ordinal()), ChatActivity.REQUEST_CODE_CONTEXT_MENU);*/
				return true;
			}
		});
		if( message.direct== ChatComMsgEntity.Direct.RECEIVE){
			if (message.status == ChatComMsgEntity.Status.CREATE) {
				// "!!!! back receive";
				holder.iv.setImageResource(R.drawable.default_image);
				showDownloadImageProgress(message, holder);
			} else {
				// "!!!! not back receive, show image directly");
				holder.pb.setVisibility(View.GONE);
				holder.tv.setVisibility(View.GONE);
				holder.iv.setImageResource(R.drawable.default_image);
				ImgMsgBody imgBody = (ImgMsgBody) message.getMsgBody();
				if (imgBody.getLocalUrl() != null) {
					 String filePath = imgBody.getLocalUrl();
					String remotePath = imgBody.getRemoteUrl();
					/*String filePath = ImageUtils.getImagePath(remotePath);
					String thumbRemoteUrl = imgBody.getThumbnailUrl();*/
					String thumbnailPath = ImageUtils.getThumbnailImagePath(filePath);
					showImageView(thumbnailPath, holder.iv, filePath, remotePath, message);
				}
			}
			return;
		}
				// 发送的消息
				// process send message
				// send pic, show the pic directly
				ImgMsgBody imgBody = (ImgMsgBody) message.getMsgBody();
				String filePath = imgBody.getLocalUrl();
				if (filePath != null && new File(filePath).exists()) {
					showImageView(ImageUtils.getThumbnailImagePath(filePath), holder.iv, filePath, null, message);
				} else {
					showImageView(ImageUtils.getThumbnailImagePath(filePath), holder.iv, filePath, AotoBangApp.getInstance().getCachePath(PathType.Img), message);
				}
				
				switch (message.status) {
				case SUCCESS:
					holder.pb.setVisibility(View.GONE);
					holder.tv.setVisibility(View.GONE);
					holder.staus_iv.setVisibility(View.GONE);
					break;
				case FAIL:
					holder.pb.setVisibility(View.GONE);
					holder.tv.setVisibility(View.GONE);
					holder.staus_iv.setVisibility(View.VISIBLE);
					break;
				case INPROGRESS:
					holder.staus_iv.setVisibility(View.GONE);
					holder.pb.setVisibility(View.VISIBLE);
					holder.tv.setVisibility(View.VISIBLE);
					break;
				default:
					sendPictureMessage(message, holder);
				}		
		
	}

private void sendPictureMessage(ChatComMsgEntity message, final ViewHolder holder) {
	
	try {

		// before send, update ui
		holder.staus_iv.setVisibility(View.GONE);
		holder.pb.setVisibility(View.VISIBLE);
		holder.tv.setVisibility(View.VISIBLE);
		holder.tv.setText("0%");
		
		final long start = System.currentTimeMillis();
		ChatManager.getInstance().sendMessage(message, new ChatCallBack() {

			@Override
			public void onSuccess() {
				activity.runOnUiThread(new Runnable() {
					public void run() {
						// send success
						holder.pb.setVisibility(View.GONE);
						holder.tv.setVisibility(View.GONE);
					}
				});
			}

			@Override
			public void onError(int code, String error) {
				
				activity.runOnUiThread(new Runnable() {
					public void run() {
						holder.pb.setVisibility(View.GONE);
						holder.tv.setVisibility(View.GONE);
						holder.staus_iv.setVisibility(View.VISIBLE);
						Toast.makeText(activity,
								activity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast), 0).show();
					}
				});
			}

			@Override
			public void onProgress(final int progress, final int  totalSize) {
				activity.runOnUiThread(new Runnable() {
					public void run() {
						float  a=((float)progress/(float)totalSize);
						  int  b=(int)(Math.round(a*100));
						holder.tv.setText(b  + "%");
					}
				});
			}

			@Override
			public void onSuccess(String objectKey) {
			}

		});
	} catch (Exception e) {
		e.printStackTrace();
	}
	
	
	}

private void showImageView(String thumbnailImagePath, ImageView iv, final String localFullSizePath, final String remotePath, final ChatComMsgEntity message) {
		Bitmap bitmap = ImageCache.getInstance().get(thumbnailImagePath);
		if(bitmap!=null){
			iv.setImageBitmap(bitmap);
			iv.setClickable(true); 
			iv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					System.err.println("image view on click");
					Intent intent = new Intent(ctx, ShowBigImgActivity.class);
					File file = new File(localFullSizePath);
					if (file.exists()) {
						Uri uri = Uri.fromFile(file);
						intent.putExtra("uri", uri);
						System.err.println("here need to check why download everytime");
					} else {
						// The local full size pic does not exist yet.
						// ShowBigImage needs to download it from the server
						// first
						// intent.putExtra("", message.get);
						ImgMsgBody body = (ImgMsgBody) message.getMsgBody();
						intent.putExtra("secret", body.getRemoteUrl());
						intent.putExtra("remotepath", remotePath);
					}
				/*	if (message != null && message.direct == ChatComMsgEntity.Direct.RECEIVE && !message.isAcked) {
						try {
							//EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
							message.isAcked = true;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}*/
					ctx.startActivity(intent);
				}
			});
			
		}else{
			new LoadLocalImgTask().execute(iv,thumbnailImagePath,localFullSizePath,ctx,remotePath);
			
		}
	
	
	}

/**
 * 下载图片
 * @param message
 * @param holder
 */
	private void showDownloadImageProgress(final ChatComMsgEntity message, final ViewHolder holder) {
		if(holder.pb!=null)
			holder.pb.setVisibility(View.VISIBLE);
			if(holder.tv!=null)
			holder.tv.setVisibility(View.VISIBLE);
			ChatManager.getInstance().downLoadFile(message, new ChatCallBack() {
				
				@Override
				public void onSuccess() {
					LogUtil.info(ChatMsgAdapter.class, "下载图片成功");
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (message.getContentType() == ChatComMsgEntity.TYPE_IMG) {
								holder.pb.setVisibility(View.GONE);
								holder.tv.setVisibility(View.GONE);
							}
							notifyDataSetChanged();
						}
					});
				}
				
				@Override
				public void onProgress(final int progress, final int totalSize) {
					if (message.getContentType() == ChatComMsgEntity.TYPE_IMG) {
						activity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								float  a=((float)progress/(float)totalSize);
								  int  b=(int)(Math.round(a*100));
								holder.tv.setText(b  + "%");

							}
						});
					}
				}
				
				@Override
				public void onError(int code, String error) {
					
				}

				@Override
				public void onSuccess(String objectKey) {
				}
			});
			
			
			
		
	}

	private void setUserAvatar(ChatComMsgEntity message, ImageView iv_avatar) {
		if(message.direct==Direct.SEND)
			new LoadAvatarTask().execute(iv_avatar,AotoBangApp.getInstance().getUserId(),AotoBangApp.getInstance().getUserList().get(AotoBangApp.getInstance().getUserId()).getRemote_avatar());	
		else
			new LoadAvatarTask().execute(iv_avatar,conversation.getUserId(),conversation.getAvatar_remote());	
	}
	public static class ViewHolder {
		FrameLayout frm;
		ImageView iv;
		TextView tv;
		ProgressBar pb;
		ImageView staus_iv;
		ImageView iv_avatar;
		TextView tv_usernick;
		ImageView playBtn;
		TextView timeLength;
		TextView size;
		LinearLayout container_status_btn;
		LinearLayout ll_container;
		ImageView iv_read_status;
		// 显示已读回执状态
		TextView tv_ack;
		// 显示送达回执状态
		TextView tv_delivered;

		TextView tv_file_name;
		TextView tv_file_size;
		TextView tv_file_download_state;
	}
	/**
	 * 更新ui上消息发送状态
	 * 
	 * @param message
	 * @param holder
	 */
	private void updateSendedView(final ChatComMsgEntity message, final ViewHolder holder) {
		
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// send success
				if (message.getContentType() == ChatComMsgEntity.TYPE_VIDEO) {
					holder.tv.setVisibility(View.GONE);
				}
				if (message.status == ChatComMsgEntity.Status.SUCCESS) {
					 if (message.getContentType() == ChatComMsgEntity.TYPE_FILE) {
					 holder.pb.setVisibility(View.INVISIBLE);
					 holder.staus_iv.setVisibility(View.INVISIBLE);
					 } else {
					 holder.pb.setVisibility(View.GONE);
					 holder.staus_iv.setVisibility(View.GONE);
					 }

				} else if (message.status == ChatComMsgEntity.Status.FAIL) {
					 if (message.getContentType() == ChatComMsgEntity.TYPE_FILE) {
					 holder.pb.setVisibility(View.INVISIBLE);
					 } else {
					 holder.pb.setVisibility(View.GONE);
					 }
					 holder.staus_iv.setVisibility(View.VISIBLE);
					Toast.makeText(activity, activity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast), 0)
							.show();
				}

				notifyDataSetChanged();
			}
		});
	}
	/**
	 * 刷新页面, 选择最后一个
	 */
	public void refreshSelectLast() {
		handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
		handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_SELECT_LAST));
	}
	/**
	 * 刷新页面
	 */
	public void refresh() {
		if (handler.hasMessages(HANDLER_MESSAGE_REFRESH_LIST)) {
			return;
		}
		android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST);
		handler.sendMessage(msg);
	}
	
	/**
	 * 刷新页面, 选择Position
	 */
	public void refreshSeekTo(int position) {
		handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST));
		android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_SEEK_TO);
		msg.arg1 = position;
		handler.sendMessage(msg);
	}

}
