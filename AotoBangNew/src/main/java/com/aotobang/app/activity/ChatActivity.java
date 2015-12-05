package com.aotobang.app.activity;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aotobang.annotation.BaseActivity;
import com.aotobang.annotation.ContentView;
import com.aotobang.annotation.FindViewNoOnClick;
import com.aotobang.annotation.FindViewOnClick;
import com.aotobang.app.R;
import com.aotobang.app.adapter.ChatMoreAdapter;
import com.aotobang.app.adapter.ChatMsgAdapter;
import com.aotobang.app.adapter.ExpressionAdapter;
import com.aotobang.app.adapter.ExpressionPagerAdapter;
import com.aotobang.app.applaction.AotoBangApp;
import com.aotobang.app.callback.InteractCallBack;
import com.aotobang.app.entity.ChatComMsgEntity;
import com.aotobang.app.entity.ImgMsgBody;
import com.aotobang.app.entity.Interact;
import com.aotobang.app.entity.TextMsgBody;
import com.aotobang.app.entity.VoiceMsgBody;
import com.aotobang.app.fragment.MenuFragment;
import com.aotobang.app.manager.AotoBlueToothManager;
import com.aotobang.app.manager.AotoConversation;
import com.aotobang.app.manager.ChatManager;
import com.aotobang.app.media.SoundMeter;
import com.aotobang.app.model.AotoPreLoadImp;
import com.aotobang.app.model.DefaultModelImp;
import com.aotobang.app.model.DefaultModelImp.PathType;
import com.aotobang.app.view.CompoundDrawableEditText;
import com.aotobang.app.view.CompoundDrawableEditText.DrawableClickListener;
import com.aotobang.app.view.ExpandGridView;
import com.aotobang.app.view.listener.VoicePlayClickListener;
import com.aotobang.bluetooth.AotoGattAttributes;
import com.aotobang.net.InterfaceIds;
import com.aotobang.net.entity.AotoRequest;
import com.aotobang.utils.DialogUtils;
import com.aotobang.utils.ImageTools;
import com.aotobang.utils.ImageUtils;
import com.aotobang.utils.LogUtil;
import com.aotobang.utils.SmileUtils;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
@ContentView(R.layout.activity_chat)
public class ChatActivity extends BaseActivity {
	private static final int REQUEST_CODE_EMPTY_HISTORY = 2;
	public static final int REQUEST_CODE_CONTEXT_MENU = 3;
	private static final int REQUEST_CODE_MAP = 4;
	public static final int REQUEST_CODE_TEXT = 5;
	public static final int REQUEST_CODE_VOICE = 6;
	public static final int REQUEST_CODE_PICTURE = 7;
	public static final int REQUEST_CODE_LOCATION = 8;
	public static final int REQUEST_CODE_NET_DISK = 9;
	public static final int REQUEST_CODE_FILE = 10;
	public static final int REQUEST_CODE_COPY_AND_PASTE = 11;
	public static final int REQUEST_CODE_PICK_VIDEO = 12;
	public static final int REQUEST_CODE_DOWNLOAD_VIDEO = 13;
	public static final int REQUEST_CODE_VIDEO = 14;
	public static final int REQUEST_CODE_DOWNLOAD_VOICE = 15;
	public static final int REQUEST_CODE_SELECT_USER_CARD = 16;
	public static final int REQUEST_CODE_SEND_USER_CARD = 17;
	public static final int REQUEST_CODE_CAMERA = 18;
	public static final int REQUEST_CODE_LOCAL = 19;
	public static final int REQUEST_CODE_CLICK_DESTORY_IMG = 20;
	public static final int REQUEST_CODE_GROUP_DETAIL = 21;
	public static final int REQUEST_CODE_SELECT_VIDEO = 23;
	public static final int REQUEST_CODE_SELECT_FILE = 24;
	public static final int REQUEST_CODE_ADD_TO_BLACKLIST = 25;
	public static final int REQUEST_CODE_INTERACT = 26;
	public static final int RESULT_CODE_COPY = 1;
	public static final int RESULT_CODE_DELETE = 2;
	public static final int RESULT_CODE_FORWARD = 3;
	public static final int RESULT_CODE_OPEN = 4;
	public static final int RESULT_CODE_DWONLOAD = 5;
	//interact
	public static final String APPLY="apply";
	public static final String AGREE="agree";
	public static final String REFUSE="refuse";
	public static final String CANCLE="cancle";
	@FindViewNoOnClick(R.id.toast)
	private TextView toast;
	@FindViewNoOnClick(R.id.chat_root)
	private RelativeLayout chat_root;
	@FindViewNoOnClick(R.id.pull_refresh_list)
	private PullToRefreshListView pull_refresh_list;
	@FindViewOnClick(R.id.voice_change)
	private ImageView voice_change;
	@FindViewNoOnClick(R.id.volume)
	private ImageView volume;
	@FindViewOnClick(R.id.btn_more)
	private ImageView btn_more;
	@FindViewOnClick(R.id.et_message)
	private CompoundDrawableEditText et_message;
	@FindViewNoOnClick(R.id.text_pre_rcd)
	private TextView text_pre_rcd;
	@FindViewOnClick(R.id.btn_send)
	private TextView btn_send;
	@FindViewOnClick(R.id.cancle)
	private TextView cancle;
	@FindViewNoOnClick(R.id.rcChat_popup)
	private LinearLayout rcChat_popup;
	@FindViewNoOnClick(R.id.ll_voice_rcd_hint_rcding)
	private LinearLayout ll_voice_rcd_hint_rcding;
	@FindViewNoOnClick(R.id.ll_voice_rcd_hint_tooshort)
	private LinearLayout ll_voice_rcd_hint_tooshort;
	@FindViewNoOnClick(R.id.vPager)
	private ViewPager expressionViewpager;
	@FindViewNoOnClick(R.id.more_grid)
	private ExpandGridView more_grid;
	@FindViewOnClick(R.id.btn_back)
	private ImageButton btn_back;
	private SoundMeter mSensor;
	private boolean mode_voice=false;
	private Handler mHandler = new Handler();
	private List<ChatComMsgEntity> mDataArrays;
	private List<String>  expressionList;
	private ChatMsgAdapter mAdapter;
	private ListView mListView;
	private int flag = 1;
	private boolean isShosrt = false;
	private long startVoiceT, endVoiceT;
	private String voiceName;
	private Drawable expression;
	private Drawable expression_show;
	private boolean express_show=false;
	private boolean show_more=false;
	public String playMsgId;
	private String toUserId="1001";
	private NewMsaageReceiver  mNewMsaageReceiver=new NewMsaageReceiver();
	private AotoConversation conversation ;
	private BluetoothAdapter mBluetoothAdapter;
	private Handler uiHandler=new Handler();
	private Runnable applyTimeOut=new Runnable(){
		public void run() {
			DialogUtils.closeProgressDialog();
			Toast.makeText(ChatActivity.this, "对方未响应", 0).show();
			sendInteractRequest(CANCLE);

		};

	};
	private Runnable maxVoiceTime=new Runnable() {

		@Override
		public void run() {
			voiceLoingTime=true;
			stop();
			flag = 1;
			text_pre_rcd.setBackgroundResource(R.drawable.voice_bg);
			ll_voice_rcd_hint_rcding.setVisibility(View.GONE);
			sendVoice(60);

		}
	};
	private void sendVoice(int voiceTime){
		ChatComMsgEntity entity = ChatComMsgEntity.createSendEntity(ChatComMsgEntity.TYPE_VOICE);
		VoiceMsgBody body=new VoiceMsgBody();
		//body.setFileName(voiceName);
		body.setFileName(entity.getMsgId().replace("-", ""));
		entity.setMsgTime(System.currentTimeMillis());
		entity.setFrom(AotoBangApp.getInstance().getUserId());
		entity.setTo(toUserId);
		body.setLength(voiceTime);
		body.setLocalUrl(AotoBangApp.getInstance().getCachePath(PathType.Audio)+voiceName);
		entity.setMsgBody(body);
		conversation.addMessage(entity);
		mAdapter.refreshSelectLast();
		//mDataArrays.add(entity);
		//mAdapter.notifyDataSetChanged();
		//mListView.setSelection(mListView.getCount() - 1);
		rcChat_popup.setVisibility(View.GONE);

	}
	private boolean voiceLoingTime=false;
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.voice_change:
				if(mode_voice){
					hidenVoice();

				}else{
					showVoice();
				}

