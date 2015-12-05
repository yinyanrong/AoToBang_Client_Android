package com.aotobang.app.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aotobang.annotation.BaseFragment;
import com.aotobang.annotation.ContentView;
import com.aotobang.annotation.FindViewNoOnClick;
import com.aotobang.app.R;
import com.aotobang.app.activity.InteractActivity;
import com.aotobang.app.applaction.AotoBangApp;
import com.aotobang.app.callback.IBlueDataCom;
import com.aotobang.app.device.control.CommonDeviceControl;
import com.aotobang.app.device.control.DeviceControl;
import com.aotobang.app.manager.AotoBlueToothManager;
import com.aotobang.bluetooth.AotoGattAttributes;
import com.aotobang.bluetooth.bean.Vibrate;
import com.aotobang.net.InterfaceIds;
import com.aotobang.net.entity.AotoRequest;
@ContentView(R.layout.interact_default)
public class DefCommonInteractFragment extends DefaultInteractFragment {
	/*
	private Runnable runnable=new VibrateRunnable();
	private static int total;
	private static int point;
	private List<Vibrate> list=new ArrayList<Vibrate>();*/
	//private Handler handler=new Handler();
	//public static boolean isRun=false;
	private View shakeView;
	private Animation shake;
	private int shakePostion=-1;
	private String toUserId;
	public static String[] names=new String[]{"自然清新","十分刺激","先苦后甜","平分秋色","九浅一深","七上八下","少女悸动","异常猛烈"};
	public static int[]   imgs=new int[]{R.drawable.grid_img1,R.drawable.grid_img2,R.drawable.grid_img3,R.drawable.grid_img4,R.drawable.grid_img5,
		R.drawable.grid_img6,R.drawable.grid_img7,R.drawable.grid_img8};
	//private CommonDeviceControl control;
	@FindViewNoOnClick(R.id.interact_grid)
	private GridView interact_grid;
		@Override
		public void onClick(View v) {
		}


		@Override
		public void handleDataResultOnSuccee(int responseId, Object data) {

		}

		
		@Override
		public void initView() {
			//InteractActivity  ac=(InteractActivity)getActivity();
			//control=(CommonDeviceControl) ac.getDeviceControl();
			shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
			shake.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					if(shakeView!=null)
					shakeView.startAnimation(shake);
					
				}
			});
			interact_grid.setAdapter(new GridAdapter());
			interact_grid.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
					if(InteractActivity.haveDevice==0){
					Toast.makeText(getActivity(), "对方没有设备", 0).show();
					return;
					}
					if(shakePostion==position){
					//	isRun=false;
						//control.setRunStatus(false);
						if(shakeView!=null){
							shakeView.clearAnimation();
							shakeView=null;
						}
						shakePostion=-1;
						sendDefaultControlRequest(0+"");
					}else{
						shakePostion=position;
						//isRun=false;
						//control.setRunStatus(false);
						if(shakeView!=null)
							shakeView.clearAnimation();
						shakeView=view;
						shakeView.startAnimation(shake);
						sendDefaultControlRequest(position+1+"");
					}
				}
			});
		}
		public void sendDefaultControlRequest(String data){
			 AotoRequest request=AotoRequest.createRequest(InterfaceIds.INTERACTION_BLUETOOTH);//蓝牙命令
		   		Map<String,Object> map=new HashMap<String, Object>();
		   		map.put("sessionId", AotoBangApp.getInstance().getSessionId());
		 		map.put("to", toUserId);
		 		map.put("content", data);
		 		map.put("type", 1);
		   		request.setParameters(map);
		   		request.setCallBack(DefCommonInteractFragment.this);
				sendRequest(request);
		}
@Override
public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	toUserId=getArguments().getString("toUserId");
}
	
	private class GridAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			
			return names.length;
		}

		@Override
		public Object getItem(int position) {
			
			return null;
		}

		@Override
		public long getItemId(int position) {
			
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder  holder;
			if(convertView==null){
				holder=new ViewHolder();
				convertView=View.inflate(getActivity(), R.layout.toy_grid_item, null);
				holder.itemImg=(ImageView) convertView.findViewById(R.id.itemImg);
				holder.itemText=(TextView) convertView.findViewById(R.id.itemText);
				convertView.setTag(holder);
				
			}else{
				holder=(ViewHolder) convertView.getTag();
				
			}
			holder.itemText.setText(names[position]);
			holder.itemImg.setImageResource(imgs[position]);
			
			
			return convertView;
		}
	}
	
	private class ViewHolder{
		private ImageView itemImg;
		private TextView itemText;
		
		
	}
	//@Override
