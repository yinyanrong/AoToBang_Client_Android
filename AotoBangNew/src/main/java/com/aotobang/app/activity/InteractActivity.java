package com.aotobang.app.activity;

import java.util.HashMap;
import java.util.Map;

import lib.com.astuetz.PagerSlidingTabStrip;

import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCallParams;
import org.linphone.core.LinphoneCore;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.aotobang.app.GlobalParams;
import com.aotobang.app.R;
import com.aotobang.app.applaction.AotoBangApp;
import com.aotobang.app.callback.IBleDisConnect;
import com.aotobang.app.callback.IBlueDataCom;
import com.aotobang.app.callback.InteractCallBack;
import com.aotobang.app.device.control.CommonDeviceControl;
import com.aotobang.app.device.control.DeviceControl;
import com.aotobang.app.device.control.LVSDeviceControl;
import com.aotobang.app.entity.Interact;
import com.aotobang.app.fragment.DefLVSInteractFragment;
import com.aotobang.app.fragment.DefCommonInteractFragment;
import com.aotobang.app.fragment.DefaultInteractFragment;
import com.aotobang.app.fragment.RealInteractFragment;
import com.aotobang.app.fragment.VoiceInteractFragment;
import com.aotobang.app.manager.AotoBlueToothManager;
import com.aotobang.app.manager.ChatManager;
import com.aotobang.app.phone.CallComingListener;
import com.aotobang.app.phone.LinphoneManager;
import com.aotobang.app.phone.LinphoneService;
import com.aotobang.app.phone.LinphoneUtils;
import com.aotobang.app.view.CircleImageView;
import com.aotobang.bluetooth.AotoGattAttributes;
import com.aotobang.net.AotoNetEngineProxy;
import com.aotobang.net.InterfaceIds;
import com.aotobang.net.callback.IDataCallBack;
import com.aotobang.net.entity.AotoRequest;
import com.aotobang.utils.LogUtil;
import com.aotobang.utils.UserUtils;
public class InteractActivity extends FragmentActivity implements OnClickListener{
private PagerSlidingTabStrip tabs;
private ViewPager pager;
private Fragment defaultFragment;
private RealInteractFragment real;
private Fragment voice;
private InteractAdapter adapter;
private DeviceControl  deviceControl;
/** 
 * 获取当前屏幕的密度 
 */  
private DisplayMetrics dm;  
private String toUserId;
public  static int haveDevice=10;
private static int currentMode=0;
private LinphoneCall mCall;
private boolean isSpeakerEnabled = false;
private boolean isMicMuted = false;
private String userName;

@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		userName=getIntent().getStringExtra("userName");
		Toast.makeText(InteractActivity.this, "您已进入实时语音对话模式", Toast.LENGTH_SHORT).show();
		LinphoneService.instance().registIncomingListener(new CallComingListener() {

			@Override
			public void onCallComing(LinphoneCore lc, LinphoneCall call, org.linphone.core.LinphoneCall.State state, String message) {
				LogUtil.info(InteractActivity.class, state.value()+"state--");
				mCall=call;
				GlobalParams.mCall=call;
				answer();
			}
		});
		isMicMuted = LinphoneManager.getLc().isMicMuted();
		haveDevice=getIntent().getIntExtra("haveDevice", 10);
		toUserId=getIntent().getStringExtra("toUserId");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_interact);
		 dm = getResources().getDisplayMetrics();  
		tabs=(PagerSlidingTabStrip) findViewById(R.id.tabs);
		pager=(ViewPager) findViewById(R.id.pager);
		spk = (ImageView) findViewById(R.id.spk);
		spk.setOnClickListener(InteractActivity.this);
		mic = (ImageView) findViewById(R.id.mic);
		mic.setOnClickListener(InteractActivity.this);
		btn_back = (ImageButton) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(InteractActivity.this);
		CircleImageView he=(CircleImageView) findViewById(R.id.interact_avatar_he);
		CircleImageView me=(CircleImageView) findViewById(R.id.interact_avatar_me);
		UserUtils.setAvatar(me, true);
		UserUtils.setPeerAvatar(he, toUserId, null,null);
		if(isSpeakerEnabled)
			spk.setImageResource(R.drawable.spk_on);
		else
			spk.setImageResource(R.drawable.spk_off);
		
		if(isMicMuted)
			mic.setImageResource(R.drawable.mic_on);
		else
			mic.setImageResource(R.drawable.mic_off);
		
		if(!isSpeakerEnabled)
			toggleSpeaker();
		Bundle b = new Bundle();
		b.putString("toUserId", toUserId);
		adapter=new InteractAdapter(getSupportFragmentManager());
		LogUtil.info(InteractActivity.class, haveDevice+"--haveDevice");
		if(haveDevice==AotoGattAttributes.DEVICE_TYPE_LVS_A||haveDevice==AotoGattAttributes.DEVICE_TYPE_LVS_B){
			b.putInt("haveDevice", haveDevice);
			defaultFragment=new DefLVSInteractFragment();
		}else{
			defaultFragment = new DefCommonInteractFragment();
		}
		if(AotoBlueToothManager.getInstance().isConnected()){
			if(AotoBlueToothManager.getInstance().getDeviceName().startsWith("LVS")){
				deviceControl=new LVSDeviceControl();
			}else{
				deviceControl=new CommonDeviceControl();
			}
			
			
		}
		
		defaultFragment.setArguments(b);
		real = new RealInteractFragment();
		real.setArguments(b);
		voice = new VoiceInteractFragment();
		voice.setArguments(b);
		pager.setAdapter(adapter);
		setTabsValue();
		tabs.setViewPager(pager);
		tabs.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				currentMode=position;
				
				LogUtil.info(InteractActivity.class,"onPageSelected-position"+position);
				if(position!=0&&!AotoBlueToothManager.getInstance().isConnected()){
					Toast.makeText(InteractActivity.this, "您没连接蓝牙设备", Toast.LENGTH_SHORT).show();
					return;
				}
				if(AotoBlueToothManager.getInstance().getDeviceName().equals(AotoGattAttributes.SMART_MINI_VIBE)||AotoBlueToothManager.getInstance().getDeviceName().startsWith(AotoGattAttributes.XAIAI)||AotoBlueToothManager.getInstance().getDeviceName().startsWith(AotoGattAttributes.XAIAIF)){
					Toast.makeText(InteractActivity.this, "您的设备不支持传感器，只能等待对方动", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(deviceControl!=null&&position==1&&AotoBlueToothManager.getInstance().isConnected()){
					AotoBlueToothManager.getInstance().openRealMode(); 
					real.start();
				}else if(deviceControl!=null&&deviceControl.isRealMode()&&AotoBlueToothManager.getInstance().isConnected()){
					AotoBlueToothManager.getInstance().closeRealMode(); 
					deviceControl.stopDevice();
					
				}
				
				
			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				//	LogUtil.info(InteractActivity.class, position+"-position-"+"positionOffset-"+positionOffset+"-positionOffsetPixels"+positionOffsetPixels);
			}
			
			@Override
			public void onPageScrollStateChanged(int position) {
			}
		});
		
		if(haveDevice==0){
			Toast.makeText(InteractActivity.this, "对方没有设备,只能对方控制你哦", Toast.LENGTH_SHORT).show();
		}
		
		if(userName!=null)
			LinphoneManager.getInstance().newOutgoingCall(userName,null);
		
		AotoBlueToothManager.getInstance().setOnDisConnectListener(new IBleDisConnect() {
			
			@Override
			public void onDisConnect() {
				AotoBlueToothManager.getInstance().setOnDisConnectListener(null);
				Toast.makeText(InteractActivity.this, "蓝牙已断开连接，请重新连接", Toast.LENGTH_SHORT).show();
				finish();
			}
		});
			
	}

