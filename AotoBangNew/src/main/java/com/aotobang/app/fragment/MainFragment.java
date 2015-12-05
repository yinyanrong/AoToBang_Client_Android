package com.aotobang.app.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aotobang.annotation.BaseFragment;
import com.aotobang.annotation.ContentView;
import com.aotobang.annotation.FindViewNoOnClick;
import com.aotobang.annotation.FindViewOnClick;
import com.aotobang.app.GlobalParams;
import com.aotobang.app.R;
import com.aotobang.app.activity.ChatActivity;
import com.aotobang.app.activity.Regist2Activity;
import com.aotobang.app.activity.SingleHighActivity;
import com.aotobang.app.activity.SingleLVSActivity;
import com.aotobang.app.activity.ToyActivity;
import com.aotobang.app.applaction.AotoBangApp;
import com.aotobang.app.asyntask.LoadAvatarTask;
import com.aotobang.app.entity.RandomFriendInfo;
import com.aotobang.app.manager.AotoBlueToothManager;
import com.aotobang.app.manager.UIFragmentManager;
import com.aotobang.app.view.CircleImageView;
import com.aotobang.app.view.CircleWaveView;
import com.aotobang.bluetooth.AotoGattAttributes;
import com.aotobang.net.AotoPacket;
import com.aotobang.net.InterfaceIds;
import com.aotobang.net.entity.AotoRequest;
import com.aotobang.utils.LogUtil;
import com.aotobang.utils.UserUtils;
@ContentView(R.layout.fragment_main)
public class MainFragment extends BaseFragment {
	public static final int REQUEST_ENABLE_BT = 1;
	public static final int REQUEST_SINGLE_HIGH = 10;
@FindViewOnClick(R.id.single_high)
private TextView single_high;
@FindViewOnClick(R.id.text_begin_search)	
private TextView text_begin_search;
@FindViewNoOnClick(R.id.text1)	
private TextView text1;
@FindViewNoOnClick(R.id.text2)
private TextView text2;
@FindViewNoOnClick(R.id.waveview)	
private CircleWaveView waveview;
@FindViewNoOnClick(R.id.circle_imageview)	
private CircleImageView circle_imageview;
private BluetoothAdapter mBluetoothAdapter;
private Handler uiHandler=new Handler();
private boolean searching=false;
private List<RandomFriendInfo> infos;
private Runnable stopRunnable=new Runnable() {
	
	@Override
	public void run() {
		stopSearch();
		if(infos!=null&&infos.size()>0){
			Bundle bundle=new Bundle();
		/*	ArrayList list = new ArrayList();
			list.add(infos);*/
			bundle.putSerializable("list", (Serializable) infos);
			UIFragmentManager.getInstance().changeFragment(MainFragment.class,MacthFriendFragment.class, true, bundle, R.id.content_frame);
		}else{
			Toast.makeText(getActivity(), R.string.no_macth_body, Toast.LENGTH_SHORT).show();
			
		}
		
		
	}
};
	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.text_begin_search:
			if(searching){
				stopSearch();
			}else{
				startSearch();
				
			}
			break;
		case R.id.single_high:
			if(AotoBlueToothManager.getInstance().isConnected()){
				Intent intent=new Intent();
				if(AotoBlueToothManager.getInstance().getDeviceName().equals(AotoGattAttributes.LVS_A006)||AotoBlueToothManager.getInstance().getDeviceName().equals(AotoGattAttributes.LVS_B006))
					intent.setClass(getActivity(), SingleLVSActivity.class);
				else
				intent.setClass(getActivity(), SingleHighActivity.class);
				startActivityForResult(intent, REQUEST_SINGLE_HIGH);
			}else{
				  if (!mBluetoothAdapter.isEnabled()) {
		                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		                startActivityForResult(enableBtIntent, MenuFragment.REQUEST_ENABLE_BT);
		            }else{
		            	Intent intent=new Intent();
						intent.setClass(getActivity(), ToyActivity.class);
						startActivity(intent);
		            }
				
				
				
			}
			
			
			
