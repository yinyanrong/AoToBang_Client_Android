package com.aotobang.app.activity;

import android.content.Intent;
import android.view.View;

import com.aotobang.annotation.BaseActivity;
import com.aotobang.annotation.ContentView;
import com.aotobang.annotation.FindViewOnClick;
import com.aotobang.app.GlobalParams;
import com.aotobang.app.R;
import com.aotobang.app.view.TextChangeColorView;
import com.aotobang.app.view.TextChangeColorView.TextClickListener;
@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {
@FindViewOnClick(R.id.main_logion)
private TextChangeColorView main_logion;
@FindViewOnClick(R.id.main_regist)
private TextChangeColorView main_regist;
	@Override
	public void onClick(View v) {
		 Intent intent;
		switch(v.getId()){
		case R.id.main_logion:
			  intent=new Intent();
			intent.setClass(MainActivity.this, LoginActivity.class);
			startActivity(intent);
			break;
		case R.id.main_regist:
			  intent=new Intent();
				intent.setClass(MainActivity.this, Regist1Activity.class);
				startActivity(intent);
			break;
		}
		
	}

	@Override
	public void handleDataResultOnSuccee(int responseId, Object data) {
	}
	@Override
	public void initView() {
		GlobalParams.mainActivity=this;//长连接
		main_logion.setTextClickListener(new TextClickListener() {
			
			@Override
			public void onUp() {
				main_logion.setTextColor(getResources().getColor(android.R.color.white));
				
			}
			
			@Override
			public void onDown() {
				main_logion.setTextColor(getResources().getColor(R.color.main_text_color_red));
			}

			@Override
			public void onMove(boolean focused) {
				if(focused)
				main_logion.setTextColor(getResources().getColor(R.color.main_text_color_red));
				else
					main_logion.setTextColor(getResources().getColor(android.R.color.white));
					
			}
		});
		main_regist.setTextClickListener(new TextClickListener() {
			@Override
			public void onUp() {
				main_regist.setTextColor(getResources().getColor(R.color.main_text_color_red));
			}
			@Override
			public void onDown() {
				main_regist.setTextColor(getResources().getColor(android.R.color.white));
			}
			@Override
			public void onMove(boolean focused) {
				if(focused)
				main_regist.setTextColor(getResources().getColor(android.R.color.white));
				else
					main_regist.setTextColor(getResources().getColor(R.color.main_text_color_red));
			}
		});
	}

}
