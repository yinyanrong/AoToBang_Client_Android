package com.aotobang.app.adapter;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aotobang.app.GlobalParams;
import com.aotobang.app.R;
import com.aotobang.app.activity.Regist2Activity;
import com.aotobang.app.applaction.AotoBangApp;
import com.aotobang.app.asyntask.LoadAvatarTask;
import com.aotobang.app.callback.ChatCallBack;
import com.aotobang.app.entity.ChatComMsgEntity;
import com.aotobang.app.entity.TextMsgBody;
import com.aotobang.app.manager.AotoConversation;
import com.aotobang.app.view.CircleImageView;
import com.aotobang.utils.DateUtils;
import com.aotobang.utils.ImageCache;
import com.aotobang.utils.LogUtil;
import com.aotobang.utils.UserUtils;

public class MessageAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<AotoConversation> conversationList;
	

	public MessageAdapter(Context context, List<AotoConversation> objects) {
		this.context=context;
		this.conversationList = objects;
		inflater = LayoutInflater.from(context);
		
		
	}

	@Override
	public int getCount() {

		return conversationList.size();
	}

	@Override
	public Object getItem(int position) {

		return conversationList.get(position);
	}

	@Override
	public long getItemId(int position) {

		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final AotoConversation con =conversationList.get(position);
		final ViewHolder holder;
		if(convertView!=null){
			holder=(ViewHolder) convertView.getTag();
		}else{
			convertView = View.inflate(context, R.layout.message_list_item, null);
			holder=new ViewHolder();
			holder.message_avatar=(CircleImageView) convertView.findViewById(R.id.message_avatar);
			holder.name=(TextView) convertView.findViewById(R.id.name);
			holder.message=(TextView) convertView.findViewById(R.id.message);
			holder.time=(TextView) convertView.findViewById(R.id.time);
			holder.unread_msg_count=(TextView) convertView.findViewById(R.id.unread_msg_count);
			holder.msg_state=(ImageView) convertView.findViewById(R.id.msg_state);
			convertView.setTag(holder);
		}
		holder.name.setText(con.getUserNick());
		new LoadAvatarTask().execute(holder.message_avatar,con.getUserId(),con.getAvatar_remote());	
		LogUtil.info(MessageAdapter.class,  con.getAvatar_remote()+"");
		if (con.getUnreadMsgCount() > 0) {
			// 显示与此用户的消息未读数
			holder.unread_msg_count.setText(String.valueOf(con.getUnreadMsgCount()));
			holder.unread_msg_count.setVisibility(View.VISIBLE);
		} else {
			holder.unread_msg_count.setVisibility(View.INVISIBLE);
		}

		if (con.getMsgCount() != 0) {
			// 把最后一条消息的内容作为item的message内容
			ChatComMsgEntity lastMessage = con.getLastMessage();
		/*	holder.message.setText(SmileUtils.getSmiledText(context, getMessageDigest(lastMessage, (context))),
					BufferType.SPANNABLE);*/
			holder.message.setText(getMessageDigest(lastMessage, (context)));
			holder.time.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
			if (lastMessage.direct == ChatComMsgEntity.Direct.SEND && lastMessage.status == ChatComMsgEntity.Status.FAIL) {
				holder.msg_state.setVisibility(View.VISIBLE);
			} else {
				holder.msg_state.setVisibility(View.GONE);
			}
		}
		
		
		
		return convertView;
	}
	private class ViewHolder{
		private CircleImageView message_avatar;
		private TextView name;
		private TextView message;
		private TextView time;
		private TextView unread_msg_count;
		private ImageView msg_state;
	}
	/**
	 * 根据消息内容和消息类型获取消息内容提示
	 * 
	 * @param message
	 * @param context
	 * @return
	 */
	private String getMessageDigest(ChatComMsgEntity message, Context context) {
		String digest = "";
		switch (message.getContentType()) {
		case ChatComMsgEntity.TYPE_LOCATION: // 位置消息
			break;
		case ChatComMsgEntity.TYPE_IMG: // 图片消息
			digest = context.getString(R.string.picture);
			break;
		case ChatComMsgEntity.TYPE_VOICE:// 语音消息
			
			digest =context.getString(R.string.message_voice);
			break;
		case ChatComMsgEntity.TYPE_VIDEO: // 视频消息
			digest =context.getString(R.string.video);
			break;
		case ChatComMsgEntity.TYPE_TEXT: // 文本消息
				TextMsgBody txtBody = (TextMsgBody) message.getMsgBody();
				digest = txtBody.getContent().replaceAll("\\[.{2,3}\\]", "[表情]");
			break;
		case ChatComMsgEntity.TYPE_FILE: // 普通文件消息
			digest =context.getString(R.string.file);
			break;
		default:
			System.err.println("error, unknow type");
			return "";
		}

		return digest;
	}
}
