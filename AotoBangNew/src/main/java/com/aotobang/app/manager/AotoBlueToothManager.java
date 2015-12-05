package com.aotobang.app.manager;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import com.aotobang.app.applaction.AotoBangApp;
import com.aotobang.app.callback.BleConnCallBack;
import com.aotobang.app.callback.IBleDeviceReadData;
import com.aotobang.app.callback.IBleDisConnect;
import com.aotobang.app.fragment.DefCommonInteractFragment;
import com.aotobang.bluetooth.AotoGattAttributes;
import com.aotobang.bluetooth.AotoGattAttributes.Mode;
import com.aotobang.bluetooth.BluetoothLeService;
import com.aotobang.bluetooth.bean.LocalBluetoothDevice;
import com.aotobang.utils.LogUtil;


public class AotoBlueToothManager {
	private IBleDisConnect   bleDisConnect;
	private IBleDeviceReadData  bleDeviceData;
	private static AotoBlueToothManager instance;
	private BluetoothLeService mBluetoothLeService;
	private BleConnCallBack callBack;
	private boolean bind=false;
	private boolean connected = false;
	private static int shakeCount;
	private String deviceName="";
	public String getDeviceName() {
		return deviceName;
	}
	private String currDeviceAddress;
	private BluetoothGattService currentService;
	private BluetoothGattService currentReadService;
	private BluetoothGattCharacteristic currentCharacteristic;
	private BluetoothGattCharacteristic currentReadCharacteristic;
	private BluetoothGattDescriptor currentDescriptor;
	private int flag;
	private List<LocalBluetoothDevice> bluetoothDeviceList=new ArrayList<LocalBluetoothDevice>();
	public List<LocalBluetoothDevice> getDeviceList(){
		
		return bluetoothDeviceList;
	} 
	public static AotoBlueToothManager getInstance(){
		
		if (instance == null) {
			synchronized (AotoBlueToothManager.class) {
				if (instance == null) {
					instance = new AotoBlueToothManager();
				}
			}
		}

		return instance;
		
	}
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public void stopLVSDevice(){
		if(currentCharacteristic==null)
			return;
		currentCharacteristic.setValue("Vibrate:0;");
		wirteCharacteristic();
		if(deviceName.equals(AotoGattAttributes.LVS_A006))
		currentCharacteristic.setValue("Rotate:0;");
		else
			currentCharacteristic.setValue("Air:Level:0;");
		wirteCharacteristic();
	}
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public void stopDevice(){
		if(currentReadCharacteristic!=null){
			closeRealMode();
			
		}
		if(currentCharacteristic!=null &&!deviceName.startsWith("LVS")){
		
		byte[] 	arrayOfByte;
		if(deviceName.equals(AotoGattAttributes.SMART_MINI_VIBE)){
			arrayOfByte=AotoGattAttributes.initArrayOfByte(0);
		}else if(deviceName.equals(AotoGattAttributes.WOLKAMO_BIU)||deviceName.equals(AotoGattAttributes.WOLKAMO_MONA)){
			arrayOfByte=new byte[]{0};
			
			
		}else if(deviceName.equals(AotoGattAttributes.IBALL)){
			arrayOfByte=AotoGattAttributes.initIBallData(0);
		}else{
			arrayOfByte=new byte[]{8,17,6,15,1,1,0,48};
		}


		currentCharacteristic.setValue(arrayOfByte);
		wirteCharacteristic();
		}else{
			stopLVSDevice();
		}
	}
	public AotoBlueToothManager() {
		AotoBangApp.mPreLoad.appContext.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

		new Thread(){
			@Override
			public void run() {
				Intent gattServiceIntent = new Intent(AotoBangApp.mPreLoad.appContext, BluetoothLeService.class);
				bind = AotoBangApp.mPreLoad.appContext.bindService(gattServiceIntent, mServiceConnection,Context.BIND_AUTO_CREATE);
			}
		}.start();

	
		
		
	}
	public  void  connectDevice(String address,BleConnCallBack callBack,String deviceName){
		flag=0;
		this.deviceName=deviceName;
		this.currDeviceAddress=address;
		this.callBack=callBack;
		
		
		/*if(!bind){
			new Thread(){
				public void run() {
					Intent gattServiceIntent = new Intent(AotoBangApp.mPreLoad.appContext, BluetoothLeService.class);
					bind = AotoBangApp.mPreLoad.appContext.bindService(gattServiceIntent, mServiceConnection,Context.BIND_AUTO_CREATE);
				};
				
			}.start();
		}else*/ if(mBluetoothLeService!=null)
				mBluetoothLeService.connect(currDeviceAddress);
		
			
	}
	public void disconnectDevice(){
		flag=1;
		mBluetoothLeService.disconnect();
		
	}
	public boolean isConnected() {
		return connected;
	}
	public String getCurrDeviceAddress() {
		return currDeviceAddress;
	}
	public void wirteReadCharacteristic(){
		mBluetoothLeService.wirteCharacteristic(currentReadCharacteristic);
		
	}
	public void wirteCharacteristic(){
		mBluetoothLeService.wirteCharacteristic(currentCharacteristic);
		
	}
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public void openRealMode(){
		if(mBluetoothLeService==null||currentDescriptor==null||currentCharacteristic==null)
			return;
	
		if(deviceName.startsWith("LVS")){
			currentCharacteristic.setValue("Gsensor:0e02;");
			mBluetoothLeService.wirteCharacteristic(currentCharacteristic);
			currentCharacteristic.setValue("StartMove:30;");
			mBluetoothLeService.wirteCharacteristic(currentCharacteristic);
			mBluetoothLeService.setLVSCharacteristicNotification(currentReadCharacteristic, true);
		}else{
			mBluetoothLeService.setCharacteristicNotification(currentReadCharacteristic, true);
			currentDescriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
			mBluetoothLeService.writeDescriptor(currentDescriptor);
		}
		
		
	}
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public void closeRealMode(){
		if(mBluetoothLeService==null||currentDescriptor==null)
			return;
	
		if(deviceName.startsWith("LVS")){
			currentCharacteristic.setValue("StopMove:30;");
			mBluetoothLeService.wirteCharacteristic(currentCharacteristic);
			stopLVSDevice();
			mBluetoothLeService.setLVSCharacteristicNotification(currentReadCharacteristic, true);
		}else{
			mBluetoothLeService.setCharacteristicNotification(currentReadCharacteristic, false);
			currentDescriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
			mBluetoothLeService.writeDescriptor(currentDescriptor);
		}
		
	
	}
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		

