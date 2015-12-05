package com.aotobang.app.view.listener;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.aotobang.app.R;
import com.aotobang.app.activity.ChatActivity;
import com.aotobang.app.entity.ChatComMsgEntity;
import com.aotobang.app.entity.VoiceMsgBody;
import com.aotobang.local.dao.LocalMsgDao;

public class VoicePlayClickListener implements OnClickListener {

	ChatComMsgEntity message;
	VoiceMsgBody voiceBody;
	ImageView voiceIconView;
	private AnimationDrawable voiceAnimation = null;
	MediaPlayer mediaPlayer = null;
	ImageView iv_read_status;
	Activity activity;
	private BaseAdapter adapter;

	public static boolean isPlaying = false;
	public static VoicePlayClickListener currentPlayListener = null;

	/**
	 * 
	 * @param message
	 * @param v
	 * @param iv_read_status
	 * @param context
	 * @param activity
	 * @param user
	 * @param chatType
	 */
	public VoicePlayClickListener(ChatComMsgEntity message, ImageView v, ImageView iv_read_status, BaseAdapter adapter, Activity activity) {
		this.message = message;
		voiceBody = (VoiceMsgBody) message.getMsgBody();
		this.iv_read_status = iv_read_status;
		this.adapter = adapter;
		this.voiceIconView = v;
		this.activity = activity;
	}

	public void stopPlayVoice() {
		voiceAnimation.stop();
		if (message.direct== ChatComMsgEntity.Direct.RECEIVE) {
			voiceIconView.setImageResource(R.drawable.left_voice3);
		} else {
			voiceIconView.setImageResource(R.drawable.right_voice3);
		}
		// stop play voice
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
		}
		isPlaying = false;
		((ChatActivity) activity).playMsgId = null;
		//adapter.notifyDataSetChanged();
	}

	public void playVoice(String filePath) {
		if (!(new File(filePath).exists())) {
			return;
		}
		((ChatActivity) activity).playMsgId =message.getMsgId() ;
		AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);

		mediaPlayer = new MediaPlayer();
		/*if (HXSDKHelper.getInstance().getModel().getSettingMsgSpeaker()) {
			audioManager.setMode(AudioManager.MODE_NORMAL);
			audioManager.setSpeakerphoneOn(true);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
		} else {
			audioManager.setSpeakerphoneOn(false);// 关闭扬声器
			// 把声音设定成Earpiece（听筒）出来，设定为正在通话中
			audioManager.setMode(AudioManager.MODE_IN_CALL);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
		}*/
		try {
			mediaPlayer.setDataSource(filePath);
			mediaPlayer.prepare();
			mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					mediaPlayer.release();
					mediaPlayer = null;
					stopPlayVoice(); // stop animation
				}

			});
			isPlaying = true;
			currentPlayListener = this;
			mediaPlayer.start();
			showAnimation();

		/*	// 如果是接收的消息
			if (message.direct== ChatComMsgEntity.Direct.RECEIVE) {
				try {
					if (!message.isAcked) {
						message.isAcked = true;
						// 告知对方已读这条消息
						if (chatType != ChatType.GroupChat)
							//EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
					}
				} catch (Exception e) {
					message.isAcked = false;
				}
				if (!voiceBody.isListen() && iv_read_status != null && iv_read_status.getVisibility() == View.VISIBLE) {
					// 隐藏自己未播放这条语音消息的标志
					iv_read_status.setVisibility(View.INVISIBLE);
					//EMChatManager.getInstance().setMessageListened(message);
				}

			}*/

		} catch (Exception e) {
		}
	}

	// show the voice playing animation
	private void showAnimation() {
		// play voice, and start animation
		if (message.direct== ChatComMsgEntity.Direct.RECEIVE) {
			voiceIconView.setImageResource(R.anim.voice_from_icon);
		} else {
			voiceIconView.setImageResource(R.anim.voice_to_icon);
		}
		voiceAnimation = (AnimationDrawable) voiceIconView.getDrawable();
		voiceAnimation.start();
	}

	@Override
	public void onClick(View v) {
		String st = activity.getResources().getString(R.string.Is_download_voice_click_later);
		if (isPlaying) {
			if (((ChatActivity) activity).playMsgId != null && ((ChatActivity) activity).playMsgId.equals(message.getMsgId())) {
				currentPlayListener.stopPlayVoice();
				return;
			}
			currentPlayListener.stopPlayVoice();
		}

		if (message.direct== ChatComMsgEntity.Direct.SEND) {
			// for sent msg, we will try to play the voice file directly
			playVoice(voiceBody.getLocalUrl());
		} else {
			if (message.status == ChatComMsgEntity.Status.SUCCESS) {
				
					
				File file = new File(voiceBody.getLocalUrl());
				if (file.exists() && file.isFile())
					playVoice(voiceBody.getLocalUrl());
				else
					System.err.println("file not exist");
				if(!voiceBody.isListen()){
					voiceBody.setListen(true);
					LocalMsgDao dao=new LocalMsgDao();
					dao.updateListend(message);
					adapter.notifyDataSetChanged();
					}

			} else if (message.status == ChatComMsgEntity.Status.INPROGRESS) {
				Toast.makeText(activity, st, Toast.LENGTH_SHORT).show();
			} else if (message.status == ChatComMsgEntity.Status.FAIL) {
				Toast.makeText(activity, "未下载成功", Toast.LENGTH_SHORT).show();
				new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						//EMChatManager.getInstance().asyncFetchMessage(message);
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						super.onPostExecute(result);
						adapter.notifyDataSetChanged();
					}

				}.execute();

			}

		}
	}

}
