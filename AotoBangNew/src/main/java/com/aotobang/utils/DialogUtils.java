package com.aotobang.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogUtils {
	
	private static ProgressDialog progressDialog;
	private static AlertDialog infoDialog;
	public static void showProgressDialog(Context ctx){
		//必须每次建立新对象
			 progressDialog = new ProgressDialog(ctx);  
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  
			progressDialog.setCancelable(true); 
			progressDialog.setMessage("加载中...");  
		if(progressDialog!=null&&!progressDialog.isShowing())
		progressDialog.show();
		
	}
	
	public static void showProgressDialog(Context ctx,String msg){
		
			 progressDialog = new ProgressDialog(ctx);  
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  
			progressDialog.setCancelable(true); 
			progressDialog.setMessage(msg);

	
	if(progressDialog!=null&&!progressDialog.isShowing())
	progressDialog.show();
	
}
	public static void showProgressDialog(Context ctx,String msg,boolean cancelable){
		
		 progressDialog = new ProgressDialog(ctx);  
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  
		progressDialog.setCancelable(cancelable); 
		progressDialog.setMessage(msg);  

if(progressDialog!=null&&!progressDialog.isShowing())
progressDialog.show();

}
	
	public static void showInfoDialog(Context ctx,String msg,DialogInterface.OnClickListener positive,DialogInterface.OnClickListener negative){
		
		  AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		   builder.setTitle("提示")    //标题
           .setCancelable(true).setMessage(msg).setPositiveButton("确定", positive).setNegativeButton("取消", negative) ;   //不响应back按钮
		   infoDialog=builder.create();
		   if(infoDialog!=null&&!infoDialog.isShowing())
			   infoDialog.show();
		   
		   
}
	public static void showInfoDialog(Context ctx,String msg,DialogInterface.OnClickListener positive,DialogInterface.OnClickListener negative,String positiveStr,String negativeStr){
		
		  AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		   builder.setTitle("提示")    //标题
         .setCancelable(true).setMessage(msg).setPositiveButton(positiveStr, positive).setNegativeButton(negativeStr, negative) ;   //不响应back按钮
		   infoDialog=builder.create();
		   if(infoDialog!=null&&!infoDialog.isShowing())
			   infoDialog.show();
		   
		   
}
	public static void closeInfoDialog(){
		if(infoDialog!=null&&infoDialog.isShowing())
			infoDialog.dismiss();
		
	}
	
	public static void closeProgressDialog(){
		if(progressDialog!=null&&progressDialog.isShowing())
			progressDialog.dismiss();
		
	}
}
