package com.aotobang.app.manager;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;

import com.alibaba.sdk.android.oss.OSSService;
import com.alibaba.sdk.android.oss.OSSServiceProvider;
import com.alibaba.sdk.android.oss.callback.SaveCallback;
import com.alibaba.sdk.android.oss.model.AccessControlList;
import com.alibaba.sdk.android.oss.model.ClientConfiguration;
import com.alibaba.sdk.android.oss.model.OSSException;
import com.alibaba.sdk.android.oss.model.TokenGenerator;
import com.alibaba.sdk.android.oss.storage.OSSBucket;
import com.alibaba.sdk.android.oss.storage.OSSData;
import com.alibaba.sdk.android.oss.storage.OSSFile;
import com.alibaba.sdk.android.oss.util.OSSToolKit;
import com.aotobang.app.applaction.AotoBangApp;
import com.aotobang.app.callback.ChatCallBack;
import com.aotobang.app.entity.ChatComMsgEntity;
import com.aotobang.net.AotoNetEngineProxy;
import com.aotobang.net.InterfaceIds;
import com.aotobang.net.callback.IDataCallBack;
import com.aotobang.net.entity.AotoRequest;
import com.aotobang.utils.ImageUtils;
import com.aotobang.utils.LogUtil;

public class OssManager {
	private static OssManager instance;

	static final String accessKey = "N21H7l02sHR6oQsD"; //没有考虑AK/SK的安全性
	static final String screctKey = "AlymCts3W3oxIuD4kF5aPeFyZyvFt2";

	public  static OSSService ossService= OSSServiceProvider.getService();
	private OssManager(){
		
		ossService.setApplicationContext(AotoBangApp.mPreLoad.appContext);
		 ossService.setGlobalDefaultTokenGenerator(new TokenGenerator(){

				@Override
				public String generateToken(String httpMethod, String md5, String type, String date, String ossHeaders, String resource) {
					
					   String content = httpMethod + "\n" + md5 + "\n" + type + "\n" + date
				                + "\n" + ossHeaders + resource;

				        return OSSToolKit.generateToken(accessKey, screctKey, content);
				}});
		 ossService.setGlobalDefaultACL(AccessControlList.PRIVATE);
		 ossService.setGlobalDefaultHostId("oss-cn-beijing.aliyuncs.com"); 
	     ossService.setCustomStandardTimeWithEpochSec(System.currentTimeMillis()/1000);
	     ClientConfiguration conf = new ClientConfiguration();
	     conf.setConnectTimeout(15 * 1000); // 设置建连超时时间，默认为30s
	     conf.setSocketTimeout(15 * 1000); // 设置socket超时时间，默认为30s
	     conf.setMaxConnections(50); // 设置全局最大并发连接数，默认为50个
	     ossService.setClientConfiguration(conf);
		
	}
	public static OssManager getInstance(){
		if(instance==null){
			synchronized (OssManager.class) {
				if(instance==null){
					instance=new OssManager();
					
				}
			}
			
		}
		return instance;
		
	}
	public OSSData uploadData(String uuid ,String dirType,String fileName,byte[] data){
		OSSBucket mBucket = ossService.getOssBucket("userfile-aotobang");
		OSSData ossData = ossService.getOssData(mBucket, uuid+"/"+dirType+"/"+fileName);
		ossData.setData(data, "img"); // 指定需要上传的数据和它的类型
		ossData.enableUploadCheckMd5sum(); // 开启上传MD5校验
		
		return ossData;
		
		
	}
	public OSSData uploadData(String uuid ,String dirType,String fileName,byte[] data,String bucketName){
		OSSBucket mBucket = ossService.getOssBucket(bucketName);
		OSSData ossData = ossService.getOssData(mBucket, uuid+"/"+dirType+"/"+fileName);
		ossData.setData(data, "img"); // 指定需要上传的数据和它的类型
		ossData.enableUploadCheckMd5sum(); // 开启上传MD5校验

		return ossData;


	}