//	public void onData(String data) {
	/*	int type=Integer.valueOf(data);
		byte[] 	arrayOfByte=AotoGattAttributes.initArrayOfByte(0);
		if(AotoBlueToothManager.getInstance().getDeviceName().equals(AotoGattAttributes.SMART_MINI_VIBE)){
			Vibrate vibrate1;
			Vibrate vibrate2;
			Vibrate vibrate3;
			switch(type){
			case 0:
				isRun=false;
				arrayOfByte=AotoGattAttributes.initArrayOfByte(0);
				AotoBlueToothManager.getInstance().getCurrentCharacteristic().setValue(arrayOfByte);
				AotoBlueToothManager.getInstance().wirteCharacteristic();
				break;
			case 1:
				arrayOfByte=AotoGattAttributes.initArrayOfByte(20);
				AotoBlueToothManager.getInstance().getCurrentCharacteristic().setValue(arrayOfByte);
				AotoBlueToothManager.getInstance().wirteCharacteristic();
				break;
			case 2:
				arrayOfByte=AotoGattAttributes.initArrayOfByte(60);
				AotoBlueToothManager.getInstance().getCurrentCharacteristic().setValue(arrayOfByte);
				AotoBlueToothManager.getInstance().wirteCharacteristic();
				break;
			case 3:
				list.clear();
				 vibrate1=new Vibrate(10,1000);
				 vibrate2=new Vibrate(0,300);
				 vibrate3=new Vibrate(80,1000);
				list.add(vibrate1);
				list.add(vibrate2);
				list.add(vibrate3);
				shaking(list);
				break;
			case 4:
				list.clear();
				 vibrate1=new Vibrate(10,500);
				 vibrate2=new Vibrate(0,300);
				 vibrate3=new Vibrate(80,300);
				list.add(vibrate1);
				list.add(vibrate2);
				list.add(vibrate3);
				shaking(list);
				break;
			case 5:
				list.clear();
				 vibrate1=new Vibrate(20,200);
				 vibrate2=new Vibrate(0,100);
				 vibrate3=new Vibrate(80,500);
				list.add(vibrate1);
				list.add(vibrate2);
				list.add(vibrate3);
				shaking(list);
				break;
			case 6:
				list.clear();
				 vibrate1=new Vibrate(60,700);
				 vibrate2=new Vibrate(0,100);
				 vibrate3=new Vibrate(20,100);
				list.add(vibrate1);
				list.add(vibrate2);
				list.add(vibrate3);
				shaking(list);
				break;
			case 7:
				list.clear();
				 vibrate1=new Vibrate(60,700);
				 vibrate2=new Vibrate(0,100);
				 vibrate3=new Vibrate(80,300);
				list.add(vibrate1);
				list.add(vibrate2);
				list.add(vibrate3);
				shaking(list);
				break;
			case 8:
				list.clear();
				 vibrate1=new Vibrate(60,700);
				 vibrate2=new Vibrate(0,100);
				 vibrate3=new Vibrate(20,100);
				list.add(vibrate1);
				list.add(vibrate2);
				list.add(vibrate3);
				shaking(list);
				break;
			}
		}else if(AotoBlueToothManager.getInstance().getDeviceName().equals(AotoGattAttributes.WOLKAMO_BIU)||AotoBlueToothManager.getInstance().getDeviceName().equals(AotoGattAttributes.WOLKAMO_MONA)){
			switch(type){
			case 0:
				arrayOfByte=new byte[]{0};
				break;
			case 1:
				arrayOfByte=new byte[]{1};
				break;
			case 2:
				arrayOfByte=new byte[]{2};
				break;
			case 3:
				arrayOfByte=new byte[]{3};
				break;
			case 4:
				arrayOfByte=new byte[]{4};
				break;
			case 5:
				arrayOfByte=new byte[]{5};
				break;
			case 6:
				arrayOfByte=new byte[]{6};
				break;
			case 7:
				arrayOfByte=new byte[]{7};
				break;
			case 8:
				arrayOfByte=new byte[]{8};
				break;
			}
			AotoBlueToothManager.getInstance().getCurrentCharacteristic().setValue(arrayOfByte);
			AotoBlueToothManager.getInstance().wirteCharacteristic();
		}else if(AotoBlueToothManager.getInstance().getDeviceName().equals(AotoGattAttributes.IBALL)){
			Vibrate vibrate1;
			Vibrate vibrate2;
			Vibrate vibrate3;
			switch(type){
			case 0:
				isRun=false;
				arrayOfByte=AotoGattAttributes.initIBallData(0);
				AotoBlueToothManager.getInstance().getCurrentCharacteristic().setValue(arrayOfByte);
				AotoBlueToothManager.getInstance().wirteCharacteristic();
				break;
			case 1:
				list.clear();
				 vibrate1=new Vibrate(-1,1000);
				 vibrate2=new Vibrate(0,300);
				 vibrate3=new Vibrate(-1,1000);
				list.add(vibrate1);
				list.add(vibrate2);
				list.add(vibrate3);
				shaking(list);
				break;
			case 2:
				list.clear();
				 vibrate1=new Vibrate(-1,500);
				 vibrate2=new Vibrate(-1,300);
				 vibrate3=new Vibrate(0,300);
				list.add(vibrate1);
				list.add(vibrate2);
				list.add(vibrate3);
				shaking(list);
				break;
			case 3:
				list.clear();
				 vibrate1=new Vibrate(-1,500);
				 vibrate2=new Vibrate(-1,600);
				 vibrate3=new Vibrate(0,300);
				list.add(vibrate1);
				list.add(vibrate2);
				list.add(vibrate3);
				shaking(list);
				break;
			case 4:
				list.clear();
				 vibrate1=new Vibrate(-1,500);
				 vibrate2=new Vibrate(-1,600);
				 vibrate3=new Vibrate(0,300);
				list.add(vibrate1);
				list.add(vibrate2);
				list.add(vibrate3);
				shaking(list);
				break;
			case 5:
				list.clear();
				 vibrate1=new Vibrate(-1,500);
				 vibrate2=new Vibrate(-1,600);
				 vibrate3=new Vibrate(0,800);
				list.add(vibrate1);
				list.add(vibrate2);
				list.add(vibrate3);
				shaking(list);
				break;
			case 6:
				list.clear();
				 vibrate1=new Vibrate(-1,300);
				 vibrate2=new Vibrate(-1,600);
				 vibrate3=new Vibrate(0,200);
				list.add(vibrate1);
				list.add(vibrate2);
				list.add(vibrate3);
				shaking(list);
				break;
			case 7:
				list.clear();
				 vibrate1=new Vibrate(-1,1000);
				 vibrate2=new Vibrate(-1,600);
				 vibrate3=new Vibrate(0,200);
				list.add(vibrate1);
				list.add(vibrate2);
				list.add(vibrate3);
				shaking(list);
				break;
			case 8:
				arrayOfByte=AotoGattAttributes.initIBallData(-1);
				AotoBlueToothManager.getInstance().getCurrentCharacteristic().setValue(arrayOfByte);
				AotoBlueToothManager.getInstance().wirteCharacteristic();
				break;
			
			}
			
		}*/
		
		
		
	//}
	
	/*private void shaking(final List<Vibrate> list){
		isRun=true;
		total=list.size();
		handler.post(runnable);
	}
	
	public class VibrateRunnable implements Runnable {
		private byte[]  arrayOfByte;
		@Override
		public void run() {
			if(isRun){
			Vibrate v=list.get(point);
					if(AotoBlueToothManager.getInstance().getDeviceName().equals(AotoGattAttributes.SMART_MINI_VIBE))
					 arrayOfByte=AotoGattAttributes.initArrayOfByte(v.getRate());
					else
						arrayOfByte=AotoGattAttributes.initIBallData(v.getRate());
					    AotoBlueToothManager.getInstance().getCurrentCharacteristic().setValue(arrayOfByte);
					    AotoBlueToothManager.getInstance().wirteCharacteristic();
						if(point==total-1)
							point=0;
						else
						point++;
						handler.postDelayed(runnable,v.getTime());
					
			}
			
		}

	}*/
	@Override
	public void stopAnimation(){
		//isRun=false;
		//handler.removeCallbacks(runnable);
	//	control.stop();
		if(shakeView!=null){
			shakeView.clearAnimation();
			shakeView=null;
		}
		shakePostion=-1;
		
	}
	
}
