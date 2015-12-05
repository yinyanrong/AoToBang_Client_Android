package com.aotobang.app.asyntask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;

import com.alibaba.sdk.android.oss.model.OSSException;
import com.alibaba.sdk.android.oss.storage.OSSData;
import com.aotobang.app.ConstantValues;
import com.aotobang.app.applaction.AotoBangApp;
import com.aotobang.app.manager.ChatManager;
import com.aotobang.app.manager.OssManager;
import com.aotobang.app.model.DefaultModelImp;
import com.aotobang.app.view.DraggableGridView;
import com.aotobang.local.bean.Photo;
import com.aotobang.utils.ImageCache;
import com.aotobang.utils.ImageTools;
import com.aotobang.utils.PathUtil;

import java.util.List;

/**
 * Created by Admin on 2015/7/21.
 */
public class LoadPhotoTask extends AsyncTask<Object,Void,Bitmap> {
    private Photo photo;
    private View child;
    @Override
    protected Bitmap doInBackground(Object... objects) {
        child= (View) objects[0];
        photo= (Photo) objects[1];

            Bitmap  img=null;
            if(photo.getLocal_url()!=null)
              img= ImageCache.getInstance().get(photo.getLocal_url());
            if(img==null&&photo.getRemote_url()!=null){
                OSSData  data= OssManager.getInstance().downLoadData(photo.getRemote_url(), ConstantValues.PHOTO_BUCKET);
                try {
                    byte[]  imgData=data.get();
                    img= BitmapFactory.decodeByteArray(imgData,0,imgData.length);
                    if(photo.getLocal_url()==null){
                        photo.setLocal_url(AotoBangApp.getInstance().getCachePath(DefaultModelImp.PathType.Img) + PathUtil.getFileName(photo.getRemote_url()));
                        ChatManager.getInstance().getPhotoDao().update(photo);
                    }
                    ImageCache.getInstance().put(photo.getLocal_url(),img);
                } catch (OSSException e) {
                    e.printStackTrace();
                }


            }




        return img;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
            if(bitmap!=null)
                child.setBackgroundDrawable(ImageTools.bitmapToDrawable(bitmap));
    }
}