/** 
 * 对PagerSlidingTabStrip的各项属性进行赋值。 
 */  
private void setTabsValue() {  
	 // 设置Tab是自动填充满屏幕的  ,必须在setViewPager前设置才生效
    tabs.setShouldExpand(true);  
    // 设置Tab的分割线是透明的  
   // tabs.setDividerColor(Color.TRANSPARENT);  
    // 设置Tab底部线的高度  
    tabs.setUnderlineHeight((int) TypedValue.applyDimension(  TypedValue.COMPLEX_UNIT_DIP, 1, dm));  
    // 设置Tab Indicator的高度  
    tabs.setIndicatorHeight((int) TypedValue.applyDimension(  TypedValue.COMPLEX_UNIT_DIP, 4, dm));  
    // 设置Tab标题文字的大小  
    tabs.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 13, dm));  
    // 设置Tab Indicator的颜色  
    tabs.setIndicatorColorResource(R.color.main_text_color_red);
    // 设置选中Tab文字的颜色 
    tabs.setSelectedTextColorResource(R.color.main_text_color_red) ;
    // 取消点击Tab时的背景色  
    tabs.setTabBackground(0);  
   
}  
private void toggleMicro() {
	LinphoneCore lc = LinphoneManager.getLc();
	isMicMuted = !isMicMuted;
	lc.muteMic(isMicMuted);
	if (isMicMuted) {
		mic.setImageResource(R.drawable.mic_off);
	} else {
		mic.setImageResource(R.drawable.mic_on);
	}
}
private void toggleSpeaker() {
	isSpeakerEnabled = !isSpeakerEnabled;
	if (isSpeakerEnabled) {
		LinphoneManager.getInstance().routeAudioToSpeaker();
		LinphoneManager.getLc().enableSpeaker(isSpeakerEnabled);
		spk.setImageResource(R.drawable.spk_on);
	} else {
		LinphoneManager.getInstance().routeAudioToReceiver();
		spk.setImageResource(R.drawable.spk_off);
	}
}
@Override
	protected void onResume() {
	ChatManager.getInstance().registReceiveInteractListener(interactCallBack);
		super.onResume();
		
		
		
	}
