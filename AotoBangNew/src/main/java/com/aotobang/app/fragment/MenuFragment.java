package com.aotobang.app.fragment;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aotobang.annotation.BaseFragment;
import com.aotobang.annotation.ContentView;
import com.aotobang.annotation.FindViewNoOnClick;
import com.aotobang.annotation.FindViewOnClick;
import com.aotobang.app.GlobalParams;
import com.aotobang.app.R;
import com.aotobang.app.activity.PhotoActivity;
import com.aotobang.app.activity.Regist2Activity;
import com.aotobang.app.activity.ToyActivity;
import com.aotobang.app.applaction.AotoBangApp;
import com.aotobang.app.asyntask.LoadAvatarTask;
import com.aotobang.app.view.CircleImageView;
import com.aotobang.utils.UserUtils;
@ContentView(R.layout.fragement_menu)
public class MenuFragment extends BaseFragment {
	public static final int UPDATA_USER_INFO=0;
	public static final int REQUEST_ENABLE_BT = 1;
	@FindViewNoOnClick(R.id.user_name)
	private TextView user_name;
	@FindViewOnClick(R.id.user_avatar)
	private  CircleImageView user_avatar;
	
	private BluetoothAdapter mBluetoothAdapter;
	private  String[] menuTexts;
	private int[] imgs=new int[]{R.drawable.menu_picture,R.drawable.menu_toy,R.drawable.menu_present,R.drawable.menu_invite,R.drawable.menu_setting,
			R.drawable.menu_ours,R.drawable.menu_op,R.drawable.menu_shop};
	@FindViewNoOnClick(R.id.menu_list)
	private ListView  list;

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.user_avatar:
			Intent intent=new Intent();
			intent.setClass(getActivity(), Regist2Activity.class);
			getActivity().startActivityForResult(intent, UPDATA_USER_INFO);
			break;
			
		}
	}
	@Override
	public void handleDataResultOnSuccee(int responseId, Object data) {
	}
	@Override
	public void onStart() {
		super.onStart();
	}
	@Override
	public void initView() {
		refreshAvatar();
		user_name.setText(AotoBangApp.mPreLoad.getUserNick());
		 BluetoothManager bluetoothManager = (BluetoothManager)getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
	     mBluetoothAdapter = bluetoothManager.getAdapter();
		menuTexts=new String[]{getString(R.string.menu_picture),getString(R.string.menu_toy),getString(R.string.menu_gift),
				getString(R.string.menu_invite)	,getString(R.string.menu_setting),getString(R.string.menu_ours),getString(R.string.menu_opinion),
				getString(R.string.menu_shop)	};
		list.setAdapter(new MenuAdapter());
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent;
				switch(position){
				case 0:
					 intent=new Intent();
					 intent.setClass(getActivity(), PhotoActivity.class);
					 startActivity(intent);
					break;
				case 1:
					  // 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
			        if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			            Toast.makeText(getActivity(), R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
			            return;
			        }
			        // 检查设备上是否支持蓝牙
			        if (mBluetoothAdapter == null) {
			            Toast.makeText(getActivity(), R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
			            return;
			        }
			          if (!mBluetoothAdapter.isEnabled()) {
	                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
	            }else{
	            	 intent=new Intent();
					intent.setClass(getActivity(), ToyActivity.class);
					startActivity(intent);
	            }
					break;
					case 2:
//						AotoBangApp.getInstance().logOut();
//						AppContext.exitLogin(SettingActivity.this);
//						AppContext.startLogin(SettingActivity.this);
						Toast.makeText(getActivity(),"礼物", Toast.LENGTH_SHORT).show();
						break;
					case 3:
						Toast.makeText(getActivity(),"邀请好友", Toast.LENGTH_SHORT).show();
						break;
					case 4:
						Toast.makeText(getActivity(),"设置", Toast.LENGTH_SHORT).show();
						break;
					case 5:
						Toast.makeText(getActivity(),"关于我们", Toast.LENGTH_SHORT).show();
						break;
					case 6:
						Toast.makeText(getActivity(),"意见反馈", Toast.LENGTH_SHORT).show();
						break;
					case 7:
						Toast.makeText(getActivity(),"商城", Toast.LENGTH_SHORT).show();
						break;

				}
				
				
			}
		});
	}
public void refreshAvatar(){
	user_name.setText(AotoBangApp.getInstance().getUserNick());
	new LoadAvatarTask().execute(user_avatar,AotoBangApp.getInstance().getUserId(),AotoBangApp.getInstance().getUserList().get(AotoBangApp.getInstance().getUserId()).getRemote_avatar());

}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		  if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
			  	Intent intent=new Intent();
				intent.setClass(getActivity(), ToyActivity.class);
				startActivity(intent);
	        }
			  
	}
	private class MenuAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			
			return menuTexts.length;
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
			ViewHolder holder=null;
			if(convertView==null){
				holder=new ViewHolder();
				convertView=View.inflate(getActivity(), R.layout.menu_list_item, null);
				holder.item_icon=(ImageView) convertView.findViewById(R.id.item_icon);
				holder.item_text=(TextView) convertView.findViewById(R.id.item_text);
				holder.line=convertView.findViewById(R.id.line);
				convertView.setTag(holder);
			}else{
				holder=(ViewHolder) convertView.getTag();
			}
			holder.item_icon.setImageResource(imgs[position]);
			holder.item_text.setText(menuTexts[position]);
			if(position==3)
				holder.line.setVisibility(View.VISIBLE);
			else
				holder.line.setVisibility(View.INVISIBLE);
			return convertView;
		}
		
		
		private class ViewHolder{
			private TextView item_text;
			private ImageView item_icon;
			private View line;
			
		}
		
	}
}
