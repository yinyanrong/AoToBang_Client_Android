package com.aotobang.app.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.aotobang.annotation.BaseActivity;
import com.aotobang.annotation.ContentView;
import com.aotobang.annotation.FindViewNoOnClick;
import com.aotobang.annotation.FindViewOnClick;
import com.aotobang.app.ConstantValues;
import com.aotobang.app.GlobalParams;
import com.aotobang.app.R;
import com.aotobang.app.applaction.AotoBangApp;
import com.aotobang.app.asyntask.LoadAvatarTask;
import com.aotobang.app.callback.ChatCallBack;
import com.aotobang.app.manager.ChatManager;
import com.aotobang.app.manager.OssManager;
import com.aotobang.app.model.DefaultModelImp;
import com.aotobang.app.view.CircleImageView;
import com.aotobang.local.bean.LocalFriend;
import com.aotobang.net.AotoPacket;
import com.aotobang.net.InterfaceIds;
import com.aotobang.net.entity.AotoRequest;
import com.aotobang.utils.DialogUtils;
import com.aotobang.utils.ImageCache;
import com.aotobang.utils.ImageUtils;
import com.aotobang.utils.LogUtil;
import com.aotobang.utils.PathUtil;
import com.soundcloud.android.crop.Crop;

@ContentView(R.layout.activity_regest_2)
public class Regist2Activity extends BaseActivity {
	public static  final String USER_AVATAR="user_avatar";
	public static String USER_AVATAR_FILE_NAME=AotoBangApp.getInstance().getUserId()+USER_AVATAR;
	private static final int TAKE_PICTURE = 0;
	private static final int CHOOSE_PICTURE = 1;
	@FindViewOnClick(R.id.rg2_avatar)
	private CircleImageView rg2_avatar;
	@FindViewNoOnClick(R.id.et_nickname)
	private EditText et_nickname;
	@FindViewOnClick(R.id.radio_button_man)
	private ImageView radio_button_man;
	@FindViewOnClick(R.id.radio_button_woman)
	private ImageView radio_button_woman;
	@FindViewOnClick(R.id.regist_btn_regist)
	private TextView regist_btn_regist;
	@FindViewOnClick(R.id.btn_back)
	private ImageButton btn_back;
	private LocalFriend friend=new LocalFriend();
	private int sex=0;
	private Uri imageUri;
	LocalFriend user;
//	private SelectPhotoUtil selectPhotoUtil;
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.rg2_avatar:
//			selectPhotoUtil.getPhotoFromCamera();
			showPicturePicker();
			break;
		case R.id.regist_btn_regist:
			submit();
			break;
		case R.id.radio_button_man:
			if(sex==0)
				return;
			sex=0;
			radio_button_man.setImageResource(R.drawable.radio_button_checked);
			radio_button_woman.setImageResource(R.drawable.radio_button_unchecked);
			break;
		case R.id.radio_button_woman:
			if(sex==1)
				return;
			sex=1;
			radio_button_man.setImageResource(R.drawable.radio_button_unchecked);
			radio_button_woman.setImageResource(R.drawable.radio_button_checked);
			break;
		case R.id.btn_back:
			finish();
			break;
			
		}
	}

	private void submit() {
		String name=et_nickname.getText().toString().trim();
		if(TextUtils.isEmpty(name)){
			showText("昵称不能为空");
			return;
		}
		DialogUtils.showProgressDialog(Regist2Activity.this);
		AotoRequest request=AotoRequest.createRequest(InterfaceIds.CHANGE_USER_INFO);//更新个人信息
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("name", name);
		map.put("sessionId", AotoBangApp.getInstance().getSessionId());
		map.put("sex",sex);
		map.put("birthday", "");
		map.put("email", "");
		map.put("remark", "");
		map.put("location", "");
		map.put("singedContent", "");
		request.setParameters(map);
		request.setCallBack(Regist2Activity.this);
		sendRequest(request);
		friend.setSex(sex);
	}
	@Override
	public void handleDataResultOnSuccee(int responseId, Object data) {
		DialogUtils.closeProgressDialog();
		switch(responseId){
		case AotoPacket.Message_Type_ACK|InterfaceIds.UPLOAD_FILE_COMPLETE://上传头像
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					showText("上传头像成功");
					ChatManager.getInstance().getFriendDao().update(friend);
				}
			});
			break;
		case AotoPacket.Message_Type_ACK|InterfaceIds.CHANGE_USER_INFO://更新个人信息
			AotoBangApp.getInstance().setUserNick(et_nickname.getText().toString().trim());
			GlobalParams.homeActivity.getHandler().sendEmptyMessage(0);
			ChatManager.getInstance().getFriendDao().update(friend);
			user.setNickname(et_nickname.getText().toString().trim());
			user.setSex(sex);
			finish();
			break;
		
		}
		
		
	}

	@Override
	public void initView() {
		friend.setUserid(AotoBangApp.getInstance().getUserId());
		 user=AotoBangApp.getInstance().getUserList().get(AotoBangApp.getInstance().getUserId());
		if(user.getRemote_avatar()!=null)
			new LoadAvatarTask().execute(rg2_avatar,AotoBangApp.getInstance().getUserId(),user.getRemote_avatar());
		if(AotoBangApp.getInstance().getUserNick()!=null)
			et_nickname.setText(AotoBangApp.getInstance().getUserNick());
		sex=user.getSex();
		if(sex==0){
			radio_button_man.setImageResource(R.drawable.radio_button_checked);
			radio_button_woman.setImageResource(R.drawable.radio_button_unchecked);
		}else{
			radio_button_man.setImageResource(R.drawable.radio_button_unchecked);
			radio_button_woman.setImageResource(R.drawable.radio_button_checked);
		}

	}

	public void showPicturePicker(){
		AlertDialog.Builder builder = new AlertDialog.Builder(Regist2Activity.this);
		builder.setTitle("图片来源");
		builder.setNegativeButton("取消", null);
		builder.setItems(new String[]{"拍照","相册"}, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(which){
				case TAKE_PICTURE:
					Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					imageUri = Uri.fromFile(new File(AotoBangApp.getInstance().getCachePath(DefaultModelImp.PathType.Photo),USER_AVATAR_FILE_NAME));
					openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
					startActivityForResult(openCameraIntent, TAKE_PICTURE);
				break;
				case CHOOSE_PICTURE:
					Crop.pickImage(Regist2Activity.this);
				break;
				}
			}
		});
		builder.create().show();
	}