		@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
				LogUtil.info(AotoBlueToothManager.class, "ACTION_GATT_CONNECTED");
			
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
				for(LocalBluetoothDevice device:bluetoothDeviceList){
					if(device.getRealName().equals(deviceName))
						device.setOn(false);
				}
				deviceName="";
				currentService=null;
				currentCharacteristic=null;
				currentReadService=null;
				currentReadCharacteristic=null;
				currentDescriptor=null;
				connected = false;
				callBack.onDisConnected(currDeviceAddress);
				if(bleDisConnect!=null)
					bleDisConnect.onDisConnect();
				
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				LogUtil.info(AotoBlueToothManager.class, "ACTION_GATT_SERVICES_DISCOVERED");
					connected = true;
					callBack.onConnected();
					if(deviceName.equals(AotoGattAttributes.SMART_MINI_VIBE)){
						currentService=mBluetoothLeService.getServiceByUUID(AotoGattAttributes.MINI_VIBRATOR_SERVICE);
						currentCharacteristic=currentService.getCharacteristic(UUID.fromString(AotoGattAttributes.MINI_VIBRATOR_TRAIT));
					}else if(deviceName.equals(AotoGattAttributes.WOLKAMO_BIU)||deviceName.equals(AotoGattAttributes.WOLKAMO_MONA)){
						currentService=mBluetoothLeService.getServiceByUUID(AotoGattAttributes.WOLKAMO_VIBRATOR_SERVICE);
						currentCharacteristic=currentService.getCharacteristic(UUID.fromString(AotoGattAttributes.WOLKAMO_VIBRATOR_TRAIT));
						currentReadService=mBluetoothLeService.getServiceByUUID(AotoGattAttributes.WOLKAMO_VIBRATOR_READ_SERVICE);
						currentReadCharacteristic=currentReadService.getCharacteristic(UUID.fromString(AotoGattAttributes.WOLKAMO_VIBRATOR_READ_TRAIT));
						currentDescriptor=currentReadCharacteristic.getDescriptor(UUID.fromString(AotoGattAttributes.WOLKAMO_VIBRATOR_READ_DESCRIPTOR));
					}else if(deviceName.equals(AotoGattAttributes.IBALL)){
						currentService=mBluetoothLeService.getServiceByUUID(AotoGattAttributes.IBALL_VIBRATOR_SERVICE_UUID);
						currentCharacteristic=currentService.getCharacteristic(UUID.fromString(AotoGattAttributes.IBALL_VIBRATOR_TRAIT));
						currentReadCharacteristic=currentService.getCharacteristic(UUID.fromString(AotoGattAttributes.IBALL_VIBRATOR_READ_TRAIT));
						currentDescriptor=currentReadCharacteristic.getDescriptor(UUID.fromString(AotoGattAttributes.IBALL_VIBRATOR_READ_DESCRIPTOR));
					}else if(deviceName.equals(AotoGattAttributes.LVS_A006)||deviceName.equals(AotoGattAttributes.LVS_B006)){
						currentService=mBluetoothLeService.getServiceByUUID(AotoGattAttributes.LVS_VIBRATOR_SERVICE_UUID);
						currentCharacteristic=currentService.getCharacteristic(UUID.fromString(AotoGattAttributes.LVS_TRAIT));
						currentReadCharacteristic=currentService.getCharacteristic(UUID.fromString(AotoGattAttributes.LVS_NOTIFY_CHARACTERISTIC_UUID));
						currentDescriptor=currentReadCharacteristic.getDescriptor(UUID.fromString(AotoGattAttributes.LVS_NOTIFY_DESCRIPTOR_UUID));
						stopLVSDevice();
						currentCharacteristic.setValue("ER;");
						mBluetoothLeService.wirteCharacteristic(currentCharacteristic);
						currentCharacteristic.setValue("Battery;");
						mBluetoothLeService.wirteCharacteristic(currentCharacteristic);
						if(currentDescriptor!=null){
							currentDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
							mBluetoothLeService.writeDescriptor(currentDescriptor);
					
					}
					}else if(deviceName.startsWith(AotoGattAttributes.XAIAI)||deviceName.startsWith(AotoGattAttributes.XAIAIF)){


						currentService=mBluetoothLeService.getServiceByUUID(AotoGattAttributes.UUID_KEY_SDATA);
						currentCharacteristic=currentService.getCharacteristic(UUID.fromString(AotoGattAttributes.UUID_KEY_CDATA));


					}else if(deviceName.startsWith(AotoGattAttributes.BONG)){


					}
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
				if(bleDeviceData!=null&&connected)
					bleDeviceData.OnData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
			}
		}
	};
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
			if (!mBluetoothLeService.initialize()) {
				LogUtil.info(AotoBlueToothManager.class, "Unable to initialize Bluetooth");
			}
			if(currDeviceAddress!=null){
			
			if(flag==0)
				 mBluetoothLeService.connect(currDeviceAddress);
			else
				mBluetoothLeService.disconnect();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
		}
	};
	public void unbindBlueToothService(){
		 AotoBangApp.mPreLoad.appContext.unbindService(mServiceConnection);
		mBluetoothLeService = null;
		
	}
	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		return intentFilter;
	}
	public BluetoothGattService getCurrentService() {
		return currentService;
	}
	public BluetoothGattService getCurrentReadService() {
		return currentReadService;
	}
	public BluetoothGattCharacteristic getCurrentCharacteristic() {
		return currentCharacteristic;
	}
	public BluetoothGattCharacteristic getCurrentReadCharacteristic() {
		return currentReadCharacteristic;
	}
	public BluetoothGattDescriptor getCurrentDescriptor() {
		return currentDescriptor;
	}
	public void registOnDeviceDataListener(IBleDeviceReadData  listener){
		this.bleDeviceData=listener;
		
	}
	public void unRegistOnDeviceDataListener(IBleDeviceReadData  listener){
		this.bleDeviceData=null;
		
	}
	public void setOnDisConnectListener(IBleDisConnect  bleDisConnect){
		this.bleDisConnect=bleDisConnect;
	}
}
