package com.aotobang.app.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.aotobang.annotation.BaseActivity;
import com.aotobang.annotation.ContentView;
import com.aotobang.annotation.FindViewOnClick;
import com.aotobang.app.ConstantValues;
import com.aotobang.app.R;
import com.aotobang.app.applaction.AotoBangApp;
import com.aotobang.app.asyntask.LoadPhotoTask;
import com.aotobang.app.callback.ChatCallBack;
import com.aotobang.app.manager.ChatManager;
import com.aotobang.app.manager.OssManager;
import com.aotobang.app.model.DefaultModelImp;
import com.aotobang.app.view.DraggableGridView;
import com.aotobang.local.bean.Photo;
import com.aotobang.net.AotoPacket;
import com.aotobang.net.InterfaceIds;
import com.aotobang.net.entity.AotoRequest;
import com.aotobang.utils.ImageCache;
import com.aotobang.utils.ImageTools;
import com.aotobang.utils.ImageUtils;
import com.aotobang.utils.LogUtil;
import com.aotobang.utils.PathUtil;
import com.aotobang.utils.PictureUtil;
import com.soundcloud.android.crop.Crop;

import org.json.JSONArray;
import org.json.JSONObject;

@ContentView(R.layout.activity_photo)
public class PhotoActivity extends BaseActivity {
@FindViewOnClick(R.id.btn_back)
private ImageButton btn_back;
@FindViewOnClick(R.id.drag_grid)
private DraggableGridView drag_grid;
    private   LayoutInflater 	mInflater;
    private  AlertDialog  dialog;
    private  TextView tv_progress;
    private boolean updata=false;
    private int updataIndex=-1;
    private List<Photo> photoInfos=new ArrayList<Photo>();
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_back:
            saveInfo();
            finish();
			break;

		}
	}

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.info(PhotoActivity.class, "onResume");
    }

    public  int  getPhotoSize(){
    return photoInfos.size();
}
	private void selectPic() {
        Crop.pickImage(PhotoActivity.this);
	}

