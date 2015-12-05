package com.aotobang.app.activity;

import java.util.List;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aotobang.annotation.BaseActivity;
import com.aotobang.annotation.ContentView;
import com.aotobang.annotation.FindViewNoOnClick;
import com.aotobang.annotation.FindViewOnClick;
import com.aotobang.app.R;
import com.aotobang.app.callback.BleConnCallBack;
import com.aotobang.app.manager.AotoBlueToothManager;
import com.aotobang.bluetooth.AotoGattAttributes;
import com.aotobang.bluetooth.bean.LocalBluetoothDevice;
import com.aotobang.utils.DialogUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
@ContentView(R.layout.activity_toy)
public class ToyActivity extends BaseActivity {
@FindViewOnClick(R.id.btn_back)
private ImageButton btn_back;
@FindViewNoOnClick(R.id.pull_refresh_list)
private PullToRefreshListView pull_refresh_list;
@FindViewNoOnClick(R.id.text_hint)
private TextView text_hint;
private LeDeviceListAdapter mLeDeviceListAdapter;
private BluetoothAdapter mBluetoothAdapter;
private boolean mScanning;
private Handler mHandler=new Handler();
private ListView actualListView;
// 2秒后停止查找搜索.
private static final long SCAN_PERIOD = 3000;
private boolean connect=false;
private Handler handler=new Handler(){
	public void handleMessage(android.os.Message msg) {
	};
};
private String currentDevice="";
private List<LocalBluetoothDevice> bluetoothDeviceList;
private Runnable timeOutRunnable=new Runnable(){

	@Override
	public void run() {
		DialogUtils.closeProgressDialog();
		showText("连接超时了，请确认蓝牙和设备都已开启");
	}};
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_back:
			finish();
			break;
		}
		
		
	}
	@Override
	public void handleDataResultOnSuccee(int responseId, Object data) {
	}

	@Override
	public void initView() {
		connect=AotoBlueToothManager.getInstance().isConnected();
		if(connect)
			currentDevice=AotoBlueToothManager.getInstance().getCurrDeviceAddress();
			
		bluetoothDeviceList=AotoBlueToothManager.getInstance().getDeviceList();
        // 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
        BluetoothManager bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

      
        pull_refresh_list.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(ToyActivity.this, System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				pull_refresh_list.setRefreshingLabel(getString(R.string.searching));
				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				scanLeDevice(true);
				// Do work to refresh the list here.
			}
		});
			pull_refresh_list.setPullLabel(getString(R.string.pull_to_search));
			pull_refresh_list.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
			
				@Override
				public void onLastItemVisible() {
					//Toast.makeText(getActivity(), "End of List!", Toast.LENGTH_SHORT).show();
				}
			});
			
			 actualListView = pull_refresh_list.getRefreshableView();
			 mLeDeviceListAdapter = new LeDeviceListAdapter();
			 actualListView.setAdapter(mLeDeviceListAdapter);	
			 pull_refresh_list.setRefreshing();
			 actualListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, final View view, final int position, long arg3) {
					if(!currentDevice.equals(bluetoothDeviceList.get(position-1).getMacAddress())&&connect){
						showText("请先关闭已经连接的设备");
						return;
					}
					if(connect){
						AotoBlueToothManager.getInstance().disconnectDevice();
						mLeDeviceListAdapter.notifyDataSetChanged();
						connect=false;
						bluetoothDeviceList.get(position-1).setOn(false);
						return;
					}
					DialogUtils.showProgressDialog(ToyActivity.this, "连接中...");
					 if (mScanning) {
				            mBluetoothAdapter.stopLeScan(mLeScanCallback);
				            mScanning = false;
				        }
					 	handler.postDelayed(timeOutRunnable, 10000);
						AotoBlueToothManager.getInstance().connectDevice(bluetoothDeviceList.get(position-1).getMacAddress(),new BleConnCallBack() {

							@Override
							public void onConnected() {
								LocalBluetoothDevice device=bluetoothDeviceList.get(position-1);
								DialogUtils.closeProgressDialog();
								connect=true;
								currentDevice=device.getMacAddress();
								device.setOn(true);
								handler.removeCallbacks(timeOutRunnable);
								mLeDeviceListAdapter.notifyDataSetChanged();
								showText("连接成功");
							}
							
							@Override
							public void onDisConnected(String address) {
								bluetoothDeviceList.get(position-1).setOn(false);
								mLeDeviceListAdapter.notifyDataSetChanged();
								connect=false;
								showText("蓝牙断开了");
							}
						},bluetoothDeviceList.get(position-1).getRealName());
				}
			});
		
	}
	@Override
		protected void onResume() {
			super.onResume();
		}
	
	 @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	 private void scanLeDevice(final boolean enable) {
		// mLeDeviceListAdapter.clear();
	        if (enable) {
	            mHandler.postDelayed(new Runnable() {
	                @Override
	                public void run() {
	                    mScanning = false;
	                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
	                    pull_refresh_list.onRefreshComplete();
	                    if(bluetoothDeviceList.isEmpty()){
	                    	text_hint.setVisibility(View.VISIBLE);
	                    	text_hint.setText(getString(R.string.toy_hint_nodevice));
	                    }else{
	                    	text_hint.setVisibility(View.INVISIBLE);
	                    	text_hint.setText(getString(R.string.toy_hint));
	                    	
	                    }
	                }
	            }, SCAN_PERIOD);
	            mScanning = true;
	            mBluetoothAdapter.startLeScan(mLeScanCallback);
	        } else {
	            mScanning = false;
	            mBluetoothAdapter.stopLeScan(mLeScanCallback);
	        }
	    }
	  private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

	        @Override
	        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
	        	mHandler.post(new Runnable() {
	                @Override
	                public void run() {
	                	if(AotoGattAttributes.supportDevices.contains(device.getName())){
	                		
	                		LocalBluetoothDevice newDevice=new LocalBluetoothDevice();
		                	newDevice.setRealName(device.getName());
		                	newDevice.setMacAddress(device.getAddress());
		                	if(device.getName().equals(AotoGattAttributes.WOLKAMO_BIU)||device.getName().equals(AotoGattAttributes.LVS_B006))
		                		newDevice.setSex(1);
		                    mLeDeviceListAdapter.addDevice(newDevice);
		                    mLeDeviceListAdapter.notifyDataSetChanged();
	                		
	                	}else if(device.getName().startsWith(AotoGattAttributes.XAIAI)||device.getName().startsWith(AotoGattAttributes.XAIAIF)){
							LocalBluetoothDevice newDevice=new LocalBluetoothDevice();
							newDevice.setRealName(device.getName());
							newDevice.setMacAddress(device.getAddress());
							if(device.getName().startsWith(AotoGattAttributes.XAIAIF))
								newDevice.setSex(1);
							mLeDeviceListAdapter.addDevice(newDevice);
							mLeDeviceListAdapter.notifyDataSetChanged();


						}
	                }
	            });
	        }
	    };
	    private class LeDeviceListAdapter extends BaseAdapter {
	        private LayoutInflater mInflator;

	        public LeDeviceListAdapter() {
	            mInflator = getLayoutInflater();
	        }

	        public void addDevice(LocalBluetoothDevice device) {
	        	if(!bluetoothDeviceList.contains(device)){
	        		bluetoothDeviceList.add(device);
	            }
	        }

	        public LocalBluetoothDevice getDevice(int position) {
	            
	            return bluetoothDeviceList.get(position);
	        }

	        public void clear() {
	        	bluetoothDeviceList.clear();
	        }

	        @Override
	        public int getCount() {
	        	return bluetoothDeviceList.size();
	        }

	        @Override
	        public Object getItem(int i) {
	            return bluetoothDeviceList.get(i);
	        }

	        @Override
	        public long getItemId(int i) {
	            return i;
	        }

	        @Override
	        public View getView(int i, View view, ViewGroup viewGroup) {
	            ViewHolder viewHolder;
	            // General ListView optimization code.
	            if (view == null) {
	                view = mInflator.inflate(R.layout.toy_list_item, null);
	                viewHolder = new ViewHolder();
	                viewHolder.item_img = (ImageView) view.findViewById(R.id.item_img);
	                viewHolder.item_deviceName = (TextView) view.findViewById(R.id.item_deviceName);
	                viewHolder.item_switch=(ImageView) view.findViewById(R.id.item_switch);
	                view.setTag(viewHolder);
	            } else {
	                viewHolder = (ViewHolder) view.getTag();
	            }

	            LocalBluetoothDevice device=bluetoothDeviceList.get(i);
	            String deviceName = device.getRealName();
	            viewHolder.item_deviceName.setText(deviceName);
	            boolean on=device.isOn();
	            if(on)
	            	viewHolder.item_switch.setImageResource(R.drawable.toy_switch_on);
	            else
	            	viewHolder.item_switch.setImageResource(R.drawable.toy_switch_off);
	            if(device.getSex()==0)
	            	 viewHolder.item_img.setImageResource(R.drawable.toy_img_blue);
	            else
	            	viewHolder.item_img.setImageResource(R.drawable.toy_img_red);
	            return view;
	        }
	    }

	    private class ViewHolder {
        	ImageView item_img;
	        TextView item_deviceName;
	        ImageView item_switch;
	    }
}