	public  OSSFile uploadFile(String uuid ,String dirType,String fileName,String localPath){
		
		OSSBucket mBucket = ossService.getOssBucket("userfile-aotobang");
		
		OSSFile ossFile = ossService.getOssFile(mBucket, uuid + "/" + dirType + "/" + fileName);
		try {
			ossFile.setUploadFilePath(localPath, dirType);
			ossFile.enableUploadCheckMd5sum();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return ossFile;
	}
	public  OSSFile uploadFile(String uuid ,String dirType,String fileName,String localPath,String bucketName){

		OSSBucket mBucket = ossService.getOssBucket(bucketName);

		OSSFile ossFile = ossService.getOssFile(mBucket, uuid+"/"+dirType+"/"+fileName);
		try {
			ossFile.setUploadFilePath(localPath, dirType);
			ossFile.enableUploadCheckMd5sum();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return ossFile;
	}
public  OSSFile downloadFile(String netPath){
	OSSBucket mBucket = ossService.getOssBucket("userfile-aotobang");
	OSSFile ossFile = ossService.getOssFile(mBucket, netPath);
	return ossFile;
		
	}
	public  OSSFile downloadFile(String netPath,String bucketName){
		OSSBucket mBucket = ossService.getOssBucket(bucketName);
		OSSFile ossFile = ossService.getOssFile(mBucket, netPath);
		return ossFile;

	}


	public   void  uploadImg(String dirType,String userId,String fileName,Bitmap bitmap,final ChatCallBack callback,boolean compress){
	
	OSSBucket mBucket = ossService.getOssBucket("userfile-aotobang");
	OSSData ossData = ossService.getOssData(mBucket, userId + "/" + dirType + "/" + fileName);
	if(compress&&bitmap.getByteCount()>1024*100)
	ossData.setData( ImageUtils.bitmap2Bytes(bitmap,true), "img"); // 指定需要上传的数据和它的类型
	else
		ossData.setData(ImageUtils.bitmap2Bytes(bitmap,false), "img");
		ossData.enableUploadCheckMd5sum(); // 开启上传MD5校验
	ossData.uploadInBackground(new SaveCallback() {
	    @Override
	    public void onSuccess( String objectKey) {
	    	LogUtil.info(ChatManager.class, objectKey);
	    	callback.onSuccess(objectKey);
	    	callback.onSuccess();
	    }

	    @Override
	    public void onProgress(String objectKey, int byteCount, int totalSize) {
	    	callback.onProgress(byteCount, totalSize);
	    }

	    @Override
	    public void onFailure(String objectKey, OSSException ossException) {
	    	callback.onError(ChatManager.ERROR_OSS, ossException.getMessage());

	    }
	});

	
	
}

	public   void  uploadImg(String dirType,String userId,String fileName,Bitmap bitmap,final ChatCallBack callback,boolean compress,String bucketName){

		OSSBucket mBucket = ossService.getOssBucket(bucketName);
		OSSData ossData = ossService.getOssData(mBucket, userId+"/"+dirType+"/"+fileName);
		if(compress&&bitmap.getByteCount()>1024*100)
			ossData.setData( ImageUtils.bitmap2Bytes(bitmap,true), "img"); // 指定需要上传的数据和它的类型
		else
			ossData.setData(ImageUtils.bitmap2Bytes(bitmap,false), "img");
		ossData.enableUploadCheckMd5sum(); // 开启上传MD5校验
		ossData.uploadInBackground(new SaveCallback() {
			@Override
			public void onSuccess( String objectKey) {
				LogUtil.info(ChatManager.class, objectKey);
				callback.onSuccess();
				callback.onSuccess(objectKey);

			}

			@Override
			public void onProgress(String objectKey, int byteCount, int totalSize) {
				callback.onProgress(byteCount, totalSize);
			}

			@Override
			public void onFailure(String objectKey, OSSException ossException) {
				callback.onError(ChatManager.ERROR_OSS, ossException.getMessage());

			}
		});



	}
public OSSData downLoadData(String netPath){
	OSSBucket mBucket = ossService.getOssBucket("userfile-aotobang");
	OSSData ossData = ossService.getOssData(mBucket, netPath); // 构造OSSData实例
	return ossData;
	
}
	public OSSData downLoadData(String netPath,String bucketName){
		OSSBucket mBucket = ossService.getOssBucket(bucketName);
		OSSData ossData = ossService.getOssData(mBucket, netPath); // 构造OSSData实例
		return ossData;

	}
}
