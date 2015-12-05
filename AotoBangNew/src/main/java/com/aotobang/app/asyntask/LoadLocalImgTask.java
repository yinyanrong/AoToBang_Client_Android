package com.aotobang.app.asyntask;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.alibaba.sdk.android.oss.model.OSSException;
import com.alibaba.sdk.android.oss.storage.OSSData;
import com.aotobang.app.ConstantValues;
import com.aotobang.app.GlobalParams;
import com.aotobang.app.activity.Regist2Activity;
import com.aotobang.app.activity.ShowBigImgActivity;
import com.aotobang.app.applaction.AotoBangApp;
import com.aotobang.app.manager.OssManager;
import com.aotobang.app.model.DefaultModelImp;
import com.aotobang.utils.ImageCache;
import com.aotobang.utils.ImageUtils;
import com.aotobang.utils.PathUtil;

public class LoadLocalImgTask extends AsyncTask<Object, Void, Bitmap> {
	private ImageView iv = null;
	private String localFullSizePath = null;
	private String thumbnailPath = null;
	private String remotePath=null;
	private Context ctx;
	
	@Override
	protected Bitmap doInBackground(Object... params) {
		iv=(ImageView) params[0];
		thumbnailPath=(String) params[1];
		localFullSizePath=(String)params[2];
		ctx=(Context)params[3];
		remotePath=(String) params[4];
		Bitmap result=ImageUtils.decodeScaleImage(localFullSizePath, 160, 160);
		if(result==null&&remotePath!=null){
			OSSData avatar= OssManager.getInstance().downLoadData(remotePath);
			try {
				byte[] data=avatar.get();
				if(data!=null) {
					result = BitmapFactory.decodeByteArray(data, 0, data.length);
					ImageCache.getInstance().put(localFullSizePath, result);
					result=ImageUtils.decodeScaleImage(localFullSizePath, 160, 160);
				}

			} catch (OSSException e) {
				e.printStackTrace();
				return null;

			}



		}


		return result;
	}
	@Override
	protected void onPostExecute(Bitmap result) {
		if(result!=null){
			iv.setImageBitmap(result);
			ImageCache.getInstance().put(thumbnailPath, result);
			iv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {

					if (thumbnailPath != null) {

						Intent intent = new Intent(ctx, ShowBigImgActivity.class);
						File file = new File(localFullSizePath);
						if (file.exists()) {
							Uri uri = Uri.fromFile(file);
							intent.putExtra("uri", uri);
						} else {
							// The local full size pic does not exist yet.
							// ShowBigImage needs to download it from the server
							// first
							intent.putExtra("remotepath", remotePath);
						}
						/*if (message != null && message.direct == EMMessage.Direct.RECEIVE && !message.isAcked) {
							message.isAcked = true;
							try {
								// 看了大图后发个已读回执给对方
								EMChatManager.getInstance().ackMessageRead(message.getFrom(), message.getMsgId());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}*/
						ctx.startActivity(intent);
					}
				
					
					
					
					
					
					
				}
			});
			
		}
		
		
		
	}

}
