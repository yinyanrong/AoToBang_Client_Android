package com.aotobang.utils;


import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.alibaba.sdk.android.oss.callback.GetFileCallback;
import com.alibaba.sdk.android.oss.model.OSSException;
import com.alibaba.sdk.android.oss.storage.OSSFile;
import com.aotobang.app.R;
import com.aotobang.app.activity.Regist2Activity;
import com.aotobang.app.applaction.AotoBangApp;
import com.aotobang.app.callback.ChatCallBack;
import com.aotobang.app.manager.OssManager;
import com.aotobang.app.model.DefaultModelImp.PathType;

public class UserUtils {
public static void  setAvatar(final ImageView view,boolean fromCache){
	Bitmap img=null;
	if(fromCache)
	 img=ImageCache.getInstance().get(Regist2Activity.USER_AVATAR_FILE_NAME);
	else
		img=ImageTools.getPhotoFromSDCard(AotoBangApp.getInstance().getCachePath(PathType.Img), Regist2Activity.USER_AVATAR_FILE_NAME);
	LogUtil.info(UserUtils.class,img==null?"yes":"no");
	if(img!=null){

		view.setImageBitmap(img);
	}else{
		String remotePath=AotoBangApp.getInstance().getUserList().get(AotoBangApp.getInstance().getUserId()).getRemote_avatar();
		if(remotePath==null)
			view.setImageResource(R.drawable.img_01);
		else{
			OSSFile avatar=OssManager.getInstance().downloadFile(remotePath);
			avatar.downloadToInBackground(AotoBangApp.getInstance().getCachePath(PathType.Img)+Regist2Activity.USER_AVATAR_FILE_NAME, new GetFileCallback() {

				@Override
				public void onProgress(String arg0, int arg1, int arg2) {
				}

				@Override
				public void onFailure(String arg0, OSSException arg1) {
				}

				@Override
				public void onSuccess(String arg0, String arg1) {
					Bitmap bit=ImageTools.getPhotoFromSDCard(AotoBangApp.getInstance().getCachePath(PathType.Img), Regist2Activity.USER_AVATAR_FILE_NAME);
					view.setImageBitmap(bit);
					ImageCache.getInstance().put(AotoBangApp.getInstance().getCachePath(PathType.Img)+Regist2Activity.USER_AVATAR_FILE_NAME, bit);


				}
			});
		}

	}

}

public static void setPeerAvatar(final ImageView view,final String userId,String remote,final ChatCallBack callback){
		Bitmap 	img=ImageCache.getInstance().get(userId+Regist2Activity.USER_AVATAR);
	if(img!=null){
		view.setImageBitmap(img);
		return;

	}
		if(remote!=null){
		//	LogUtil.info(UserUtils.class, remote);
			OSSFile avatar=OssManager.getInstance().downloadFile(remote);
			avatar.downloadToInBackground(AotoBangApp.getInstance().getCachePath(PathType.Img)+userId+Regist2Activity.USER_AVATAR, new GetFileCallback() {

				@Override
				public void onProgress(String arg0, int arg1, int arg2) {
				}

				@Override
				public void onFailure(String arg0, OSSException arg1) {
				}

				@Override
				public void onSuccess(String arg0, String arg1) {
					callback.onSuccess();
					/*Bitmap bit=ImageTools.getPhotoFromSDCard(AotoBangApp.getInstance().getCachePath(PathType.Img), userId+Regist2Activity.USER_AVATAR);
					view.setImageBitmap(bit);
					ImageCache.getInstance().put(AotoBangApp.getInstance().getCachePath(PathType.Img)+userId+Regist2Activity.USER_AVATAR, bit);*/
				}
			});


		}else
			view.setImageResource(R.drawable.img_01);


	}



}