@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
        beginCrop(data.getData());
    } else if (requestCode == Crop.REQUEST_CROP) {
        handleCrop(resultCode, data);
    }

	}
    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(AotoBangApp.getInstance().getCachePath(DefaultModelImp.PathType.Photo), "cropped"+ UUID.randomUUID().toString()));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            new UploadTask().execute(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
	@Override
	public void handleDataResultOnSuccee(int responseId,final Object data) {
        switch (responseId){
            case AotoPacket.Message_Type_ACK|InterfaceIds.PHOTO_INFO:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        photoInfos=copyData(data);
                        LogUtil.info(PhotoActivity.class,photoInfos.size()+"---PHOTO_INFO");
                        for(int i=0;i<6;i++){
                            View v=  mInflater.inflate(R.layout.photo_item,null,false);
                            drag_grid.addView(v);
                        }
                        if(photoInfos.size()>0){
                            for(int i=0;i<photoInfos.size();i++){
                                new LoadPhotoTask().execute(drag_grid.getChildAt(i),photoInfos.get(i));
                            }
                        }
                    }
                });

                break;
            case AotoPacket.Message_Type_ACK|InterfaceIds.UPLOAD_FILE_COMPLETE://上传完成
                for(int i=0;i<6;i++) {
                    Photo p;
                    if(photoInfos.size()>i)
                     p = photoInfos.get(i);
                    else
                    p=new Photo();
                    p.setPhoto_index(i);
                    ChatManager.getInstance().getPhotoDao().update(p);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showText("保存成功");
                        finish();
                    }
                });
                break;
        }
	}

    @Override
    public void handleDataResultOnError(int responseId, int errCode, Object error) {
        super.handleDataResultOnError(responseId, errCode, error);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveInfo();
    }

    private void saveInfo() {  //上传图片
        AotoRequest request=new AotoRequest();
        request.setRequestId(InterfaceIds.UPLOAD_FILE_COMPLETE);//完成
        Map<String,Object> map=new HashMap<String, Object>();
        map.put("to", "");
        map.put("sessionId", AotoBangApp.getInstance().getSessionId());
        map.put("url", "");
        map.put("type", 101);
        map.put("vTime", 0);
        List<String> infos=new ArrayList<String>();
        for(int i=0;i<photoInfos.size();i++){
            infos.add(photoInfos.get(i).getRemote_url());
        }
        map.put("photoArr", infos);
        request.setParameters(map);
        request.setCallBack(PhotoActivity.this);
        sendRequest(request);



    }

    private List<Photo> copyData(Object data) {
        List<String>  list= (List<String>) data;
        List<Photo>  photos=new ArrayList<Photo>();
        for(int i=0;i<list.size();i++){
            if(i==6)
                break;
            Photo photo =new Photo();
            photo.setRemote_url(list.get(i));
            photo.setPhoto_index(i);
            photos.add(photo);
        }
        return photos;
    }

    @Override
	public void initView() {
        drag_grid.setActivity(PhotoActivity.this);
        mInflater= (LayoutInflater)PhotoActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        List<Photo>  photos=ChatManager.getInstance().getPhotoDao().findAll();
        if(photos!=null&&photos.get(0).getRemote_url()!=null){
            for(int i=0;i<6;i++ ){
                View v=  mInflater.inflate(R.layout.photo_item,null,false);
                if(photos.get(i).getRemote_url()!=null){
                    photoInfos.add(photos.get(i));
                }
                drag_grid.addView(v);
            }
            for(int i=0;i<photoInfos.size();i++){
                new LoadPhotoTask().execute(drag_grid.getChildAt(i),photoInfos.get(i));
            }
        }else{
            getPhotoInfos();

        }
        setListeners();
		
	}

    private void getPhotoInfos() {
        AotoRequest request=new AotoRequest();
        request.setRequestId(InterfaceIds.PHOTO_INFO);
        Map<String,Object> map=new HashMap<String, Object>();
        map.put("uuid", AotoBangApp.getInstance().getUserId());
        map.put("sessionId", AotoBangApp.getInstance().getSessionId());
        request.setParameters(map);
        request.setCallBack(PhotoActivity.this);
        sendRequest(request);
    }

    private void setListeners() {
        drag_grid.setOnRearrangeListener(new DraggableGridView.OnRearrangeListener() {
            public void onRearrange(int oldIndex, int newIndex) {
                Photo p = photoInfos.remove(oldIndex);
                photoInfos.add(newIndex, p);
                LogUtil.info(PhotoActivity.class,photoInfos.size()+"--size");
            }
        });
        drag_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (photoInfos == null || position + 1 > photoInfos.size()) {
                    selectPic();
                } else {
                    showUpdataDialog(position);
                }


            }
        });
    }

    private void showUpdataDialog(final int position) {
        AlertDialog.Builder builder=new AlertDialog.Builder(PhotoActivity.this);
        builder.setItems(new String[]{"选择相片", "删除相片"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        updata=true;
                        updataIndex=position;
                        selectPic();
                        break;
                    case 1:
                        photoInfos.remove(position);
                        drag_grid.removeViewAt(position);
                        View v=  mInflater.inflate(R.layout.photo_item,null,false);
                        drag_grid.addView(v);
                        break;
                }

            }
        });
          dialog=builder.create();
            dialog.show();
    }


    private  class UploadTask extends AsyncTask<Object, Void, Bitmap>{
        @Override
        protected void onPreExecute() {
            View v = drag_grid.getChildAt(photoInfos.size());
            v.setBackgroundColor(getResources().getColor(android.R.color.white));
            tv_progress = (TextView) v.findViewById(R.id.tv_progress);
            tv_progress.setVisibility(View.VISIBLE);
            tv_progress.setText("上传中...");
        }

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
           if(bitmap.getByteCount()>1024*100){
                byte[] data=ImageUtils.bitmap2Bytes(bitmap,true);
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

            LogUtil.info(PhotoActivity.class,uri.toString());
            OssManager.getInstance().uploadImg("photo", AotoBangApp.getInstance().getUserId(), PathUtil.getFileName(uri.toString()), bitmap, new ChatCallBack() {
                @Override
                public void onSuccess(final String objectKey) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (tv_progress != null)
                                tv_progress.setVisibility(View.GONE);
                            LogUtil.info(PhotoActivity.class, objectKey);
                            ImageCache.getInstance().put(AotoBangApp.getInstance().getCachePath(DefaultModelImp.PathType.Img) + PathUtil.getFileName(uri.toString()), bitmap);
                            View v;
                            Photo p = new Photo();
                            p.setLocal_url(AotoBangApp.getInstance().getCachePath(DefaultModelImp.PathType.Img) + PathUtil.getFileName(uri.toString()));
                            p.setRemote_url(objectKey);
                            if (updata) {
                                p.setPhoto_index(updataIndex);
                                 v = drag_grid.getChildAt(updataIndex);
                                photoInfos.remove(updataIndex);
                                photoInfos.add(updataIndex, p);
                                updata=false;
                                updataIndex=-1;
                            } else {
                                p.setPhoto_index(photoInfos.size());
                                 v = drag_grid.getChildAt(photoInfos.size());
                                photoInfos.add(p);
                            }
                            v.setBackgroundDrawable(ImageTools.bitmapToDrawable(bitmap));
                            ChatManager.getInstance().getPhotoDao().update(p);
                        }
                    });


                }

                @Override
                public void onProgress(final int progress, final int totalSize) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            float a = ((float) progress / (float) totalSize);
                            int b = (int) (Math.round(a * 100));
                            tv_progress.setText(b + "%");
                        }
                    });


                }

                @Override
                public void onError(int code, String error) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            View v = drag_grid.getChildAt(photoInfos.size());
                            v.setBackgroundResource(R.drawable.profile_edit_add_picture300);
                            tv_progress.setVisibility(View.GONE);
                        }
                    });

                }

                @Override
                public void onSuccess() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_progress.setVisibility(View.GONE);
                        }
                    });
                }
            }, true, ConstantValues.PHOTO_BUCKET);

        }
    }

}