private void  answer(){
	LinphoneCallParams params = LinphoneManager.getLc().createDefaultCallParameters();
	boolean isLowBandwidthConnection = !LinphoneUtils.isHightBandwidthConnection(InteractActivity.this);
	if (isLowBandwidthConnection) {
		params.enableLowBandwidth(true);
	}
	LinphoneManager.getInstance().acceptCallWithParams(mCall, params);
	
}
@Override
	protected void onPause() {
	super.onPause();
	
		
	}
@Override
	protected void onDestroy() {
	super.onDestroy();
	ChatManager.getInstance().unRegistReceiveInteractListener();
	LinphoneManager.getLc().terminateCall(mCall);
	if(deviceControl!=null&&deviceControl.isRealMode())
		deviceControl.stopDevice();
	 AotoRequest request=AotoRequest.createRequest(InterfaceIds.INTERACTION_BLUETOOTH);
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("sessionId", AotoBangApp.getInstance().getSessionId());
		map.put("to", toUserId);
		map.put("content", "");
		map.put("type", 4);
		request.setParameters(map);//蓝牙指令
		request.setCallBack(new IDataCallBack() {
			@Override
			public void handleDataResultOnSuccee(int responseId, Object data) {
			}
			
			@Override
			public void handleDataResultOnError(int responseId, int errCode, Object error) {
			}
		});
		AotoNetEngineProxy.getInstance(InteractActivity.this).sendReqeust(request);
		if(currentMode==0){
			((DefaultInteractFragment) defaultFragment).stopAnimation();
			if(deviceControl!=null){
			deviceControl.stopDevice();
			LogUtil.info(InteractActivity.class, "stop");
			}
			
		}
		
		/*if(AotoBlueToothManager.getInstance().getDeviceName().startsWith("LVS-"))
			AotoBlueToothManager.getInstance().stopLVSDevice();
		else
		AotoBlueToothManager.getInstance().stopDevice();*/
	
	}
private IBlueDataCom  bdc=null;
private InteractCallBack  interactCallBack=new InteractCallBack() {
	@Override
	public void OnInteractMsg(Interact msg) {
		LogUtil.info(InteractActivity.class, msg.getData());
		switch(msg.getType()){
		case 1:
			//bdc=(IBlueDataCom)defaultFragment;
			//bdc.onData(msg.getData());
			deviceControl.control(msg.getData());
			break;
		case 2:
		//	bdc=(IBlueDataCom)real;
			//bdc.onData(msg.getData());
			deviceControl.realControl(msg.getData());
			break;
		case 3:
			break;
		case 4:
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(InteractActivity.this, "对方已离开互动", Toast.LENGTH_SHORT).show();
					finish();
				}
			});
			break;
		
		}
		
		
		
		
	}
};
private ImageView mic;
private ImageView spk;
private ImageButton btn_back;
	private class InteractAdapter extends FragmentPagerAdapter{
		public InteractAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		private  String[] titles = { "预设互动", "互相传感","声控讲故事"};
		

		@Override
		public Fragment getItem(int position) {
			switch(position){
			case 0:
				return defaultFragment;
			case 1:
				return real;
			case 2:
				return voice;
			
			
			}
			return null;
		}
		@Override
		public CharSequence getPageTitle(int position) {
			
			return titles[position];
		}
		@Override
		public int getCount() {
			
			return titles.length;
		}
		
		
		
		
	}
	@Override
		public void onBackPressed() {
		setResult(Activity.RESULT_OK);
			super.onBackPressed();
		}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.spk:
			toggleSpeaker();
			break;
		case R.id.mic:
			toggleMicro();
			break;
		case R.id.btn_back:
			setResult(Activity.RESULT_OK);
			finish();
			break;
		
		
		}
	}
	public DeviceControl getDeviceControl(){
		return deviceControl;
	}

}
