package com.aotobang.app.activity;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.aotobang.annotation.BaseActivity;
import com.aotobang.annotation.ContentView;
import com.aotobang.annotation.FindViewNoOnClick;
import com.aotobang.annotation.FindViewOnClick;
import com.aotobang.app.ConstantValues;
import com.aotobang.app.R;
import com.aotobang.app.applaction.AotoBangApp;
import com.aotobang.app.entity.PreLoginData;
import com.aotobang.app.manager.ChatManager;
import com.aotobang.app.service.AotoNetService;
import com.aotobang.local.bean.LocalFriend;
import com.aotobang.net.AotoConnect;
import com.aotobang.net.AotoNetEngineProxy;
import com.aotobang.net.AotoPacket;
import com.aotobang.net.InterfaceIds;
import com.aotobang.net.entity.AotoRequest;
import com.aotobang.utils.DialogUtils;
import com.aotobang.utils.LogUtil;
@ContentView(R.layout.activity_regest_1)
public class Regist1Activity extends BaseActivity {
	@FindViewOnClick(R.id.btn_back)
	private ImageButton btn_back;
	@FindViewNoOnClick(R.id.et_phone_num)
	private EditText et_phone_num;
	@FindViewNoOnClick(R.id.et_verifi)
	private EditText et_verifi;
	@FindViewNoOnClick(R.id.et_pwd)
	private EditText et_pwd;
	@FindViewNoOnClick(R.id.et_pwd_confirm)
	private EditText et_pwd_confirm;
	@FindViewOnClick(R.id.regist_btn_regist)
	private TextView regist_btn_regist;
	@FindViewOnClick(R.id.verifi_code)
	private TextView verifi_code;
	
	

	//验证码的定时器
		private final Timer timer = new Timer(); 
		private TimerTask task; 
		 private	int i=60;
		 Handler handler = new Handler() {
			    @Override 
			    public void handleMessage(Message msg) { 
			        // TODO  要做的事情 
			        if(i>0){
			        	i--;
			        	verifi_code.setText("等待"+i+"秒");
			        }else{
			        	task.cancel();
			        	verifi_code.setClickable(true);
			        	verifi_code.setText("重新获取验证码");
			        }
			    }
			};
			

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_back:
			finish();
			break;
		case R.id.regist_btn_regist:
			regist();
			break;
		case R.id.verifi_code:
			getVerifiCode();
			break;
		
		
		}
	}

	private void getVerifiCode() {
	
		String phone=et_phone_num.getText().toString().trim();
		if(TextUtils.isEmpty(phone)||phone.length()!=11||phone.startsWith("0")){
			showText("手机号码不正确");
			return;
			
		}
		DialogUtils.showProgressDialog(Regist1Activity.this);
		AotoRequest request=new AotoRequest();
		request.setRequestId(InterfaceIds.VERIFY_CODE);
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("phone", phone);
		request.setParameters(map);
		request.setCallBack(Regist1Activity.this);
		AotoNetEngineProxy.getInstance(Regist1Activity.this).getNetDataShort(request,ConstantValues.HOST,ConstantValues.PORT);//访问网络的方式
	}

	private void regist() {
		String phone=et_phone_num.getText().toString().trim();
		String verifi=et_verifi.getText().toString().trim();
		String pwd=et_pwd.getText().toString().trim();
		String pwd_confirm=et_pwd_confirm.getText().toString().trim();
		if(TextUtils.isEmpty(phone)||phone.length()!=11||phone.startsWith("0")){
			showText("手机号码不正确");
			return;
		}
		if(TextUtils.isEmpty(pwd)||TextUtils.isEmpty(pwd_confirm)){
			showText("请输入密码或者确认密码");
			return;
		}
		if(!pwd.equals(pwd_confirm)){
			showText("两次密码输入不一致");
			return;
		}
		if(TextUtils.isEmpty(verifi)){
			showText("验证码不能为空");
			return;
		}
		DialogUtils.showProgressDialog(Regist1Activity.this);
		AotoRequest request=new AotoRequest();
		request.setRequestId(InterfaceIds.PHONE_REGIST);
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("phone", phone);
		map.put("pwd", pwd);
		map.put("verifyCode", verifi);
		map.put("type", 0);
		map.put("mode", 0);
		request.setParameters(map);
		request.setCallBack(Regist1Activity.this);
		AotoNetEngineProxy.getInstance(Regist1Activity.this).getNetDataShort(request,ConstantValues.HOST,ConstantValues.PORT);//注册
		
	}

	@Override
	public void handleDataResultOnSuccee(int responseId, final Object data) {
		switch(responseId){
		case AotoPacket.Message_Type_ACK|InterfaceIds.VERIFY_CODE:
			DialogUtils.closeProgressDialog();
			LogUtil.info(Regist1Activity.class, "短信已经发送");
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					showText("短信已经发送，请查收");
					verifi_code.setClickable(false);
					i=60;
					task = new TimerTask() { 
					    @Override 
					    public void run() { 
					        // TODO 操作定时器 
					        handler.sendEmptyMessage(0); 
					    } 
					}; 
					timer.schedule(task, 1000, 1000); 
				}
			});
			
			break;
		case AotoPacket.Message_Type_ACK|InterfaceIds.PHONE_REGIST://登陆
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					PreLoginData pld=(PreLoginData)data;
					AotoBangApp.getInstance().setSessionId(pld.getSessionId());
					AotoBangApp.getInstance().setUserId(pld.getUuid());
					AotoBangApp.getInstance().initCachePath();
					AotoBangApp.getInstance().setUserName(et_phone_num.getText().toString().trim());
					AotoBangApp.getInstance().setUserNick(et_phone_num.getText().toString().trim());
					AotoBangApp.getInstance().setPassword(et_pwd.getText().toString().trim());
					 ChatManager.getInstance().registReceiveMsgListener();
					 if(!AotoConnect.getInstance().isConnect()){
						 Intent intent = new Intent(getApplicationContext(), 
				                 AotoNetService.class); 
				        startService(intent);
					 }
					 ChatManager.getInstance().initDao();
					 LocalFriend friend=new LocalFriend();
					 friend.setNickname(et_phone_num.getText().toString().trim());
					 friend.setUserid(pld.getUuid());
					 ChatManager.getInstance().getFriendDao().insert(friend);
					 AotoRequest request=new AotoRequest();
			      		request.setRequestId(InterfaceIds.LOGIN);
			      		Map<String,Object> map=new HashMap<String, Object>();
			      		map.put("uuid", AotoBangApp.getInstance().getUserId());
			      		map.put("sessionId", AotoBangApp.getInstance().getSessionId());
			    		map.put("state", 0);
			    		map.put("type", 0);
						map.put("macAddress", AotoBangApp.getInstance().getLocalMacAddress());
			      		request.setParameters(map);
			      		request.setCallBack(Regist1Activity.this);
			            sendRequest(request); 
					
					
					
				}
			});
			break;
		case AotoPacket.Message_Type_ACK|InterfaceIds.LOGIN:
			DialogUtils.closeProgressDialog();
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					showText("注册成功");
					Intent intent=new Intent();
					intent.putExtra("from", "regist");
					intent.setClass(Regist1Activity.this, HomeActivity.class);
					startActivity(intent);
					finish();
				}
			});
			
			break;
		}
	}

	@Override
	public void initView() {
		
		
	}

}
