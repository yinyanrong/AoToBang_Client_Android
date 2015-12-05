package com.aotobang.app.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.aotobang.app.GlobalParams;
import com.aotobang.app.R;
import com.aotobang.app.applaction.AotoBangApp;
import com.aotobang.app.fragment.MacthFriendFragment;
import com.aotobang.app.fragment.MainFragment;
import com.aotobang.app.fragment.MenuFragment;
import com.aotobang.app.manager.AotoBlueToothManager;
import com.aotobang.app.manager.ChatManager;
import com.aotobang.app.manager.PhoneManager;
import com.aotobang.app.manager.UIFragmentManager;
import com.aotobang.app.model.AotoPreLoad;
import com.aotobang.app.model.AotoPreLoadImp;
import com.aotobang.utils.LogUtil;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class HomeActivity extends SlidingFragmentActivity implements OnClickListener {
	public InputMethodManager imm;
	public static SlidingMenu slidingMenu;
	private TextView main_title_text;
	private ImageButton btn_message;
	private long mkeyTime;
	private TextView new_msg_count;
	private BroadcastReceiver newMsgReceiver;
	private ImageButton btn_user;
	private Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			LogUtil.info(HomeActivity.class, "refeshAvatar");
			refeshAvatar();
		};
		
	};
	public  Handler getHandler(){
		
		return handler;
		
	};
	// 账号在别处登录
	public boolean remoteLogin = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initView( savedInstanceState);
		newMsgReceiver=new NewMsgBroadcastReceiver();
		
	}
	private void initView(Bundle savedInstanceState) {
		if(savedInstanceState!=null){
			 startActivity(new Intent(this, LaunchActivity.class));
			 finish();
			 return;
		}
		/*if(savedInstanceState!=null&&savedInstanceState.getBoolean("remoteLogin", false)){
			  	finish();
	            startActivity(new Intent(this, LoginActivity.class));
	            return;
			
		}
		if(savedInstanceState!=null){
			ChatManager.getInstance().loadConversations();
		}
		if(getIntent().getStringExtra("from")!=null&&getIntent().getStringExtra("from").equals("regist")){
			Intent info=new Intent();
			info.setClass(HomeActivity.this, Regist2Activity.class);
			startActivity(info);
		}*/
		PhoneManager.getPhoneManager().storeAccount(AotoBangApp.getInstance().getUserName(), AotoBangApp.getInstance().getPassword(), "sip.aotobang.com:6060", HomeActivity.this);
		GlobalParams.homeActivity=HomeActivity.this;
		slidingMenu = getSlidingMenu();
		slidingMenu.setMode(SlidingMenu.LEFT);
	//	slidingMenu.setShadowDrawable(R.drawable.shadow);
		setBehindContentView(R.layout.sliding_menu_frame);
		setContentView(R.layout.sliding_content_frame);
		slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		slidingMenu.setShadowDrawable(R.drawable.shadow);
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		slidingMenu.setFadeDegree(0.35f);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		menu = new MenuFragment();
		if(savedInstanceState==null){
		getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame,menu).commitAllowingStateLoss();		
		UIFragmentManager.getInstance().changeFragment(null,MainFragment.class, false, null, R.id.content_frame);
		}

		initTitleViews();
		if(getIntent().getBooleanExtra("remoteLogin", false)&&!isRemoteDialogShow){
			showConflictDialog();
			
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult( requestCode,  resultCode,  data);
		LogUtil.info(HomeActivity.class, "收到result"+resultCode);
	
	}
	public void refeshAvatar(){
		menu.refreshAvatar();
		Fragment main=UIFragmentManager.getInstance().getFragment(MainFragment.class.getName());
		MainFragment m=(MainFragment)main;
		m.refreshAvatar();
	}
	private void initTitleViews() {
		main_title_text=(TextView) findViewById(R.id.main_title_text);
		  Typeface fontFace = Typeface.createFromAsset(getAssets(),
                  "fashion_black.ttf");
		  main_title_text.setTypeface(fontFace);
		  btn_user = (ImageButton) findViewById(R.id.btn_user);
		  btn_user.setOnClickListener(this);
		  btn_message=(ImageButton) findViewById(R.id.btn_message);
		  btn_message.setOnClickListener(this);
		  new_msg_count = (TextView) findViewById(R.id.new_msg_count);
		
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Fragment main=UIFragmentManager.getInstance().getFragment(MainFragment.class.getName());
			if(main!=null&&main.isVisible()){
		    if(keyCode == KeyEvent.KEYCODE_BACK){
		           if((System.currentTimeMillis() - mkeyTime) > 2000){
		           mkeyTime = System.currentTimeMillis();
		           Toast.makeText(this, R.string.pre_leave, Toast.LENGTH_SHORT).show();
		   }else{
			   moveTaskToBack(true);
				return true;
		   }

		 }
		           return true;
		 
	}else{
		//MacthFriendFragment mach=(MacthFriendFragment)UIFragmentManager.getInstance().getFragment(MacthFriendFragment.class.getName());
		/*if(mach!=null){
			mach.onKeyDow();
		return true;
		}*/
	}
			
		    return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		 outState.putBoolean("remoteLogin", remoteLogin);
		super.onSaveInstanceState(outState);
	}
	@Override
	protected void onResume() {
		super.onResume();
		if(!remoteLogin&&!isRemoteDialogShow){
		updateUnreadLabel();
		}
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ChatManager.NEW_MESSAGE_ACTION);
		intentFilter.addAction(ChatManager.NEW_FRIEND_ACTION);
		intentFilter.setPriority(10);
		registerReceiver(newMsgReceiver, intentFilter);
		AotoPreLoadImp prload = (AotoPreLoadImp) AotoPreLoadImp.getInstance();
		prload.pushActivity(this);
	}
	@Override
	protected void onPause() {
	
		super.onPause();
	}
	@Override
	protected void onStop() {
		unregisterReceiver(newMsgReceiver);
		AotoPreLoadImp prload = (AotoPreLoadImp) AotoPreLoadImp.getInstance();
		prload.popActivity(this);
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		LogUtil.info(HomeActivity.class, "home销毁了");
		
		super.onDestroy();
	}
	@Override
	public void onClick(View v) {
		Intent intent;
		switch(v.getId()){
		case R.id.btn_message:
			intent=new Intent();
			intent.setClass(HomeActivity.this, MessageActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_user:
			slidingMenu.showMenu();
			break;
			
		}
	}
	private  class NewMsgBroadcastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
				String msgId=intent.getStringExtra("msgid");
						abortBroadcast();
						updateUnreadLabel();
						AotoPreLoad.getInstance().getNotifier().onNewMsg(ChatManager.getInstance().getMsgDao().findMsgByUUID(msgId));
			
		}
		
		
		
	}
	/**
	 * 刷新未读消息数
	 */
	public void updateUnreadLabel() {
		int count = getUnreadMsgCountTotal();
		if (count > 0) {
			new_msg_count.setText(String.valueOf(count));
			new_msg_count.setVisibility(View.VISIBLE);
		} else {
			new_msg_count.setVisibility(View.INVISIBLE);
		}
	}
	/**
	 * 获取未读消息数
	 * 
	 * @return
	 */
	public int getUnreadMsgCountTotal() {
		int unreadMsgCountTotal = 0;
		unreadMsgCountTotal = ChatManager.getInstance().getAllUnreadMsgCount();
		return unreadMsgCountTotal;
	}
	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		super.onNewIntent(intent);
		if(intent.getBooleanExtra("remoteLogin", false)&&!isRemoteDialogShow)
				showConflictDialog();
		
		if(intent.getStringExtra("from")!=null&&intent.getStringExtra("from").equals("regist")){
			Intent info=new Intent();
			info.setClass(HomeActivity.this, Regist2Activity.class);
			startActivity(info);
		}
	}
	private android.app.AlertDialog.Builder remoteBuilder;
	private boolean isRemoteDialogShow;
	private MenuFragment menu;
	private void showConflictDialog(){
		isRemoteDialogShow=true;
		if (!HomeActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (remoteBuilder == null)
					remoteBuilder = new android.app.AlertDialog.Builder(HomeActivity.this);
				remoteBuilder.setTitle("下线通知");
				remoteBuilder.setMessage(R.string.connect_conflict);
				remoteBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						remoteBuilder = null;
						finish();
						startActivity(new Intent(HomeActivity.this, LoginActivity.class));
					}
				});
				remoteBuilder.setCancelable(false);
				remoteBuilder.create().show();
				remoteLogin = true;
			} catch (Exception e) {
			}

		}
		
		
		
	}

}
