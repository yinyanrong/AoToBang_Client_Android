package com.aotobang.app.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aotobang.annotation.BaseActivity;
import com.aotobang.annotation.ContentView;
import com.aotobang.annotation.FindViewNoOnClick;
import com.aotobang.annotation.FindViewOnClick;
import com.aotobang.app.R;
import com.aotobang.app.fragment.DefCommonInteractFragment;
import com.aotobang.app.manager.AotoBlueToothManager;
import com.aotobang.bluetooth.AotoGattAttributes;
import com.aotobang.bluetooth.bean.Vibrate;
@ContentView(R.layout.activity_single_high)
public class SingleHighActivity extends BaseActivity {
@FindViewOnClick(R.id.btn_back)
private ImageButton btn_back;
@FindViewNoOnClick(R.id.toy_grid)
private GridView toy_grid;

private View shakeView;
private Animation shake;
private int shakePostion=-1;
private List<Vibrate> list=new ArrayList<Vibrate>();
private Handler handler=new Handler();
private Runnable runnable=new VibrateRunnable();
public static boolean isRun=false;
private static int total;
private static int point;
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_back:
			setResult(Activity.RESULT_OK);
			finish();
			break;
			
		
		}
	}

	@Override
	public void handleDataResultOnSuccee(int responseId, Object data) {
	}
	@Override
		public void onBackPressed() {
		setResult(Activity.RESULT_OK);
			super.onBackPressed();
		}
@Override
	protected void onDestroy() {
	if(AotoBlueToothManager.getInstance().isConnected())
		stopDevice();
		super.onDestroy();
	}
	@Override
	public void initView() {
		shake = AnimationUtils.loadAnimation(SingleHighActivity.this, R.anim.shake);
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
		toy_grid.setAdapter(new GridAdapter());
		toy_grid.setOnItemClickListener(new OnItemClickListener() {

			@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
					if(!AotoBlueToothManager.getInstance().isConnected()){
						showText("请先连接蓝牙设备");
						return;
					}
						if(position==shakePostion){
							stopDevice();
							return;
					}
						
						shakePostion=position;
						isRun=false;
						if(shakeView!=null)
							shakeView.clearAnimation();
						shakeView=view;
						shakeView.startAnimation(shake);
						byte[] arrayOfByte=null;
				if(AotoBlueToothManager.getInstance().getDeviceName().equals(AotoGattAttributes.SMART_MINI_VIBE)){
					Vibrate vibrate1;
					Vibrate vibrate2;
					Vibrate vibrate3;
						switch(position){
						case 0:
							arrayOfByte=AotoGattAttributes.initArrayOfByte(20);
							AotoBlueToothManager.getInstance().getCurrentCharacteristic().setValue(arrayOfByte);
							AotoBlueToothManager.getInstance().wirteCharacteristic();
							break;
						case 1:
							arrayOfByte=AotoGattAttributes.initArrayOfByte(60);
							AotoBlueToothManager.getInstance().getCurrentCharacteristic().setValue(arrayOfByte);
							AotoBlueToothManager.getInstance().wirteCharacteristic();
							break;
						case 2:
							list.clear();
							 vibrate1=new Vibrate(10,1000);
							 vibrate2=new Vibrate(0,300);
							 vibrate3=new Vibrate(80,1000);
							list.add(vibrate1);
							list.add(vibrate2);
							list.add(vibrate3);
							shaking(list);
							break;
						case 3:
							list.clear();
							 vibrate1=new Vibrate(10,500);
							 vibrate2=new Vibrate(0,300);
							 vibrate3=new Vibrate(80,300);
							list.add(vibrate1);
							list.add(vibrate2);
							list.add(vibrate3);
							shaking(list);
							break;
						case 4:
							list.clear();
							 vibrate1=new Vibrate(20,200);
							 vibrate2=new Vibrate(0,100);
							 vibrate3=new Vibrate(80,500);
							list.add(vibrate1);
							list.add(vibrate2);
							list.add(vibrate3);
							shaking(list);
							break;
						case 5:
							list.clear();
							 vibrate1=new Vibrate(60,700);
							 vibrate2=new Vibrate(0,100);
							 vibrate3=new Vibrate(20,100);
							list.add(vibrate1);
							list.add(vibrate2);
							list.add(vibrate3);
							shaking(list);
							break;
						case 6:
							list.clear();
							 vibrate1=new Vibrate(60,700);
							 vibrate2=new Vibrate(0,100);
							 vibrate3=new Vibrate(80,300);
							list.add(vibrate1);
							list.add(vibrate2);
							list.add(vibrate3);
							shaking(list);
							break;
						case 7:
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
					switch(position){
					case 0:
						arrayOfByte=new byte[]{1};
						break;
					case 1:
						arrayOfByte=new byte[]{2};
						break;
					case 2:
						arrayOfByte=new byte[]{3};
						break;
					case 3:
						arrayOfByte=new byte[]{4};
						break;
					case 4:
						arrayOfByte=new byte[]{5};
						break;
					case 5:
						arrayOfByte=new byte[]{6};
						break;
					case 6:
						arrayOfByte=new byte[]{7};
						break;
					case 7:
						arrayOfByte=new byte[]{8};
						break;
						
					}
					AotoBlueToothManager.getInstance().getCurrentCharacteristic().setValue(arrayOfByte);
					AotoBlueToothManager.getInstance().wirteCharacteristic();
				}else if(AotoBlueToothManager.getInstance().getDeviceName().equals(AotoGattAttributes.IBALL)){
					Vibrate vibrate1;
					Vibrate vibrate2;
					Vibrate vibrate3;
					switch(position){
					case 0:
						list.clear();
						 vibrate1=new Vibrate(-1,1000);
						 vibrate2=new Vibrate(0,300);
						 vibrate3=new Vibrate(-1,1000);
						list.add(vibrate1);
						list.add(vibrate2);
						list.add(vibrate3);
						shaking(list);
						break;
					case 1:
						list.clear();
						 vibrate1=new Vibrate(-1,500);
						 vibrate2=new Vibrate(-1,300);
						 vibrate3=new Vibrate(0,300);
						list.add(vibrate1);
						list.add(vibrate2);
						list.add(vibrate3);
						shaking(list);
						break;
					case 2:
						list.clear();
						 vibrate1=new Vibrate(-1,500);
						 vibrate2=new Vibrate(-1,600);
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
						 vibrate3=new Vibrate(0,800);
						list.add(vibrate1);
						list.add(vibrate2);
						list.add(vibrate3);
						shaking(list);
						break;
					case 5:
						list.clear();
						 vibrate1=new Vibrate(-1,300);
						 vibrate2=new Vibrate(-1,600);
						 vibrate3=new Vibrate(0,200);
						list.add(vibrate1);
						list.add(vibrate2);
						list.add(vibrate3);
						shaking(list);
						break;
					case 6:
						list.clear();
						 vibrate1=new Vibrate(-1,1000);
						 vibrate2=new Vibrate(-1,600);
						 vibrate3=new Vibrate(0,200);
						list.add(vibrate1);
						list.add(vibrate2);
						list.add(vibrate3);
						shaking(list);
						break;
					case 7:
						arrayOfByte=AotoGattAttributes.initIBallData(-1);
						AotoBlueToothManager.getInstance().getCurrentCharacteristic().setValue(arrayOfByte);
						AotoBlueToothManager.getInstance().wirteCharacteristic();
						break;
					}
				}else if(AotoBlueToothManager.getInstance().getDeviceName().startsWith(AotoGattAttributes.XAIAI)||AotoBlueToothManager.getInstance().getDeviceName().startsWith(AotoGattAttributes.XAIAIF)){

						switch (position){
							case 0:
								arrayOfByte=new  byte[]{8,17,6,15,1,1,31,79};
								break;
							case 1:
								arrayOfByte=new  byte[]{8,17,6,15,1,1,32,80};
								break;
							case 2:
								arrayOfByte=new  byte[]{8,17,6,15,1,1,33,81};
								break;
							case 3:
								arrayOfByte=new  byte[]{8,17,6,15,1,1,34,82};
								break;
							case 4:
								arrayOfByte=new  byte[]{8,17,6,15,1,1,35,83};
								break;
							case 5:
								arrayOfByte=new  byte[]{8,17,6,15,1,1,38,84};
								break;
							case 6:
								arrayOfByte=new  byte[]{8,17,6,15,1,1,39,85};
								break;
							case 7:
								if(AotoBlueToothManager.getInstance().getDeviceName().startsWith(AotoGattAttributes.XAIAI))
									arrayOfByte=new  byte[]{8,17,6,15,1,1,40,86};
								else
								arrayOfByte=new  byte[]{8,17,6,15,1,1,30,78};
								break;
						}

					AotoBlueToothManager.getInstance().getCurrentCharacteristic().setValue(arrayOfByte);
					AotoBlueToothManager.getInstance().wirteCharacteristic();



				}
				
				
				
				
			}
			
			
		});
		
		
	}
	private void shaking(final List<Vibrate> list){
		isRun=true;
		total=list.size();
		handler.post(runnable);
	}
