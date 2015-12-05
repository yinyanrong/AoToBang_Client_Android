package com.aotobang.app.activity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.callback.GetFileCallback;
import com.alibaba.sdk.android.oss.model.OSSException;
import com.alibaba.sdk.android.oss.storage.OSSFile;
import com.aotobang.annotation.BaseActivity;
import com.aotobang.annotation.ContentView;
import com.aotobang.annotation.FindViewNoOnClick;
import com.aotobang.annotation.FindViewOnClick;
import com.aotobang.app.ConstantValues;
import com.aotobang.app.GlobalParams;
import com.aotobang.app.R;
import com.aotobang.app.applaction.AotoBangApp;
import com.aotobang.app.entity.PreLoginData;
import com.aotobang.app.entity.UserInfo;
import com.aotobang.app.manager.ChatManager;
import com.aotobang.app.manager.OssManager;
import com.aotobang.app.model.DefaultModelImp.PathType;
import com.aotobang.app.service.AotoNetService;
import com.aotobang.local.bean.LocalFriend;
import com.aotobang.net.AotoConnect;
import com.aotobang.net.AotoNetEngineProxy;
import com.aotobang.net.AotoPacket;
import com.aotobang.net.InterfaceIds;
import com.aotobang.net.entity.AotoRequest;
import com.aotobang.utils.DialogUtils;
@ContentView(R.layout.activity_login)
public class LoginActivity extends BaseActivity {
	@FindViewNoOnClick(R.id.login_edit_username)
	private EditText login_edit_username;
	@FindViewNoOnClick(R.id.login_edit_pwd)
	private EditText login_edit_pwd;
	@FindViewOnClick(R.id.text_login)
	private TextView text_login;
	@FindViewOnClick(R.id.btn_back)
	private ImageButton btn_back;
	private boolean login=false;
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.text_login:
			login();
			break;
		case R.id.btn_back:
			finish();
		break;
			
		
		}
	}
@Override
protected void onDestroy() {
	super.onDestroy();
	
}
	private void login() {
		String name=login_edit_username.getText().toString().trim();
		String pwd=login_edit_pwd.getText().toString().trim();
		if(TextUtils.isEmpty(name)){
			showText("用户名不能为空！");
			return;
		}
		if(TextUtils.isEmpty(pwd)){
			showText("密码不能为空！");
			return;
			
		}
		DialogUtils.showProgressDialog(LoginActivity.this);
		AotoRequest request=new AotoRequest();
		request.setRequestId(InterfaceIds.PRE_LOGIN);
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("value", name);
		map.put("macAddress", AotoBangApp.getInstance().getLocalMacAddress());
		map.put("pwd", pwd);
		request.setParameters(map);
		request.setCallBack(LoginActivity.this);
		AotoNetEngineProxy.getInstance(LoginActivity.this).getNetDataShort(request,ConstantValues.HOST,ConstantValues.PORT);//前置登陆
		
		
	}

	@Override
	public void handleDataResultOnSuccee(int responseId, Object data) {
		switch(responseId){
		case AotoPacket.Message_Type_ACK|InterfaceIds.PRE_LOGIN:
			PreLoginData pld=(PreLoginData)data;
			AotoBangApp.getInstance().setSessionId(pld.getSessionId());
			AotoBangApp.getInstance().setUserId(pld.getUuid());
			AotoBangApp.getInstance().initCachePath();
			AotoBangApp.getInstance().setUserName(login_edit_username.getText().toString().trim());
			AotoBangApp.getInstance().setPassword(login_edit_pwd.getText().toString().trim());
			ChatManager.getInstance().init();
			/* if(!AotoConnect.getInstance().isConnect()){
				 Intent intent = new Intent(getApplicationContext(), 
		                 AotoNetService.class); 
		        startService(intent);
			 }*/
			 	AotoRequest request=new AotoRequest();
	      		request.setRequestId(InterfaceIds.LOGIN);
	      		Map<String,Object> map=new HashMap<String, Object>();
	      		map.put("uuid", AotoBangApp.getInstance().getUserId());
	      		map.put("sessionId", AotoBangApp.getInstance().getSessionId());//
	    		map.put("state", 0);
	    		map.put("type", 0);
				map.put("mode", 0);
				map.put("macAddress",  AotoBangApp.getInstance().getLocalMacAddress());
	      		request.setParameters(map);
	      		request.setCallBack(LoginActivity.this);
	            sendRequest(request); 
	           
			break;
		case AotoPacket.Message_Type_ACK|InterfaceIds.LOGIN:
			DialogUtils.closeProgressDialog();
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					if(GlobalParams.mainActivity!=null)
					GlobalParams.mainActivity.finish();
					showText("登陆成功");
				}
			});
			UserInfo info=(UserInfo)data;
			if(ChatManager.getInstance().getFriendDao().findByUserId(AotoBangApp.getInstance().getUserId())==null){
				LocalFriend friend=new LocalFriend();
				friend.setLocal_avatar(AotoBangApp.getInstance().getCachePath(PathType.Img)+Regist2Activity.USER_AVATAR_FILE_NAME);
				friend.setUserid(AotoBangApp.getInstance().getUserId());
				friend.setNickname(info.getNick());
				friend.setSex(info.getSex());
				friend.setRemote_avatar(info.getIcon());
				friend.setUsername(login_edit_username.getText().toString().trim());
				ChatManager.getInstance().getFriendDao().insert(friend);
				
			}
			DialogUtils.closeProgressDialog();
			if(info.getNick()!=null){
				AotoBangApp.getInstance().setUserNick(info.getNick());
			}
			if(info.getIcon()!=null)
				GlobalParams.userIcon=info.getIcon();
			login=true;
		 	Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
		 //	intent.putExtra("from", "regist");
  			startActivity(intent);
  			finish();
			break;
		default :
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					showText("未知接口");
				}
			});
			break;
		}
		
		
	}

	@Override
	public void handleDataResultOnError(int responseId, int errCode, final Object error) {
			super.handleDataResultOnError(responseId, errCode, error);
	}

	@Override
	public void initView() {
	
		
	}

}
