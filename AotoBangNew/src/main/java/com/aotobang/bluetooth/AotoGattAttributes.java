/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aotobang.bluetooth;

import java.util.ArrayList;
import java.util.List;


/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class AotoGattAttributes {
    public static final String HEART_RATE_MEASUREMENT = "0000C004-0000-1000-8000-00805f9b34fb";
    public static final String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static final int DEVICE_TYPE_NONE=0;
    public static final int DEVICE_TYPE_COMMON=1;
    public static final int DEVICE_TYPE_LVS_A=2;
    public static final int DEVICE_TYPE_LVS_B=3;
    public static final String SMART_MINI_VIBE="Smart Mini Vibe";
    
	 public static final String MINI_VIBRATOR_SERVICE = "78667579-7b48-43db-b8c5-7928a6b0a335";
	 public static final String MINI_VIBRATOR_TRAIT= "78667579-a914-49a4-8333-aa3c0cd8fedc";
	 //有情有趣
	 public static final String WOLKAMO_MONA="Wolkamo-Mona";
	 public static final String WOLKAMO_BIU="Wolkamo-Biu";
	 public static final String WOLKAMO_VIBRATOR_SERVICE="f000AA70-0451-4000-b000-000000000000";//震动服务
	 public static final String WOLKAMO_VIBRATOR_TRAIT="f000AA71-0451-4000-b000-000000000000";//byte[1-8]

	 public static final String WOLKAMO_VIBRATOR_READ_SERVICE="f000aa10-0451-4000-b000-000000000000";
	 public static final String WOLKAMO_VIBRATOR_READ_TRAIT="f000aa11-0451-4000-b000-000000000000";
	 public static final String WOLKAMO_VIBRATOR_READ_DESCRIPTOR="00002901-0000-1000-8000-00805f9b34fb";//读取震动
	    //春水堂
	 public static final String IBALL="IBall";
	 public static final String IBALL_VIBRATOR_SERVICE_UUID = "0000fff0-0000-1000-8000-00805f9b34fb";
	 public static final String IBALL_VIBRATOR_TRAIT = "0000fff3-0000-1000-8000-00805f9b34fb";
	 
	 
	 public static final String IBALL_VIBRATOR_READ_TRAIT ="0000fff4-0000-1000-8000-00805f9b34fb";
	 public static final String IBALL_VIBRATOR_READ_DESCRIPTOR = "00002902-0000-1000-8000-00805f9b34fb";
	 
	 public static final String LVS_A006="LVS-A006";
	 public static final String LVS_B006="LVS-B006";
	 
	 public static final String LVS_VIBRATOR_SERVICE_UUID = "0000fff0-0000-1000-8000-00805f9b34fb";
	 public static final String LVS_TRAIT = "0000fff2-0000-1000-8000-00805f9b34fb";
	 public static final String LVS_NOTIFY_CHARACTERISTIC_UUID = "0000fff3-0000-1000-8000-00805f9b34fb";
	 public static final String LVS_NOTIFY_DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb";
	 //XAIAI
	public static final String XAIAI="XAIAI-";
	public static final String XAIAIF="XAIAIF-";
	 public static final String UUID_KEY_CDATA = "0000ffe9-0000-1000-8000-00805f9b34fb";
	public static final String UUID_KEY_SDATA = "0000ffe5-0000-1000-8000-00805f9b34fb";

	public static final String BONG="bongXX";
	public static final String BONG_SERVICE_UUID="6e400001-b5a3-f393-e0a9-e50e24dcca1e";
	public static final String BONG_UUID="6e400002-b5a3-f393-e0a9-e50e24dcca1e";
	 public static final List<String> supportDevices=new ArrayList<String>();
	 static{
		 supportDevices.add(SMART_MINI_VIBE);
		 supportDevices.add(WOLKAMO_MONA);
		 supportDevices.add(WOLKAMO_BIU);
		 supportDevices.add(IBALL);
		 supportDevices.add(LVS_A006);
		 supportDevices.add(LVS_B006);
		 //supportDevices.add(BONG);
		 
	 }
	 public static String getLVSData(int rate,Mode mode){
		 String data=null;
		 switch(mode){
		 case Vibrate:
			 rate=rate*6;
			 data="Vibrate:"+rate+";";
			 break;
		 case Rotate:
			 rate=rate*6;
			 data="Rotate:"+rate+";";
			 break;
		 case Inflate:
			 data="Air:Level:"+rate+";";
			 break;
		 }
		 
		 return data;
	 }
	 public static enum Mode {
	    	Vibrate,
	    	Rotate,
	    	Inflate;
	    	
	    }
	 public static  byte[] initIBallData(int value){
		  byte[] arrayOfByte = new byte[5];
		    arrayOfByte[0] = (byte) 65440;
		    arrayOfByte[1] = (byte) value;
		    arrayOfByte[2] = 0;
		    arrayOfByte[3] = -1;
		    arrayOfByte[4] = -1;
		    return  arrayOfByte;
		
	}
	public static  byte[] initArrayOfByte(int param){
		int i=(int) ((int)param*2.55);
		 byte[] arrayOfByte = new byte[13];
		    arrayOfByte[0] = 12;
		    arrayOfByte[1] = -1;
		    arrayOfByte[2] = 4;
		    arrayOfByte[3] = 8;
		    arrayOfByte[4] =(byte)i;
		    arrayOfByte[5] = 64;
		    arrayOfByte[7] = 5;
		    arrayOfByte[8] = 10;
		    arrayOfByte[9] = (byte)i;
		   return arrayOfByte;
		
		
	}
	    
}

 