public class VibrateRunnable implements Runnable {
		private 	byte[]  arrayOfByte;
	

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

	}
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	private void stopDevice(){
		isRun=false;
		handler.removeCallbacks(runnable);
		byte[] 	arrayOfByte=null;
		if(AotoBlueToothManager.getInstance().getDeviceName().equals(AotoGattAttributes.SMART_MINI_VIBE)){
			arrayOfByte=AotoGattAttributes.initArrayOfByte(0);
		}else if(AotoBlueToothManager.getInstance().getDeviceName().equals(AotoGattAttributes.WOLKAMO_BIU)||AotoBlueToothManager.getInstance().getDeviceName().equals(AotoGattAttributes.WOLKAMO_MONA)){
			arrayOfByte=new byte[]{0};
		}else if(AotoBlueToothManager.getInstance().getDeviceName().equals(AotoGattAttributes.IBALL)){
			arrayOfByte=AotoGattAttributes.initIBallData(0);
		}else if(AotoBlueToothManager.getInstance().getDeviceName().startsWith(AotoGattAttributes.XAIAI)||AotoBlueToothManager.getInstance().getDeviceName().startsWith(AotoGattAttributes.XAIAIF)){
			arrayOfByte= new byte[]{8,17,6,15,1,1,0,48};
		}
	
		if(shakeView!=null){
			shakeView.clearAnimation();
			shakeView=null;
		}
		shakePostion=-1;
		AotoBlueToothManager.getInstance().getCurrentCharacteristic().setValue(arrayOfByte);
		AotoBlueToothManager.getInstance().wirteCharacteristic();
		
	}
	private class GridAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return DefCommonInteractFragment.names.length;
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
				convertView=View.inflate(SingleHighActivity.this, R.layout.toy_grid_item, null);
				holder.itemImg=(ImageView) convertView.findViewById(R.id.itemImg);
				holder.itemText=(TextView) convertView.findViewById(R.id.itemText);
				convertView.setTag(holder);
			}else{
				holder=(ViewHolder) convertView.getTag();
			}
			holder.itemText.setText(DefCommonInteractFragment.names[position]);
			holder.itemImg.setImageResource(DefCommonInteractFragment.imgs[position]);
			return convertView;
		}
	}
	
	private class ViewHolder{
		private ImageView itemImg;
		private TextView itemText;
	}

}
