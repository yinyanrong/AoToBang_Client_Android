package com.aotobang.net;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.aotobang.app.R;
import com.aotobang.app.applaction.AotoBangApp;
import com.aotobang.app.entity.AliPay;
import com.aotobang.app.entity.ChatComMsgEntity;
import com.aotobang.app.entity.FriendListInfo;
import com.aotobang.app.entity.Interact;
import com.aotobang.app.entity.LikeEntity;
import com.aotobang.app.entity.PreLoginData;
import com.aotobang.app.entity.RandomFriendInfo;
import com.aotobang.app.entity.UnionPay;
import com.aotobang.app.entity.UserDetailInfo;
import com.aotobang.app.entity.UserInfo;
import com.aotobang.app.entity.Version;
import com.aotobang.exception.AotoParseException;
import com.aotobang.net.entity.AotoNetEvent;
import com.aotobang.net.entity.AotoNotifiEvent;
import com.aotobang.net.entity.AotoResponse;
import com.aotobang.utils.InterfaceUtil;
/**
 * 解析工厂
 * @author hhj
 *
 */
public class ParseDataFactory {
	private static Context ctx=AotoBangApp.mPreLoad.appContext;
	public static AotoNotifiEvent parseNotifiResponse(AotoPacket  response){
		AotoNotifiEvent event=new AotoNotifiEvent();
		JSONObject obj;
		switch(response.getPacketId()){
		case AbstractPacket.Message_Type_NTF|InterfaceIds.CHART:
			try {
				obj = new JSONObject(response.getContent());
				event.setType(AotoNotifiEvent.Type.CHAT);
				event.setFrom(obj.getString("from"));
				event.setTo(obj.getString("dispath_uuid"));
				event.setData(obj.get("content"));
				event.setChat_type(ChatComMsgEntity.TYPE_TEXT);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		case AbstractPacket.Message_Type_NTF|InterfaceIds.UPLOAD_FILE_COMPLETE:
			try {
				obj = new JSONObject(response.getContent());
				int type=obj.getInt("type");
				event.setType(AotoNotifiEvent.Type.CHAT);
				event.setFrom(obj.getString("from"));
				event.setTo(obj.getString("dispath_uuid"));
				event.setData(obj.get("url"));
				if(obj.has("vTime"))
					event.setVoice_time(obj.getInt("vTime"));
				switch(type){
				case 0:
					event.setChat_type(ChatComMsgEntity.TYPE_IMG);
					break;
				case 1:
					event.setChat_type(ChatComMsgEntity.TYPE_VOICE);
					break;
				case 2:
					event.setChat_type(ChatComMsgEntity.TYPE_VIDEO);
					break;
				case 3:
					event.setChat_type(ChatComMsgEntity.TYPE_FILE);
					break;
				
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		case AbstractPacket.Message_Type_NTF|InterfaceIds.MACTH_FRIEND_LIKE:
			event.setType(AotoNotifiEvent.Type.MATCH_LIKE);
			
			LikeEntity entity=new LikeEntity();
			try {
				obj=new JSONObject(response.getContent());
				entity.setAvatar(obj.getString("icon"));
				entity.setFrom(obj.getString("from"));
				entity.setNick(obj.getString("nick"));
				entity.setTime(obj.getString("time"));
				entity.setUserName(obj.getString("userName"));
				event.setTo(obj.getString("dispath_uuid"));
				entity.setAvatar(obj.getString("icon"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		
			event.setLike(entity);
			
			break;
		case AbstractPacket.Message_Type_NTF|InterfaceIds.INTERACT_APPLY:
			event.setType(AotoNotifiEvent.Type.Interact_Apply);
			Interact apply=new Interact();
			try {
				obj=new JSONObject(response.getContent());
				apply.setType(obj.getInt("type"));
				apply.setData(obj.getString("content"));
				apply.setHaveDevice(obj.getInt("haveDevice"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			event.setInteract(apply);
			break;
		case AbstractPacket.Message_Type_NTF|InterfaceIds.INTERACTION_BLUETOOTH:
			event.setType(AotoNotifiEvent.Type.INTERACT);
			Interact in=new Interact();
			try {
				obj=new JSONObject(response.getContent());
				in.setType(obj.getInt("type"));
				in.setData(obj.getString("content"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			event.setInteract(in);
			break;
		case AbstractPacket.Message_Type_NTF|InterfaceIds.LOGIN:
			event.setType(AotoNotifiEvent.Type.REMOTE_LOGIN);
			event.setData(response.getContent());
		break;
		
		}
		
		
		
		
		
		
		
		
		return event;
	}
	
	public static AotoNetEvent parseResponse(AotoResponse response)throws AotoParseException{
		AotoNetEvent event=new AotoNetEvent();
		event.setResponseId(response.getResponseId());
		
		int errorCode=-1;
		JSONObject obj;
		String body=null;
		try {
			obj = new JSONObject(response.getData());
			errorCode=obj.getInt("code");
			if(obj.has("body"))
			body=obj.getString("body");
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		event.setErrorCode(errorCode);
		if(errorCode!=0){
			event.setData(updateErrorMessage(errorCode));
		}else{
			event.setData(updateDataMessage(response.getResponseId(),body));
		}
		
		
		
		
		return event;
	}
	
	private static Object updateDataMessage(int responseId,String body)throws AotoParseException {
		switch(responseId){
		case AbstractPacket.Message_Type_ACK|InterfaceIds.REGISTER:
		case AbstractPacket.Message_Type_ACK|InterfaceIds.LOGIN_OUT:
		case AbstractPacket.Message_Type_ACK|InterfaceIds.CHANGE_PWD:
		case AbstractPacket.Message_Type_ACK|InterfaceIds.VERIFY_CODE:
		case AbstractPacket.Message_Type_ACK|InterfaceIds.CHANGE_PHONE:
		case AbstractPacket.Message_Type_ACK|InterfaceIds.CHANGE_USER_INFO:
		case AbstractPacket.Message_Type_ACK|InterfaceIds.ADD_FRIENT_REQUEST:
		case AbstractPacket.Message_Type_ACK|InterfaceIds.CHART:
		case AbstractPacket.Message_Type_ACK|InterfaceIds.DELETE_FRIEND:
		case AbstractPacket.Message_Type_ACK|InterfaceIds.UPLOAD_LOCATION:
		case AbstractPacket.Message_Type_ACK|InterfaceIds.UPLOAD_FILE_COMPLETE:
		case AbstractPacket.Message_Type_ACK|InterfaceIds.MACTH_FRIEND_LIKE:
			return true;
		case AbstractPacket.Message_Type_ACK|InterfaceIds.PRE_LOGIN:
			if(body!=null&&!body.equals("null"))
			{
				
				PreLoginData data=JSON.parseObject(body, PreLoginData.class);
				return data;
			}else{
				throw new AotoParseException("InterfaceIds.PRE_LOGIN"+InterfaceUtil.getInterfaceName(responseId)+":无数据体");
				
			}
		case AbstractPacket.Message_Type_ACK|InterfaceIds.PHONE_REGIST:
			if(body!=null&&!body.equals("null"))
			{
				
				PreLoginData data=JSON.parseObject(body, PreLoginData.class);
				return data;
			}else{
				throw new AotoParseException("InterfaceIds.PRE_LOGIN"+InterfaceUtil.getInterfaceName(responseId)+":无数据体");
				
			}
		case AbstractPacket.Message_Type_ACK|InterfaceIds.LOGIN:
			if(body!=null&&!body.equals("null"))
			{
				UserInfo info=JSON.parseObject(body, UserInfo.class);
				return info;
			}else{
				throw new AotoParseException("InterfaceIds.LOGIN"+InterfaceUtil.getInterfaceName(responseId)+":无数据体");
				
			}
		case AbstractPacket.Message_Type_ACK|InterfaceIds.VERSION:
			if(body!=null&&!body.equals("null"))
			{
				Version v=JSON.parseObject(body, Version.class);
				return v;
			}else{
				throw new AotoParseException("InterfaceIds.VERSION"+InterfaceUtil.getInterfaceName(responseId)+":无数据体");
				
			}
		case AbstractPacket.Message_Type_ACK|InterfaceIds.UNIONPAY_RECHARGE:
			if(body!=null&&!body.equals("null"))
			{
				UnionPay upay=JSON.parseObject(body, UnionPay.class);
				return upay;
			}else{
				throw new AotoParseException("InterfaceIds.UNIONPAY_RECHARGE"+InterfaceUtil.getInterfaceName(responseId)+":无数据体");
				
			}
		case AbstractPacket.Message_Type_ACK|InterfaceIds.ALIPAY_RECHARGE:
			if(body!=null&&!body.equals("null"))
			{
				AliPay apay=JSON.parseObject(body, AliPay.class);
				return apay;
			}else{
				throw new AotoParseException("InterfaceIds.ALIPAY_RECHARGE"+InterfaceUtil.getInterfaceName(responseId)+":无数据体");
				
			}
		case AbstractPacket.Message_Type_ACK|InterfaceIds.GET_USER_INFO:
			if(body!=null&&!body.equals("null"))
			{
				UserDetailInfo info=JSON.parseObject(body, UserDetailInfo.class);
				return info;
			}else{
				throw new AotoParseException("InterfaceIds.GET_USER_INFO"+InterfaceUtil.getInterfaceName(responseId)+":无数据体");
				
			}
		case AbstractPacket.Message_Type_ACK|InterfaceIds.GET_FRIENT_LIST:
			if(body!=null&&!body.equals("null"))
			{
				List<FriendListInfo> infos=JSON.parseArray(body, FriendListInfo.class);
				return infos;
			}else{
				throw new AotoParseException("InterfaceIds.GET_FRIENT_LIST"+InterfaceUtil.getInterfaceName(responseId)+":无数据体");
				
			}
		case AbstractPacket.Message_Type_ACK|InterfaceIds.AGREE_FRIENT_REQUEST:
			if(body!=null&&!body.equals("null"))
			{
				JSONObject obj;
				boolean isAdd=true;
				try {
					obj = new JSONObject(body);
					 isAdd=obj.getBoolean("isAdd");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return isAdd;
			}else{
				throw new AotoParseException("InterfaceIds.AGREE_FRIENT_REQUEST"+InterfaceUtil.getInterfaceName(responseId)+":无数据体");
				
			}
			//随机获取在线用户
		case AbstractPacket.Message_Type_ACK|InterfaceIds.RANDOM_FRIEND:
			if(body!=null&&!body.equals("null"))
			{
				List<RandomFriendInfo> list=null;
				JSONObject obj;
				try {
					obj = new JSONObject(body);
					 String infos=obj.getString("profile");
					 list=JSON.parseArray(infos, RandomFriendInfo.class);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return list;
			}else{
				return null;
				//throw new AotoParseException("InterfaceIds.RANDOM_FRIEND"+InterfaceUtil.getInterfaceName(responseId)+":无数据体");
				
			}
		case AbstractPacket.Message_Type_ACK|InterfaceIds.UPLOAD_FILE:
			if(body!=null&&!body.equals("null"))
			{
				JSONObject obj;
				boolean isAdd=true;
				try {
					obj = new JSONObject(body);
					 isAdd=obj.getBoolean("isAdd");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return isAdd;
			}else{
				throw new AotoParseException("InterfaceIds.UPLOAD_FILE"+InterfaceUtil.getInterfaceName(responseId)+":无数据体");
				
			}
			case AbstractPacket.Message_Type_ACK|InterfaceIds.PHOTO_INFO:
				if(body!=null&&!body.equals("null"))
				{
					String array=null;
					try {
						JSONObject	 obj=new JSONObject(body);
						 array=obj.getString("photoInfo");
					} catch (JSONException e) {
						e.printStackTrace();
					}
				List<String>  photos=JSON.parseArray(array,String.class);

					return photos;
				}else{
					throw new AotoParseException("InterfaceIds.UPLOAD_FILE"+InterfaceUtil.getInterfaceName(responseId)+":无数据体");

				}
		
		
		}
		
		
		
		
		
		
		
		return null;
		
	}

	public static String updateErrorMessage(int errorCode){
		switch(errorCode){
		case 1:
			return ctx.getString(R.string.password_error);
		case 2:
			return ctx.getString(R.string.account_lock);
		case 3:
			return ctx.getString(R.string.other_error);
		case 4:
			return  ctx.getString(R.string.verification_code);
		case 5:
			return ctx.getString(R.string.token_error);
		case 6:
			return ctx.getString(R.string.account_inexistence);
		case 7:
			return ctx.getString(R.string.account_existed);
		case 8:
			return ctx.getString(R.string.parameter_error1);
		case 9:
			return ctx.getString(R.string.parameter_error2);
		case 10:
			return ctx.getString(R.string.Mac_error);
		case 11:
			return ctx.getString(R.string.phone_error);
		case 12:
			return ctx.getString(R.string.no_bind_bank);
		case 13:
			return ctx.getString(R.string.no_complete_info);
		case 14:
			return ctx.getString(R.string.real_name_error);
		case 15:
			return ctx.getString(R.string.server_error);
		case 16:
			return ctx.getString(R.string.server_busy);
		case 17:
			return ctx.getString(R.string.interface_error);
		case 18:
			return ctx.getString(R.string.msg_type_error);
			default:
				return "未知错误";
		}
	}
	
}