				break;
			case R.id.btn_send:
				sendTextMsg();
				break;
			case R.id.btn_more:
				if(show_more){
					hidenMore();
				}else{
					showMore();
					if(mode_voice){
						hidenVoice();

					}
				}
				break;
			case R.id.btn_back:
				finish();
				break;



		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MenuFragment.REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
			Intent intent=new Intent();
			intent.setClass(ChatActivity.this, ToyActivity.class);
			startActivity(intent);
			return;
		}


		if (resultCode == RESULT_OK){
			if (requestCode == REQUEST_CODE_LOCAL) {
				hidenMore();// 发送本地图片
				if (data != null) {
					Uri selectedImage = data.getData();
					if (selectedImage != null) {
						sendPicByUri(selectedImage);
					}
				}

			}
		}else if(resultCode==REQUEST_CODE_INTERACT&&AotoBlueToothManager.getInstance().isConnected()){
			uiHandler.postDelayed(new Runnable() {

				@Override
				public void run() {

					AotoBlueToothManager.getInstance().stopDevice();
				}
			}, 1000);


		}
	}
	private void sendPicByUri(Uri selectedImage) {
		Cursor cursor = getContentResolver().query(selectedImage, null, null, null, null);
		String st8 = getString(R.string.cant_find_pictures);
		if (cursor != null) {
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex("_data");
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			cursor = null;

			if (picturePath == null || picturePath.equals("null")) {
				Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
			sendPicture(picturePath);
		} else {
			File file = new File(selectedImage.getPath());
			if (!file.exists()) {
				Toast toast = Toast.makeText(this, st8, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;

			}
			sendPicture(file.getAbsolutePath());
		}

	}
	/**
	 * 发送图片
	 *
	 * @param filePath
	 */
	private void sendPicture(final String filePath) {
		ChatComMsgEntity entity=ChatComMsgEntity.createSendEntity(ChatComMsgEntity.TYPE_IMG);
		entity.setFrom(AotoBangApp.getInstance().getUserId());
		entity.setTo(toUserId);
		entity.setMsgTime(System.currentTimeMillis());
		ImgMsgBody  body=new ImgMsgBody();
		//body.setFileName(PathUtil.getFileName(filePath));
		body.setFileName(entity.getMsgId().replace("-", ""));
		body.setLocalUrl(filePath);
		entity.setMsgBody(body);
		entity.direct=ChatComMsgEntity.Direct.SEND;
		// 默认超过100k的图片会压缩后发给对方，可以设置成发送原图
		// body.setSendOriginalImage(true);

		//mListView.setAdapter(mAdapter);
		/*mDataArrays.add(entity);
		mAdapter.notifyDataSetChanged();
		mListView.setSelection(mListView.getCount()-1);*/
		conversation.addMessage(entity);
		mAdapter.refreshSelectLast();
		setResult(RESULT_OK);
		// more(more);
	}

	@Override
	public void initView() {
		BluetoothManager bluetoothManager = (BluetoothManager)ChatActivity.this.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		toUserId=getIntent().getStringExtra("userId");
		getWindow().setBackgroundDrawableResource(R.drawable.chat_bg);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		chat_root.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				int heightDiff = chat_root.getRootView().getHeight()- chat_root.getHeight();
				if(heightDiff >100){
					 /* if(expressionViewpager.getVisibility()==View.VISIBLE||more_grid.getVisibility()==View.VISIBLE){
							hidenExpression();
							hidenMore();
						}*/
					mListView.setSelection(mListView.getCount()-1);

				}else{


				}

			}
		});
		mSensor=new SoundMeter(ChatActivity.this);
		et_message.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(TextUtils.isEmpty(s.toString().trim())){
					btn_more.setVisibility(View.VISIBLE);
					btn_send.setVisibility(View.INVISIBLE);


				}else{
					btn_more.setVisibility(View.INVISIBLE);
					btn_send.setVisibility(View.VISIBLE);

				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {



			}
		});
		expression = getResources().getDrawable(R.drawable.chatting_biaoqing_btn_normal);
		expression=ImageTools.zoomDrawable(expression,(int) getResources().getDimension(R.dimen.biaoqing_icon), (int) getResources().getDimension(R.dimen.biaoqing_icon));
		expression_show = getResources().getDrawable(R.drawable.chatting_biaoqing_btn_enable);
		expression_show=ImageTools.zoomDrawable(expression_show, (int) getResources().getDimension(R.dimen.biaoqing_icon), (int) getResources().getDimension(R.dimen.biaoqing_icon));
		et_message.setCompoundDrawablesWithIntrinsicBounds(null, null,expression, null);
		et_message.setLazy(20, 10);
		et_message.setDrawableClickListener(new DrawableClickListener(){

			@Override
			public void onClick(DrawablePosition position) {
				if(position==DrawablePosition.RIGHT)
					changeExpression();
			}


		});
		et_message.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(expressionViewpager.getVisibility()==View.VISIBLE||more_grid.getVisibility()==View.VISIBLE){
					hidenExpression();
					hidenMore();
				}


			}
		});
		text_pre_rcd.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				return false;
			}
		});
		// 表情list
		expressionList = getExpressionRes(35);
		List<View> views = new ArrayList<View>();
		View gv1 = getGridChildView(1);
		//View gv2 = getGridChildView(2);
		views.add(gv1);
		//views.add(gv2);
		expressionViewpager.setAdapter(new ExpressionPagerAdapter(views));
		//消息list
		mListView=pull_refresh_list.getRefreshableView();
		mListView.setSelector(android.R.color.transparent);
		mListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideKeyboard();
				hidenExpression();
				hidenMore();
				return false;
			}
		});
		mListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		more_grid.setSelector(android.R.color.transparent);
		more_grid.setAdapter(new ChatMoreAdapter(ChatActivity.this));
		more_grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int postion, long id) {
				switch(postion){
					case 2:
						selectPicFromLocal();
						break;
					case 3:
						if(AotoBlueToothManager.getInstance().isConnected()){
							uiHandler.postDelayed(applyTimeOut, 5000);
							sendInteractRequest(APPLY);

						}else{
							if (!mBluetoothAdapter.isEnabled()) {
								Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
								startActivityForResult(enableBtIntent, MenuFragment.REQUEST_ENABLE_BT);
							}else{
								Intent intent=new Intent();
								intent.setClass(ChatActivity.this, ToyActivity.class);
								startActivity(intent);
							}


							Toast.makeText(ChatActivity.this, "没有连接设备", 0).show();

						}


						break;


				}


			}
		});;
		initData();
		boolean isShowChatToast=sp.getBoolean(DefaultModelImp.PREF_SHOW_CHAT_TOAST, false);
		if(!isShowChatToast){
			Animation	in = AnimationUtils.loadAnimation(ChatActivity.this, R.anim.chat_prompt_in);
			in.setFillAfter(true);
			toast.setVisibility(View.VISIBLE);
			toast.startAnimation(in);
			uiHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					Animation	out = AnimationUtils.loadAnimation(ChatActivity.this, R.anim.chat_prompt_out);
					out.setAnimationListener(new AnimationListener() {

						@Override
						public void onAnimationStart(Animation animation) {
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
						}

						@Override
						public void onAnimationEnd(Animation animation) {
							sp.edit().putBoolean(DefaultModelImp.PREF_SHOW_CHAT_TOAST, true).commit();
							toast.setVisibility(View.GONE);
						}
					});
					out.setFillAfter(true);
					toast.startAnimation(out);




				}
			}, 3000);
		}


	}

	/**
	 * 从图库获取图片
	 */
	public void selectPicFromLocal() {
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");

		} else {
			intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, REQUEST_CODE_LOCAL);
	}
	private void showVoice(){
		mode_voice=true;
		et_message.setVisibility(View.INVISIBLE);
		text_pre_rcd.setVisibility(View.VISIBLE);
		voice_change.setImageResource(R.drawable.selector_btn_keyboard);
		btn_more.setVisibility(View.VISIBLE);
		btn_send.setVisibility(View.INVISIBLE);
		hidenExpression();
		hideKeyboard();
		hidenMore();

	}
	private void hidenVoice(){
		if(mode_voice){
			mode_voice=false;
			et_message.setVisibility(View.VISIBLE);
			text_pre_rcd.setVisibility(View.INVISIBLE);
			voice_change.setImageResource(R.drawable.selector_voice_change);
			if(TextUtils.isEmpty(et_message.getText().toString().trim())){
				btn_more.setVisibility(View.VISIBLE);
				btn_send.setVisibility(View.INVISIBLE);
			}else{
				btn_more.setVisibility(View.INVISIBLE);
				btn_send.setVisibility(View.VISIBLE);

			}
		}
	}
	private void showMore(){
		show_more=true;
		more_grid.setVisibility(View.VISIBLE);
		hideKeyboard();
		hidenExpression();
		mListView.setSelection(mListView.getCount()-1);


	}
	private void hidenMore(){
		if(show_more){
			show_more=false;
			more_grid.setVisibility(View.GONE);
		}
	}
	private void changeExpression(){

		if(express_show){

			hidenExpression();

		}else{
			showExpression();

		}

	}
	private void showExpression(){
		hideKeyboard();
		hidenMore();
		mListView.setSelection(mListView.getCount()-1);
		et_message.setCompoundDrawablesWithIntrinsicBounds(null, null,expression_show, null);
		expressionViewpager.setVisibility(View.VISIBLE);
		express_show=true;




	}
	private void hidenExpression(){
		if(express_show){
			mListView.setSelection(mListView.getCount()-1);
			et_message.setCompoundDrawablesWithIntrinsicBounds(null, null,expression, null);
			expressionViewpager.setVisibility(View.GONE);
			express_show=false;

		}

	}

	private  int pager = 0;
	private final static int PAGER_SIZE = 10;


	public void initData() {
		conversation=ChatManager.getInstance().getConversationByUUID(toUserId);
		LogUtil.info(ChatActivity.class, conversation.getUserName()+"-initData-"+conversation.getUserId());
		final List<ChatComMsgEntity> msgs = conversation.getAllMessages();
		int msgCount = msgs != null ? msgs.size() : 0;
		if (msgCount < conversation.getAllMessagesCount() && msgCount < PAGER_SIZE) {
			String msgId = null;
			if (msgs != null && msgs.size() > 0) {
				msgId = msgs.get(0).getMsgId();
			}
			conversation.loadMoreMsgFromDB(msgId, PAGER_SIZE);

		}
		pager++;
		mAdapter = new ChatMsgAdapter(this, mDataArrays,toUserId);
		mListView.setAdapter(mAdapter);
		mAdapter.refreshSelectLast();
	}

	private String getDate() {
		Calendar c = Calendar.getInstance();
		String year = String.valueOf(c.get(Calendar.YEAR));
		String month = String.valueOf(c.get(Calendar.MONTH));
		String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH) + 1);
		String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
		String mins = String.valueOf(c.get(Calendar.MINUTE));

		StringBuffer sbBuffer = new StringBuffer();
		sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":"
				+ mins);

		return sbBuffer.toString();
	}
	private void sendTextMsg() {
		String contString = et_message.getText().toString();
		if (contString.length() > 0) {
			ChatComMsgEntity entity = ChatComMsgEntity.createSendEntity(ChatComMsgEntity.TYPE_TEXT);
			entity.setMsgTime(System.currentTimeMillis());
			TextMsgBody body=new TextMsgBody();
			entity.setMsgBody(body);
			entity.setTo(toUserId);
			entity.setFrom(AotoBangApp.getInstance().getUserId());
			body.setContent(contString);
			//	mDataArrays.add(entity);
			et_message.setText("");
			conversation.addMessage(entity);
			mAdapter.refreshSelectLast();
			//	mAdapter.notifyDataSetChanged();
			//	mListView.setSelection(mListView.getCount() - 1);

		}
	}
	/**
	 * 获取表情的gridview的子view
	 *
	 * @param i
	 * @return
	 */
	private View getGridChildView(int i) {
		View view = View.inflate(this, R.layout.expression_gridview, null);
		ExpandGridView gv = (ExpandGridView) view.findViewById(R.id.gridview);
		gv.setSelector(android.R.color.transparent);
		List<String> list = new ArrayList<String>();
		if (i == 1) {
			List<String> list1 = expressionList.subList(0, 20);
			list.addAll(list1);
		} else if (i == 2) {
			list.addAll(expressionList.subList(20, expressionList.size()));
		}
		list.add("delete_expression");
		final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this, 1, list);
		gv.setAdapter(expressionAdapter);
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String filename = expressionAdapter.getItem(position);
				try {
					// 文字输入框可见时，才可输入表情
					// 按住说话可见，不让输入表情
					if (text_pre_rcd.getVisibility() != View.VISIBLE) {

						if (filename != "delete_expression") { // 不是删除键，显示表情
							// 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
							Class clz = Class.forName("com.aotobang.utils.SmileUtils");

							Field field = clz.getField(filename);
							et_message.append(SmileUtils.getSmiledText(ChatActivity.this, (String) field.get(null)));
						} else { // 删除文字或者表情
							if (!TextUtils.isEmpty(et_message.getText())) {

								int selectionStart = et_message.getSelectionStart();// 获取光标的位置
								if (selectionStart > 0) {
									String body = et_message.getText().toString();
									String tempStr = body.substring(0, selectionStart);
									int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
									if (i != -1) {
										CharSequence cs = tempStr.substring(i, selectionStart);
										if (SmileUtils.containsKey(cs.toString()))
											et_message.getEditableText().delete(i, selectionStart);
										else
											et_message.getEditableText().delete(selectionStart - 1, selectionStart);
									} else {
										et_message.getEditableText().delete(selectionStart - 1, selectionStart);
									}
								}
							}

						}
					}
				} catch (Exception e) {
				}

			}
		});
		return view;
	}
	public List<String> getExpressionRes(int getSum) {
		List<String> reslist = new ArrayList<String>();
		for (int x = 1; x <= getSum; x++) {
			String filename = "ee_" + x;

			reslist.add(filename);

		}
		return reslist;

	}
	private static final int POLL_INTERVAL = 300;

	private Runnable mSleepTask = new Runnable() {
		public void run() {
			stop();
		}
	};
	private Runnable mPollTask = new Runnable() {
		public void run() {
			double amp = mSensor.getAmplitude();
			updateDisplay(amp);
			mHandler.postDelayed(mPollTask, POLL_INTERVAL);

		}
	};


	private void start(String name) {
		mSensor.start(name);
		mHandler.postDelayed(mPollTask, POLL_INTERVAL);
	}

	private void stop() {
		mHandler.removeCallbacks(mSleepTask);
		mHandler.removeCallbacks(mPollTask);
		mSensor.stop();
		volume.setImageResource(R.drawable.amp1);
	}

	private void updateDisplay(double signalEMA) {

		switch ((int) signalEMA) {
			case 0:
			case 1:
				volume.setImageResource(R.drawable.amp1);
				break;
			case 2:
			case 3:
				volume.setImageResource(R.drawable.amp2);

				break;
			case 4:
			case 5:
				volume.setImageResource(R.drawable.amp3);
				break;
			case 6:
			case 7:
				volume.setImageResource(R.drawable.amp4);
				break;
			case 8:
			case 9:
				volume.setImageResource(R.drawable.amp5);
				break;
			case 10:
			case 11:
				volume.setImageResource(R.drawable.amp6);
				break;
			default:
				volume.setImageResource(R.drawable.amp7);
				break;
		}
	}
	//private	Rect rect;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!Environment.getExternalStorageDirectory().exists()) {
			Toast.makeText(this, "没有储存卡", Toast.LENGTH_LONG).show();
			return false;
		}
		if(mode_voice){

			int[] location = new int[2];
			//	text_pre_rcd.getLocationOnScreen(location); // 获取在当前窗口内的绝对坐标
			text_pre_rcd.getLocationInWindow(location);
			int btn_rc_Y = location[1];
			int btn_rc_X = location[0];
			/*if (null == rect) {
	    		rect = new Rect();
	        }
			text_pre_rcd.getDrawingRect(rect);
			rect.left=location[0];
			rect.top=location[1];
			rect.right=rect.right+location[0];
			rect.bottom=rect.bottom+location[1];*/
			switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					voiceLoingTime=false;
					if(flag==1&&!isShosrt){
						if (!Environment.getExternalStorageDirectory().exists()) {
							Toast.makeText(this, "No SDCard", Toast.LENGTH_LONG).show();
							return false;
						}
						//	if (rect.contains((int)event.getRawX(),(int)event.getRawY())) {//判断手势按下的位置是否是语音录制按钮的范围内
						if (event.getY() > btn_rc_Y && event.getX() > btn_rc_X) {
							text_pre_rcd.setBackgroundResource(R.drawable.voice_pre_bg);
							rcChat_popup.setVisibility(View.VISIBLE);
							ll_voice_rcd_hint_rcding.setVisibility(View.GONE);
							ll_voice_rcd_hint_tooshort.setVisibility(View.GONE);
							mHandler.postDelayed(new Runnable() {
								public void run() {
									if (!isShosrt) {
										ll_voice_rcd_hint_rcding.setVisibility(View.VISIBLE);
									}
								}
							}, 300);
							startVoiceT = System.currentTimeMillis();
							voiceName = startVoiceT + ".amr";
							start(voiceName);
							mHandler.postDelayed(maxVoiceTime, 60000);
							flag = 2;
						}
					}
					break;
				case MotionEvent.ACTION_MOVE:
					if (event.getY() < btn_rc_Y-200) {//手势按下的位置不在语音录制按钮的范围内
						cancle.setTextColor(getResources().getColor(R.color.red));
						cancle.setText(getString(R.string.release_to_cancel));
					}
					else{
						cancle.setTextColor(getResources().getColor(android.R.color.white));
						cancle.setText(getString(R.string.move_up_to_cancel));
					}
					break;
				case MotionEvent.ACTION_UP:
					cancle.setTextColor(getResources().getColor(android.R.color.white));
					cancle.setText(getString(R.string.move_up_to_cancel));
					if(voiceLoingTime)
						return false;

					mHandler.removeCallbacks(maxVoiceTime);
					if(flag==2){
						text_pre_rcd.setBackgroundResource(R.drawable.voice_bg);
						if (event.getY() <=btn_rc_Y-200){
							rcChat_popup.setVisibility(View.GONE);
							stop();
							flag = 1;
							File file = new File(AotoBangApp.getInstance().getCachePath(PathType.Audio)+voiceName);

							if (file.exists()) {
								file.delete();
							}
						}else{

							ll_voice_rcd_hint_rcding.setVisibility(View.GONE);
							endVoiceT = System.currentTimeMillis();
							flag = 1;
							int time = (int) ((endVoiceT - startVoiceT) / 1000);
							if (time < 1) {
								isShosrt = true;
								ll_voice_rcd_hint_tooshort.setVisibility(View.VISIBLE);

								mHandler.postDelayed(new Runnable() {
									public void run() {
										ll_voice_rcd_hint_tooshort.setVisibility(View.GONE);
										rcChat_popup.setVisibility(View.GONE);
										isShosrt = false;
										stop();
									}
								}, 1000);
								return false;
							}
							stop();
							sendVoice(time);

						}

					}
					break;

			}

		}

		return super.onTouchEvent(event);
	}
	/**
	 * 覆盖手机返回键
	 */
	@Override
	public void onBackPressed() {
		if (expressionViewpager.getVisibility() == View.VISIBLE) {
			hidenExpression();
		} else if(more_grid.getVisibility()==View.VISIBLE){

			hidenMore();

		}else {
			super.onBackPressed();
		}
	}
	/**
	 * 隐藏软键盘
	 */
	private void hideKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		ChatManager.getInstance().registInteractApplyListener(interactCallBack);
		AotoPreLoadImp.getInstance().getNotifier().reset();
		conversation.resetUnreadMsgCount();
		mAdapter.refresh();
		IntentFilter intentFilter = new IntentFilter(ChatManager.NEW_MESSAGE_ACTION);
		intentFilter.setPriority(30);
		registerReceiver(mNewMsaageReceiver, intentFilter);
		AotoPreLoadImp prload = (AotoPreLoadImp) AotoPreLoadImp.getInstance();
		prload.pushActivity(this);


	}
	@Override
	protected void onPause() {
		ChatManager.getInstance().unRegistInteractApplyListener();
		conversation.resetUnreadMsgCount();
		unregisterReceiver(mNewMsaageReceiver);
		if (VoicePlayClickListener.isPlaying && VoicePlayClickListener.currentPlayListener != null) {
			// 停止语音播放
			VoicePlayClickListener.currentPlayListener.stopPlayVoice();
		}
		super.onPause();
	}
	@Override
	protected void onStop() {
		super.onStop();
		AotoPreLoadImp prload = (AotoPreLoadImp) AotoPreLoadImp.getInstance();
		prload.popActivity(this);
	}
	@Override
	protected void onDestroy() {

		super.onDestroy();
	}
	private android.app.AlertDialog.Builder interactBuilder;
	private AlertDialog  interactDialog;
	private int currentDeviceType;
	//private boolean isInteractDialogShow=false;
	private void showInteractDialog(){
		//	isInteractDialogShow=true;
		if(!ChatActivity.this.isFinishing()){
			if (interactBuilder == null){
				interactBuilder = new android.app.AlertDialog.Builder(ChatActivity.this);
				interactBuilder.setTitle("提示");
				interactBuilder.setMessage("对方邀请你互动，是否同意?");
				interactBuilder.setPositiveButton("同意", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						sendInteractRequest(AGREE);
						Intent intent=new Intent();
						intent.putExtra("toUserId", toUserId);
						intent.putExtra("haveDevice", currentDeviceType);
						intent.setClass(ChatActivity.this, InteractActivity.class);
						startActivity(intent);
					}
				});
				interactBuilder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						sendInteractRequest(REFUSE);
					}
				});
				interactBuilder.setCancelable(false);
				interactDialog=interactBuilder.create();
			}
			if(interactDialog!=null&&!interactDialog.isShowing())
				interactDialog.show();



		}





	}
	private InteractCallBack  interactCallBack=new InteractCallBack() {

		@Override
		public void OnInteractMsg(Interact msg) {
			currentDeviceType=msg.getHaveDevice();
			LogUtil.info(ChatActivity.class, msg.getData());
			int type=msg.getType();
			if(type!=0)
				return;
			String data=msg.getData();
			if(data.equals(APPLY)){

				runOnUiThread(new Runnable() {

					@Override
					public void run() {

						showInteractDialog();
						/*DialogUtils.showInfoDialog(ChatActivity.this, "对方邀请你互动", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									sendInteractRequest(AGREE);
									Intent intent=new Intent();
									intent.putExtra("toUserId", toUserId);
									intent.setClass(ChatActivity.this, InteractActivity.class);
									startActivity(intent);
								
							}
						}, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								sendInteractRequest(REFUSE);
							}
						},"同意","拒绝");*/
					}
				});



			}else if(data.equals(AGREE)){
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						DialogUtils.closeProgressDialog();
					}
				});
				//LogUtil.info(ChatActivity.class, conversation.getUserName()+"--"+conversation.getUserId());

				uiHandler.removeCallbacks(applyTimeOut);
				Intent intent=new Intent();
				intent.putExtra("haveDevice", msg.getHaveDevice());
				intent.putExtra("toUserId", toUserId);
				intent.putExtra("userName", conversation.getUserName());
				intent.setClass(ChatActivity.this, InteractActivity.class);
				startActivity(intent);

				//	LinphoneManager.getInstance().newOutgoingCall(conversation.getUserName(),null);


			}else if(data.equals(REFUSE)){

				runOnUiThread( new Runnable() {
					public void run() {
						DialogUtils.closeProgressDialog();
						uiHandler.removeCallbacks(applyTimeOut);
						Toast.makeText(ChatActivity.this, "对方拒绝互动请求", 0).show();

					}
				});

			}else if(data.equals(CANCLE)){
				interactDialog.dismiss();
			}
		}
	};
	private class NewMsaageReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String userId=intent.getStringExtra("userid");
			String msgId=intent.getStringExtra("msgid");
			LogUtil.info(ChatActivity.class, "收到一条新消息");
			if(userId.equals(toUserId)){
				abortBroadcast();
				mAdapter.refreshSelectLast();
			}




		}
	}
	@Override
	public void handleDataResultOnSuccee(int responseId, Object data) {
	}
	@Override
	public void handleDataResultOnError(int responseId, int errCode, Object error) {
		super.handleDataResultOnError(responseId, errCode, error);



	}
	public void sendInteractRequest(String param){
		if(param.equals(APPLY))
			DialogUtils.showProgressDialog(ChatActivity.this, "正在申请互动...等待对方同意",false);
		AotoRequest request=AotoRequest.createRequest(InterfaceIds.INTERACT_APPLY);
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("sessionId", AotoBangApp.getInstance().getSessionId());
		map.put("to", toUserId);
		map.put("content", param);
		map.put("type", 0);
		if( AotoBlueToothManager.getInstance().isConnected()){
			if(AotoBlueToothManager.getInstance().getDeviceName().equals(AotoGattAttributes.LVS_A006))
				map.put("haveDevice",AotoGattAttributes.DEVICE_TYPE_LVS_A);
			else if(AotoBlueToothManager.getInstance().getDeviceName().equals(AotoGattAttributes.LVS_B006))
				map.put("haveDevice",AotoGattAttributes.DEVICE_TYPE_LVS_B);
			else
				map.put("haveDevice",AotoGattAttributes.DEVICE_TYPE_COMMON);
		}else{
			map.put("haveDevice",AotoGattAttributes.DEVICE_TYPE_NONE);
		}
		request.setParameters(map);
		request.setCallBack(ChatActivity.this);
		sendRequest(request);
	}
	public ListView getListView(){
		return mListView;


	}

}
