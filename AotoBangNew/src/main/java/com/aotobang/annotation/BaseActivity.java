package com.aotobang.annotation;




import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.aotobang.app.R;
import com.aotobang.app.activity.LaunchActivity;
import com.aotobang.app.activity.LoginActivity;
import com.aotobang.app.applaction.AotoBangApp;
import com.aotobang.net.AotoNetEngineProxy;
import com.aotobang.net.callback.IDataCallBack;
import com.aotobang.net.entity.AotoRequest;
import com.aotobang.utils.DialogUtils;
import com.aotobang.utils.LogUtil;
import com.aotobang.app.view.ToastView;
public abstract class BaseActivity extends Activity implements OnClickListener,IDataCallBack {
	public InputMethodManager imm;	
	public SharedPreferences sp;//
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		requestWindowFeature(Window.FEATURE_NO_TITLE);//不显示标题
		AnnotationManager.startAnnotation(this);
		sp=PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);
		LogUtil.info(BaseActivity.class, getClass().getName());
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		initView();
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 点击空白处隐藏软键盘
		View view = this.getCurrentFocus();
		if (view != null)
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		return super.onTouchEvent(event);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	public abstract void initView();
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}
	public void sendRequest(AotoRequest request){
		AotoNetEngineProxy.getInstance(BaseActivity.this).sendReqeust(request);
	}
	@Override
	public void handleDataResultOnError(int responseId, int errCode, final Object error) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				DialogUtils.closeProgressDialog();

				if (((BaseActivity.this) instanceof LaunchActivity) && ((String) error).equals(getString(R.string.token_error))) {
					Toast.makeText(BaseActivity.this, "登陆超时了,请从新登陆", Toast.LENGTH_SHORT).show();
					AotoBangApp.getInstance().logOut();
					startActivity(new Intent(BaseActivity.this, LoginActivity.class));
					finish();
					return;

				}
				Toast.makeText(BaseActivity.this, (String) error, Toast.LENGTH_SHORT).show();
				
			}
		});
		
	}
	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
	}
	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
	}
	@Override
	public void finish() {
		super.finish();
		if(!((BaseActivity.this)  instanceof LaunchActivity))
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}

	protected void showText(String text) {
		new ToastView(this, text).show();
	}
}
