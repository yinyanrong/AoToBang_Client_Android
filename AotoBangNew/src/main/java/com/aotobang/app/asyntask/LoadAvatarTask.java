package com.aotobang.app.asyntask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.AsyncTask;

import com.alibaba.sdk.android.oss.model.OSSException;
import com.alibaba.sdk.android.oss.storage.OSSData;
import com.aotobang.app.ConstantValues;
import com.aotobang.app.R;
import com.aotobang.app.activity.Regist2Activity;
import com.aotobang.app.applaction.AotoBangApp;
import com.aotobang.app.manager.OssManager;
import com.aotobang.app.model.DefaultModelImp.PathType;
import com.aotobang.app.view.CircleImageView;
import com.aotobang.utils.ImageCache;
import com.aotobang.utils.LogUtil;

public class LoadAvatarTask extends AsyncTask<Object, Void, Bitmap> {
	private CircleImageView iv;
	private String userId;
	private String remoteUrl;
	private Bitmap bitmap;

	@Override
	protected Bitmap doInBackground(Object... params) {
		iv=(CircleImageView) params[0];
		userId=(String) params[1];
		remoteUrl=(String) params[2];
		LogUtil.info(LoadAvatarTask.class, userId+Regist2Activity.USER_AVATAR);
		bitmap=ImageCache.getInstance().get(AotoBangApp.getInstance().getCachePath(PathType.Img)+userId+Regist2Activity.USER_AVATAR);
		if(bitmap!=null)
			return bitmap;
		else if(remoteUrl!=null){
			OSSData avatar=OssManager.getInstance().downLoadData(remoteUrl, ConstantValues.PHOTO_BUCKET);
			try {
				byte[] data=avatar.get();
				if(data!=null){
				bitmap=BitmapFactory.decodeByteArray(data, 0, data.length);
				ImageCache.getInstance().put(AotoBangApp.getInstance().getCachePath(PathType.Img)+userId+Regist2Activity.USER_AVATAR, bitmap,false);
				return bitmap;
				}else
					return null;
				
			} catch (OSSException e) {
				e.printStackTrace();
				return null;
			
			}
			
		}
		return null;
	}
	@Override
	protected void onPostExecute(Bitmap result) {
		
		if(result!=null)
		iv.setImageBitmap(result);
		else
			iv.setImageResource(R.drawable.img_01);
		
		iv.setTag("set");
	}

}
