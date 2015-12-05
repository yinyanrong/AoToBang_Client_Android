package com.aotobang.app.device.control;

import com.aotobang.app.manager.AotoBlueToothManager;

import android.os.Handler;

public abstract class AbstractControl implements DeviceControl {
	protected int deviceType;
	protected Handler handler=new Handler();
	protected boolean realMode=false;
	protected Runnable stopRunnable=new Runnable() {
		
		@Override
		public void run() {
			if(AotoBlueToothManager.getInstance().isConnected())
			AotoBlueToothManager.getInstance().stopDevice();
			
			
		}
	};
		@Override
		public boolean isRealMode() {
			
			return realMode;
		}
		@Override
		public void setRealMode(boolean real) {
			
			this.realMode=real;
		}
		
		@Override
		public void setDeviceType(int type) {
			this.deviceType=type;
		}
		@Override
		public int getDeviceType() {
			
			return deviceType;
		}
		@Override
		public void realControl(String data) {
			handler.removeCallbacks(stopRunnable);
			handler.postDelayed(stopRunnable, 1500);
			
			
		}
		@Override
		public void stopDevice() {
			if(realMode){
			realMode=false;
			//AotoBlueToothManager.getInstance().closeRealMode();
			}
			AotoBlueToothManager.getInstance().stopDevice();
			
		}

}
