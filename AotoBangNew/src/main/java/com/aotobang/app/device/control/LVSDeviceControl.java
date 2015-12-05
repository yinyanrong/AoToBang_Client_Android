package com.aotobang.app.device.control;

import java.util.Random;

import com.aotobang.app.manager.AotoBlueToothManager;
import com.aotobang.bluetooth.AotoGattAttributes;
import com.aotobang.bluetooth.AotoGattAttributes.Mode;
import com.aotobang.utils.LogUtil;

public class LVSDeviceControl extends AbstractControl {
private Random random=new Random();

	@Override
	public void control(String data) {
		if(AotoBlueToothManager.getInstance().getCurrentCharacteristic()!=null){
		AotoBlueToothManager.getInstance().getCurrentCharacteristic().setValue(data);
		AotoBlueToothManager.getInstance().wirteCharacteristic();
		}
	}
	@Override
	public void realControl(String data) {
		LogUtil.info(LVSDeviceControl.class, deviceType+"--deviceType--");
		int i=Integer.valueOf(data);
		String controlData = null;
		String controlDataSecond = null;
		int virbate;
		int second;
		LogUtil.info(LVSDeviceControl.class, data);
		switch(i){
		case 0:
		
	
		//	AotoBlueToothManager.getInstance().stopLVSDevice();
			break;
		case 1:
		case 2:
		case 3:
			virbate=random.nextInt(2)%(2-1+1) + 1;
			second=random.nextInt(2)%(2-1+1) + 1;
			controlData=AotoGattAttributes.getLVSData(virbate, Mode.Vibrate);
			if(AotoBlueToothManager.getInstance().getDeviceName().equals(AotoGattAttributes.LVS_A006))
				controlDataSecond=AotoGattAttributes.getLVSData(second, Mode.Rotate);
			else
				controlDataSecond=AotoGattAttributes.getLVSData(second, Mode.Inflate);
			break;
	
		case 4:
		case 7:
			virbate=random.nextInt(4)%(4-3+1) + 3;
			second=random.nextInt(4)%(4-3+1) + 3;
			controlData=AotoGattAttributes.getLVSData(virbate, Mode.Vibrate);
			if(AotoBlueToothManager.getInstance().getDeviceName().equals(AotoGattAttributes.LVS_A006))
				controlDataSecond=AotoGattAttributes.getLVSData(second, Mode.Rotate);
			else
				controlDataSecond=AotoGattAttributes.getLVSData(second, Mode.Inflate);
			break;
		}
		LogUtil.info(LVSDeviceControl.class, controlData+"--controlData--"+"controlDataSecond---"+controlDataSecond);
		if(controlDataSecond!=null&&controlData!=null){
			control(controlDataSecond);
			control(controlData);
			super.realControl(controlDataSecond);
		
		}
	}


}
