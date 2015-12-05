package com.aotobang.app.device.control;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;

import com.aotobang.app.manager.AotoBlueToothManager;
import com.aotobang.bluetooth.AotoGattAttributes;
import com.aotobang.bluetooth.bean.Vibrate;
import com.aotobang.utils.LogUtil;

public class CommonDeviceControl extends AbstractControl{
	
	
	private Runnable runnable=new VibrateRunnable();
	private  int total;
	private  int point;
	public  boolean isRun=false;
	private List<Vibrate> list=new ArrayList<Vibrate>();
	
	@Override
	public void control(String data) {
		int type=Integer.valueOf(data);
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
			
		}else if(AotoBlueToothManager.getInstance().getDeviceName().startsWith(AotoGattAttributes.XAIAI)||AotoBlueToothManager.getInstance().getDeviceName().startsWith(AotoGattAttributes.XAIAIF)){

			switch (type){
				case 0:
					arrayOfByte=new  byte[]{8,17,6,15,1,1,0,48};
					break;
				case 1:
					arrayOfByte=new  byte[]{8,17,6,15,1,1,31,79};
					break;
				case 2:
					arrayOfByte=new  byte[]{8,17,6,15,1,1,32,80};
					break;
				case 3:
					arrayOfByte=new  byte[]{8,17,6,15,1,1,33,81};
					break;
				case 4:
					arrayOfByte=new  byte[]{8,17,6,15,1,1,34,82};
					break;
				case 5:
					arrayOfByte=new  byte[]{8,17,6,15,1,1,35,83};
					break;
				case 6:
					arrayOfByte=new  byte[]{8,17,6,15,1,1,38,84};
					break;
				case 7:
					arrayOfByte=new  byte[]{8,17,6,15,1,1,39,85};
					break;
				case 8:
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
	private void shaking(final List<Vibrate> list){
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

	}
	public void setRunStatus(boolean isRun){
		this.isRun=isRun;
	}
	@Override
	public void stopDevice() {
		LogUtil.info(CommonDeviceControl.class, "stop");
		isRun=false;
		handler.removeCallbacks(runnable);
		super.stopDevice();
	}
	@Override
	public void realControl(String netData) {
		/*byte[]  data = new byte[]{0};
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
		AotoBlueToothManager.getInstance().wirteCharacteristic();*/
		control(netData);
		super.realControl(netData);
	}
}