			break;
		
		
		}
		
		
	}

	private void stopSearch() {
		searching=false;
		waveview.setVisibility(View.INVISIBLE);
		waveview.stop();
		text1.setText(getString(R.string.search_your_destined));
		text_begin_search.setText(getString(R.string.start_search));
		uiHandler.removeCallbacks(stopRunnable);
	}

	private void startSearch() {
		searching=true;
		waveview.setVisibility(View.VISIBLE);
		waveview.start();
		text1.setText(getString(R.string.searching));
		text_begin_search.setText(getString(R.string.stop_search));
		AotoRequest request=AotoRequest.createRequest(InterfaceIds.RANDOM_FRIEND);//随机获取在线用户
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("num", 10);
		map.put("sessionId", AotoBangApp.getInstance().getSessionId());
		request.setParameters(map);
		request.setCallBack(MainFragment.this);
		sendRequest(request);
		uiHandler.postDelayed(stopRunnable, 3000);
		
		
		
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	@Override
	public void initView() {
		 BluetoothManager bluetoothManager = (BluetoothManager)getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
	     mBluetoothAdapter = bluetoothManager.getAdapter();
		waveview.setWaveColor(getActivity().getResources().getColor(R.color.main_text_color_red));
		waveview.setVisibility(View.INVISIBLE);
		refreshAvatar();
		
		
	}
public 	void 	refreshAvatar(){
	new LoadAvatarTask().execute(circle_imageview,AotoBangApp.getInstance().getUserId(),AotoBangApp.getInstance().getUserList().get(AotoBangApp.getInstance().getUserId()).getRemote_avatar());
		
	}
	@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			LogUtil.info(MainFragment.class, "MainFragment--onCreate");
		}
	@Override
		public void onStart() {
		LogUtil.info(MainFragment.class, "MainFragment--onStart");
			super.onStart();
		}
	@Override
		public void onResume() {
		LogUtil.info(MainFragment.class, "MainFragment--onResume");
		if(searching)
			startSearch();
			super.onResume();
		}
@Override
	public void onPause() {
	LogUtil.info(MainFragment.class, "MainFragment--onPause");
		super.onPause();
	}
@Override
	public void onHiddenChanged(boolean hidden) {
		if(hidden)
			LogUtil.info(MainFragment.class, "MainFragment--hidden");
		else{
			if(infos!=null)
				infos.clear();
			LogUtil.info(MainFragment.class, "MainFragment--显示了");
			
		}
	}

@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
	super.onActivityResult(requestCode, resultCode, data);
	//LogUtil.info(MainFragment.class, "requestCode"+requestCode+"resultCode"+resultCode+"Activity.RESULT_OK"+Activity.RESULT_OK);
	 if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
		  	Intent intent=new Intent();
			intent.setClass(getActivity(), ToyActivity.class);
			startActivity(intent);
     }else if(requestCode == REQUEST_SINGLE_HIGH && resultCode == Activity.RESULT_OK&&AotoBlueToothManager.getInstance().isConnected()){
    	 			uiHandler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							AotoBlueToothManager.getInstance().stopDevice();
						}
					}, 1000);
    	 
     }
    	 
     
}
@Override
public void handleDataResultOnSuccee(int responseId, Object data) {
	switch(responseId){
	case AotoPacket.Message_Type_ACK|InterfaceIds.RANDOM_FRIEND:
		infos=(ArrayList<RandomFriendInfo>)data;
		LogUtil.info(MainFragment.class,infos.size()+"");
		break;
	
	}
	
	
}

@Override
public void handleDataResultOnError(int responseId, int errCode, final Object error) {
	super.handleDataResultOnError(responseId, errCode, error);
	uiHandler.removeCallbacks(stopRunnable);
	stopSearch();
}

}
