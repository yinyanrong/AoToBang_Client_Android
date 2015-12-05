package com.aotobang.app.fragment;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aotobang.annotation.BaseFragment;
import com.aotobang.annotation.ContentView;
import com.aotobang.annotation.FindViewNoOnClick;
import com.aotobang.annotation.FindViewOnClick;
import com.aotobang.app.R;
import com.aotobang.app.applaction.AotoBangApp;
import com.aotobang.app.callback.IBlueDataCom;
import com.aotobang.app.manager.AotoBlueToothManager;
import com.aotobang.bluetooth.AotoGattAttributes;
import com.aotobang.bluetooth.AotoGattAttributes.Mode;
import com.aotobang.net.InterfaceIds;
import com.aotobang.net.entity.AotoRequest;
import com.aotobang.utils.LogUtil;
@ContentView(R.layout.activity_single_lvs)
public class DefLVSInteractFragment extends DefaultInteractFragment {
@FindViewNoOnClick(R.id.rl_high__title)
private RelativeLayout   rl_high__title;
@FindViewOnClick(R.id.vibrate_add)
private ImageView vibrate_add;
@FindViewOnClick(R.id.vibrate_line)
private ImageView vibrate_line;
@FindViewOnClick(R.id.vibrate_subtract)
private ImageView vibrate_subtract;
@FindViewOnClick(R.id.reversal)
private ImageView reversal;
@FindViewOnClick(R.id.second_add)
private ImageView second_add;
@FindViewOnClick(R.id.second_line)
private ImageView second_line;
@FindViewOnClick(R.id.second_subtract)
private ImageView second_subtract;
@FindViewOnClick(R.id.second_text)
private TextView second_text;
@FindViewOnClick(R.id.reversal_text)
private TextView reversal_text;
@FindViewNoOnClick(R.id.title_line)
private View title_line;
private int deviceType=0;
private int vibratMode=0;
private int secondMode=0;

private String toUserId;
private int haveDevice;
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.vibrate_subtract:
			if(vibratMode==0)
				return;
			changeMode(--vibratMode,Mode.Vibrate);
			break;
		case R.id.vibrate_add:
			if(vibratMode==4)
				return;
			changeMode(++vibratMode,Mode.Vibrate);
			break;
		case R.id.second_add:
			Mode mode;
			if(secondMode==4)
				return;
			if(deviceType==0)
				mode=Mode.Rotate;
			else
				mode=Mode.Inflate;
			changeMode(++secondMode,mode);
			break;
		case R.id.second_subtract:
			if(secondMode==0)
				return;
		
			if(deviceType==0)
				mode=Mode.Rotate;
			else
				mode=Mode.Inflate;
			changeMode(--secondMode,mode);
			break;
		case R.id.reversal:
			sendControlRequest("RotateChange;");
			break;
		}
	}
	private void changeMode(int rate,Mode mode){
		String data=AotoGattAttributes.getLVSData(rate, mode);
		sendControlRequest(data);
		if(mode==Mode.Vibrate){
		switch(rate){
		case 0:
			vibrate_line.setImageResource(R.drawable.lvs_line0);
			break;
		case 1:
			vibrate_line.setImageResource(R.drawable.lvs_line1);
			break;
		case 2:
			vibrate_line.setImageResource(R.drawable.lvs_line2);
			break;
		case 3:
			vibrate_line.setImageResource(R.drawable.lvs_line3);
			break;
		case 4:
			vibrate_line.setImageResource(R.drawable.lvs_line4);
			break;
		}
		}else{
			switch(rate){
			case 0:
				second_line.setImageResource(R.drawable.lvs_line0);
				break;
			case 1:
				second_line.setImageResource(R.drawable.lvs_line1);
				break;
			case 2:
				second_line.setImageResource(R.drawable.lvs_line2);
				break;
			case 3:
				second_line.setImageResource(R.drawable.lvs_line3);
				break;
			case 4:
				second_line.setImageResource(R.drawable.lvs_line4);
				break;
			}
			
		}
	}
	
	private void sendControlRequest(String data){
		
		 AotoRequest request=AotoRequest.createRequest(InterfaceIds.INTERACTION_BLUETOOTH);//蓝牙命令
	   		Map<String,Object> map=new HashMap<String, Object>();
	   		map.put("sessionId", AotoBangApp.getInstance().getSessionId());
	 		map.put("to", toUserId);
	 		map.put("content", data);
	 		map.put("type", 1);
	   		request.setParameters(map);
	   		request.setCallBack(DefLVSInteractFragment.this);
			sendRequest(request);
		
		
	}
	@Override
	public void handleDataResultOnSuccee(int responseId, Object data) {
	}
@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		toUserId=getArguments().getString("toUserId");
		haveDevice=getArguments().getInt("haveDevice");
	}
	@Override
	public void initView() {
		title_line.setVisibility(View.GONE);
		rl_high__title.setVisibility(View.GONE);
		if(haveDevice==AotoGattAttributes.DEVICE_TYPE_LVS_B){
			deviceType=1;
			second_add.setImageResource(R.drawable.star_small);
			second_subtract.setImageResource(R.drawable.star_big);
			second_text.setText("充气强度");
			reversal.setVisibility(View.INVISIBLE);
			reversal_text.setVisibility(View.INVISIBLE);
		}else{
			second_add.setImageResource(R.drawable.add);
			second_subtract.setImageResource(R.drawable.subtract);
			second_text.setText("旋转");
			reversal.setVisibility(View.VISIBLE);
			reversal_text.setVisibility(View.VISIBLE);
		}
		
		
	}
	/*@Override
	public void onData(String data) {
		LogUtil.info(DefLVSInteractFragment.class, data);
		AotoBlueToothManager.getInstance().getCurrentCharacteristic().setValue(data);
		AotoBlueToothManager.getInstance().wirteCharacteristic();
	}*/
	@Override
	public void stopAnimation() {
		AotoBlueToothManager.getInstance().stopLVSDevice();
		
	}

}
