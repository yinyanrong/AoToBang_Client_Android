package com.aotobang.app.activity;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.aotobang.annotation.BaseActivity;
import com.aotobang.annotation.ContentView;
import com.aotobang.annotation.FindViewOnClick;
import com.aotobang.app.R;
import com.aotobang.app.manager.AotoBlueToothManager;
import com.aotobang.bluetooth.AotoGattAttributes;
import com.aotobang.bluetooth.AotoGattAttributes.Mode;
@ContentView(R.layout.activity_single_lvs)
public class SingleLVSActivity extends BaseActivity {
@FindViewOnClick(R.id.btn_back)
private ImageButton btn_back;	
@FindViewOnClick(R.id.vibrate_add)
private ImageView vibrate_add;
@FindViewOnClick(R.id.vibrate_line)
private ImageView vibrate_line;
@FindViewOnClick(R.id.vibrate_subtract)
private ImageView vibrate_subtract;
@FindViewOnClick(R.id.reversal)
private ImageView reversal;
@FindViewOnClick(R.id.second_add)
private ImageView second_add;
@FindViewOnClick(R.id.second_line)
private ImageView second_line;

@FindViewOnClick(R.id.second_subtract)
private ImageView second_subtract;
@FindViewOnClick(R.id.second_text)
private TextView second_text;
@FindViewOnClick(R.id.reversal_text)
private TextView reversal_text;
private int deviceType=0;//+方法
private int vibratMode=0;//震动
private int secondMode=0;//声音
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_back:
			finish();
			break;
		case R.id.vibrate_subtract://震动
			if(vibratMode==0)
				return;
			changeMode(--vibratMode,Mode.Vibrate);
			break;
		case R.id.vibrate_add:
			if(vibratMode==4)
				return;
			changeMode(++vibratMode,Mode.Vibrate);
			break;
		case R.id.second_add://声音
			Mode mode;
			if(secondMode==4)
				return;
			if(deviceType==0)
				mode=Mode.Rotate;
			else
				mode=Mode.Inflate;
			changeMode(++secondMode,mode);
			break;
		case R.id.second_subtract:
			if(secondMode==0)
				return;
		
			if(deviceType==0)
				mode=Mode.Rotate;
			else
				mode=Mode.Inflate;
			changeMode(--secondMode,mode);
			break;
		case R.id.reversal:
			AotoBlueToothManager.getInstance().getCurrentCharacteristic().setValue("RotateChange;");
			AotoBlueToothManager.getInstance().wirteCharacteristic();
			break;
			
		}
	}
	private void changeMode(int rate,Mode mode){
		String data=AotoGattAttributes.getLVSData(rate, mode);
		AotoBlueToothManager.getInstance().getCurrentCharacteristic().setValue(data);//蓝牙管理
		AotoBlueToothManager.getInstance().wirteCharacteristic();
		if(mode==Mode.Vibrate){
		switch(rate){
		case 0:
			vibrate_line.setImageResource(R.drawable.lvs_line0);
			break;
		case 1:
			vibrate_line.setImageResource(R.drawable.lvs_line1);
			break;
		case 2:
			vibrate_line.setImageResource(R.drawable.lvs_line2);
			break;
		case 3:
			vibrate_line.setImageResource(R.drawable.lvs_line3);
			break;
		case 4:
			vibrate_line.setImageResource(R.drawable.lvs_line4);
			break;
		}
		}else{
			switch(rate){
			case 0:
				second_line.setImageResource(R.drawable.lvs_line0);
				break;
			case 1:
				second_line.setImageResource(R.drawable.lvs_line1);
				break;
			case 2:
				second_line.setImageResource(R.drawable.lvs_line2);
				break;
			case 3:
				second_line.setImageResource(R.drawable.lvs_line3);
				break;
			case 4:
				second_line.setImageResource(R.drawable.lvs_line4);
				break;
			}
			
		}
	}
	
	@Override
	public void initView() {
		String deviceName=AotoBlueToothManager.getInstance().getDeviceName();
		if(deviceName.equals(AotoGattAttributes.LVS_B006)){
			deviceType=1;
			second_add.setImageResource(R.drawable.star_small);
			second_subtract.setImageResource(R.drawable.star_big);
			second_text.setText("充气强度");
			reversal.setVisibility(View.INVISIBLE);
			reversal_text.setVisibility(View.INVISIBLE);
		}else{
			second_add.setImageResource(R.drawable.add);
			second_subtract.setImageResource(R.drawable.subtract);
			second_text.setText("旋转");
			reversal.setVisibility(View.VISIBLE);
			reversal_text.setVisibility(View.VISIBLE);
		}
	}
	@Override
		protected void onDestroy() {
			AotoBlueToothManager.getInstance().stopLVSDevice();
			super.onDestroy();
		}
	@Override
	public void handleDataResultOnSuccee(int responseId, Object data) {
	}

	

}
