package com.aotobang.app.fragment;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.aotobang.annotation.BaseFragment;
import com.aotobang.annotation.ContentView;
import com.aotobang.app.R;
import com.aotobang.app.activity.InteractActivity;
import com.aotobang.app.applaction.AotoBangApp;
import com.aotobang.app.callback.IBleDeviceReadData;
import com.aotobang.app.device.control.CommonDeviceControl;
import com.aotobang.app.device.control.DeviceControl;
import com.aotobang.app.manager.AotoBlueToothManager;
import com.aotobang.bluetooth.AotoGattAttributes;
import com.aotobang.net.InterfaceIds;
import com.aotobang.net.entity.AotoRequest;
import com.aotobang.utils.LogUtil;
@ContentView(R.layout.interact_real)
public class RealInteractFragment extends BaseFragment {
	private DeviceControl  control;
	//public static boolean  realMan=false;
	private String toUserId;
	private static int shakeCount;
	/*private Runnable stopRunnable=new Runnable() {
		
		@Override
		public void run() {
			if(AotoBlueToothManager.getInstance().isConnected())
			AotoBlueToothManager.getInstance().stopDevice();
			
			
		}
	};*/
	private long nowtime=0;
	public Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			//模式：1,2,4,7
			int count=msg.what;
			LogUtil.info(RealInteractFragment.class, "count---"+count);
			shakeCount=0;
		
			
				AotoRequest request=new AotoRequest();
	      		request.setRequestId(InterfaceIds.INTERACTION_BLUETOOTH);
	      		Map<String,Object> map=new HashMap<String, Object>();
	      		if(AotoBlueToothManager.getInstance().getDeviceName().equals(AotoGattAttributes.WOLKAMO_MONA)||AotoBlueToothManager.getInstance().getDeviceName().equals(AotoGattAttributes.WOLKAMO_BIU)){
	      		if(count<5)
	      			map.put("content", "0");
	      		else if(count<6&&count>2)
	      			map.put("content", "2");
	      		else if(count<10&&count>6)
	      			map.put("content", "3");
	      		else if(count<14&&count>10)
	      			map.put("content", "4");
	      		else
	      			map.put("content", "7");
	      		
	      		}else if(AotoBlueToothManager.getInstance().getDeviceName().equals(AotoGattAttributes.IBALL)){
	      			LogUtil.info(RealInteractFragment.class, "pre---"+pre);
	      			if(pre<1000)
	      				map.put("content", "0");
	      			else
	      				map.put("content", "3");
	      		}else if(AotoBlueToothManager.getInstance().getDeviceName().startsWith("LVS")){
	      			map.put("content", count+"");
	      			
	      			
	      		}
	      		
