package com.aotobang.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class PathUtil {
	private static PathUtil instance;
	private PathUtil(){
		
	}
	public static PathUtil getInstance(){
		if(instance==null){
		synchronized (PathUtil.class) {
			if(instance==null)
				instance=new PathUtil();
		}
		}
		
		return instance;
	}
	
	public static String getImgPathByUri(Uri uri,Context context){

		String[] proj = { MediaStore.Images.Media.DATA };

		Cursor actualimagecursor =context.getContentResolver().query(uri,proj,null,null,null);

		int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

		actualimagecursor.moveToFirst();

		String img_path = actualimagecursor.getString(actual_image_column_index);

		return img_path;
		
	}
public static String getFileName(String path){
	String fileName= path.substring(path.lastIndexOf("/") + 1, path.length());
	return fileName;
}
/*public  static String getCacheDataPath(FileType type){
	String path=null;
	switch(type){
	case IMG:
		path=GlobalParams.applicationContext.getExternalFilesDir(ConstantValues.CACHEDIR).getAbsolutePath()+"/"+AotoBangApp.mPreLoad.getId()+"/"+"image/";
		break; 
	case AUDIO:
		path=GlobalParams.applicationContext.getExternalFilesDir(ConstantValues.CACHEDIR).getAbsolutePath()+"/"+AotoBangApp.mPreLoad.getId()+"/"+"audio/";
		break; 
	case VIDEO:
		path=GlobalParams.applicationContext.getExternalFilesDir(ConstantValues.CACHEDIR).getAbsolutePath()+"/"+AotoBangApp.mPreLoad.getId()+"/"+"video/";
		break; 
	
	}
	LogUtil.info(PathUtil.class, path);
	File file=new File(path);
	if(!file.exists())
		 file.mkdir();
	return path;
	
}
 public static enum FileType {
    	IMG,
    	VIDEO,
    	AUDIO;
    	
    }*/
}
