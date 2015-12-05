package com.aotobang.net;

import android.content.Context;
import android.widget.Toast;

import com.aotobang.app.R;
import com.aotobang.net.callback.IDataCallBack;
import com.aotobang.net.callback.LongConnCallBack;
import com.aotobang.net.entity.AotoRequest;
import com.aotobang.utils.NetUtil;

public class AotoNetEngineProxy {
	private static AotoNetEngineProxy instance;
	private Context ctx;
	private AotoNetEngineProxy(Context ctx){
		this.ctx=ctx;
		
	}
	public static AotoNetEngineProxy getInstance(Context ctx){
		if(instance==null){
		synchronized (AotoNetEngineProxy.class) {
			if(instance==null){
				instance=new AotoNetEngineProxy(ctx);
			}
		}
		}
		return instance;
		
	}
	public  void  openLongConnect(String host,int port,LongConnCallBack callBack){
		if(!NetUtil.checkNetWork(ctx)){
			Toast.makeText(ctx, R.string.the_current_network, 0).show();
			return;
		}
		AotoNetEngine.getInstance().openLongConnect(host, port,callBack);
	}
	public void sendReqeust(AotoRequest request){
		if(!NetUtil.checkNetWork(ctx)){
			request.getCallBack().handleDataResultOnError(AotoPacket.Message_Type_ACK|request.getRequestId(), AotoNetEngine.NO_NET, ctx.getString(R.string.the_current_network));
			return;
		}
		
		AotoNetEngine.getInstance().sendRequest(request);
		
		
	}
	public void getNetDataShort(AotoRequest request,String host,int port){
		if(!NetUtil.checkNetWork(ctx)){
			request.getCallBack().handleDataResultOnError(AotoPacket.Message_Type_ACK|request.getRequestId(), AotoNetEngine.NO_NET, ctx.getString(R.string.the_current_network));
			return;
		}
		AotoNetEngine.getInstance().getNetDataShort(request, host, port);
		
		
	}
	
	public void closeLongConnect(){
		AotoNetEngine.getInstance().closeLongConnect();
		instance=null;
		
	}
	
	
}