	      		map.put("type", 2);
	      		map.put("sessionId", AotoBangApp.getInstance().getSessionId());
	    		map.put("to", toUserId);
	      		request.setParameters(map);
	      		request.setCallBack(RealInteractFragment.this);
	      		 sendRequest(request); 
	      		 
	      		 
			
			
		};
		
	};
	private IBleDeviceReadData  listener=new IBleDeviceReadData(){
		public void OnData(String data) {
			displayData(data);
		};
		
	};
	/*public void stop(){
		//realMan=false;
		byte[]  data = new byte[]{0};
		if(AotoBlueToothManager.getInstance().getDeviceName().equals(AotoGattAttributes.SMART_MINI_VIBE)){
			data=AotoGattAttributes.initArrayOfByte(0);
			
		}else if(AotoBlueToothManager.getInstance().getDeviceName().equals(AotoGattAttributes.IBALL)){
			data=new byte[5];
			data[0] = (byte) 65440;
			data[1] = 0;
			data[2] = 0;
			data[3] = -1;
			data[4] = -1;
			
		}
		AotoBlueToothManager.getInstance().getCurrentCharacteristic().setValue(data);
		AotoBlueToothManager.getInstance().wirteCharacteristic();
		
	}*/
	public void  start(){
		control.setRealMode(true);
	//	realMan=true;
		new Thread(){
			@Override
			public void run() {
				
				while(control.isRealMode()){
					
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if(System.currentTimeMillis()-nowtime>1000)
						AotoBlueToothManager.getInstance().openRealMode();
					handler.sendEmptyMessage(shakeCount);
				}
			}
			
			
		}.start();
	}
	@Override
	public void onClick(View v) {
	}

	@Override
	public void handleDataResultOnSuccee(int responseId, Object data) {
	}

	@Override
	public void initView() {
		control=((InteractActivity)getActivity()).getDeviceControl();
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		toUserId=getArguments().getString("toUserId");
	}
	@Override
	public void onResume() {
		AotoBlueToothManager.getInstance().registOnDeviceDataListener(listener);
		super.onResume();
		LogUtil.info(RealInteractFragment.class, "RealInteractFragment---onResume");
	}
	@Override
	public void onPause() {
	
		super.onPause();
		LogUtil.info(RealInteractFragment.class, "RealInteractFragment---onPause");
	}
	@Override
	public void onDestroy() {
		AotoBlueToothManager.getInstance().unRegistOnDeviceDataListener(listener);
		super.onDestroy();
	}
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if(hidden)
			LogUtil.info(RealInteractFragment.class, "RealInteractFragment---隐藏");
		else
			LogUtil.info(RealInteractFragment.class, "RealInteractFragment---显示");
	}

	/*@Override
	public void onData(String netData) {
		
		byte[]  data = new byte[]{0};
		int i=Integer.valueOf(netData);
		if(AotoBlueToothManager.getInstance().getDeviceName().equals(AotoGattAttributes.IBALL)){
			if(i!=0){
				data=new byte[5];
				data[0] = (byte) 65440;
				data[1] = -1;
				data[2] = 0;
				data[3] = -1;
				data[4] = -1;
				}else{
					data=new byte[5];
					data[0] = (byte) 65440;
					data[1] = 0;
					data[2] = 0;
					data[3] = -1;
					data[4] = -1;
					
				}
		}else if(AotoBlueToothManager.getInstance().getDeviceName().equals(AotoGattAttributes.SMART_MINI_VIBE)){
				switch(i){
				case 0:
					data=AotoGattAttributes.initArrayOfByte(0);
					break;
				case 1:
					data=AotoGattAttributes.initArrayOfByte(10);
					break;
				case 2:
					data=AotoGattAttributes.initArrayOfByte(20);
					break;
				case 3:
					data=AotoGattAttributes.initArrayOfByte(30);
					break;
				case 4:
					data=AotoGattAttributes.initArrayOfByte(40);
					break;
				case 5:
					data=AotoGattAttributes.initArrayOfByte(50);
					break;
				case 6:
					data=AotoGattAttributes.initArrayOfByte(60);
					break;
				case 7:
					data=AotoGattAttributes.initArrayOfByte(70);
					break;
				case 8:
					data=AotoGattAttributes.initArrayOfByte(80);
					break;
				
				
				}
			
		}else if(AotoBlueToothManager.getInstance().getDeviceName().equals(AotoGattAttributes.WOLKAMO_BIU)||AotoBlueToothManager.getInstance().getDeviceName().equals(AotoGattAttributes.WOLKAMO_MONA)){
			
			switch(i){
			
			case 0:
				data=new byte[]{0};
				
				break;
			case 1:
				data=new byte[]{1};
				break;
			case 2:
				data=new byte[]{2};
				break;
			case 3:
				data=new byte[]{4};
				break;
			case 4:
				
				data=new byte[]{4};
				break;
			case 7:
				data=new byte[]{7};
				break;
				
				
			}
			
		}
		AotoBlueToothManager.getInstance().getCurrentCharacteristic().setValue(data);
		AotoBlueToothManager.getInstance().wirteCharacteristic();
		handler.removeCallbacks(stopRunnable);
		handler.postDelayed(stopRunnable, 1500);
	}*/
	int pre;
	private void displayData(String data) {
		if(AotoBlueToothManager.getInstance().getDeviceName().startsWith("IBal")){
			if (data != null) {
				String str=data.replace(" ", "");
				String str2=str.substring(6, 12);
				String num=new BigInteger(str2, 16).toString();
				int i=Integer.valueOf(num)/1000;
				pre=i;
		}
		}else if(AotoBlueToothManager.getInstance().getDeviceName().startsWith("LVS-")){
			
			if(data!=null){
				nowtime=System.currentTimeMillis();
				String str=data.replace(" ", "");
				String num=new BigInteger(str,16).toString();
				int i=Integer.valueOf(num.substring(0, 7));
				LogUtil.info(RealInteractFragment.class, i+"-------");
				if(Math.abs(i-pre)>10&&Math.abs(i-pre)<1000)
					shakeCount++;
					 pre=i;
				
			}
		}else{
			
		if (data != null) {
			String str=data.replace(" ", "");
		//LogUtil.info(RealInteractFragment.class, "读取到设备数据--"+str);
			int i=Integer.parseInt(new BigInteger(str, 16).toString())/1000;
			if(Math.abs(i-pre)>50)
			shakeCount++;
			
			 pre=i;
			
		}
		
		}
	}

}