private void uploadAvatar(String url) {
	friend.setRemote_avatar(url);
	AotoRequest request=new AotoRequest();
	request.setRequestId(InterfaceIds.UPLOAD_FILE_COMPLETE);//上传完成
	Map<String,Object> map=new HashMap<String, Object>();
	map.put("to", "");
	map.put("sessionId", AotoBangApp.getInstance().getSessionId());
	map.put("url", url);
	map.put("type", 100);
	map.put("vTime", 0);
	map.put("photoArr", new ArrayList<String>());
	request.setParameters(map);
	request.setCallBack(Regist2Activity.this);
	sendRequest(request);
}
	private void beginCrop(Uri source) {
		Uri destination = Uri.fromFile(new File(AotoBangApp.getInstance().getCachePath(DefaultModelImp.PathType.Photo), PathUtil.getFileName(source.toString())));
		Crop.of(source, destination).asSquare().start(this);
	}

	private void handleCrop(int resultCode, Intent result) {
		if (resultCode == RESULT_OK) {
			new UploadTask().execute(Crop.getOutput(result));
		} else if (resultCode == Crop.RESULT_ERROR) {
			showText(Crop.getError(result).getMessage());
		}
	}
@Override
protected void onActivityResult(final int requestCode, int resultCode, final Intent data) {
	super.onActivityResult(requestCode, resultCode, data);
	if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
		beginCrop(data.getData());
	} else if (requestCode == Crop.REQUEST_CROP) {
		handleCrop(resultCode, data);
	}else if(requestCode==TAKE_PICTURE&&resultCode==RESULT_OK){
		beginCrop(imageUri);
	}

}


	private  class UploadTask extends AsyncTask<Object, Void, Bitmap> {
		private Uri uri;
		@Override
		protected Bitmap doInBackground(Object[] objects) {
			uri= (Uri) objects[0];
			ContentResolver resolver = getContentResolver();
			Bitmap bitmap = null;
			try {
				bitmap = MediaStore.Images.Media.getBitmap(resolver, uri);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			if(new File(AotoBangApp.getInstance().getCachePath(DefaultModelImp.PathType.Photo),USER_AVATAR_FILE_NAME).length()>1024*100){
				byte[] data= ImageUtils.bitmap2Bytes(bitmap, true);
				bitmap= BitmapFactory.decodeByteArray(data, 0, data.length);
			}
			return bitmap;
		}

		@Override
		protected void onPostExecute(final Bitmap bitmap) {
			if (bitmap == null) {
				showText("获取图片失败");
				return;
			}

			ImageCache.getInstance().remove(AotoBangApp.getInstance().getCachePath(DefaultModelImp.PathType.Img) + USER_AVATAR_FILE_NAME);
			ImageCache.getInstance().put(AotoBangApp.getInstance().getCachePath(DefaultModelImp.PathType.Img) + USER_AVATAR_FILE_NAME, bitmap);
			friend.setLocal_avatar(AotoBangApp.getInstance().getCachePath(DefaultModelImp.PathType.Img) + USER_AVATAR_FILE_NAME);
			rg2_avatar.setImageBitmap(bitmap);
			LogUtil.info(PhotoActivity.class, uri.toString());
			OssManager.getInstance().uploadImg("avatar", AotoBangApp.getInstance().getUserId(), USER_AVATAR_FILE_NAME,bitmap, new ChatCallBack() {

				@Override
				public void onSuccess(String objectKey) {
					GlobalParams.homeActivity.getHandler().sendEmptyMessage(0);
					uploadAvatar(objectKey);
				}

				@Override
				public void onProgress(int progress, int totalSize) {
				}

				@Override
				public void onError(int code, String error) {

				}

				@Override
				public void onSuccess() {
				}
			},true, ConstantValues.PHOTO_BUCKET);


		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
