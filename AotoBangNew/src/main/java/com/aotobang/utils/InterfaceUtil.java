package com.aotobang.utils;

import java.util.HashMap;
import java.util.Map;

import com.aotobang.net.AbstractPacket;
import com.aotobang.net.InterfaceIds;




public class InterfaceUtil {
	public static InterfaceUtil instance;
	private static Map<Integer,String> maps;
	private InterfaceUtil(){
		maps=new HashMap<Integer, String>();
		maps.put(AbstractPacket.Message_Type_ACK|InterfaceIds.HEART_BEAT, "心跳");
		maps.put(AbstractPacket.Message_Type_ACK|InterfaceIds.REGISTER, "注册");
		maps.put(AbstractPacket.Message_Type_ACK|InterfaceIds.PRE_LOGIN, "前置登录");
		maps.put(AbstractPacket.Message_Type_ACK|InterfaceIds.LOGIN, "登录");
		maps.put(AbstractPacket.Message_Type_ACK|InterfaceIds.LOGIN_OUT, "登出");
		maps.put(AbstractPacket.Message_Type_ACK|InterfaceIds.VERSION, "版本检测");
		maps.put(AbstractPacket.Message_Type_ACK|InterfaceIds.CHANGE_PWD, "修改密码");
		maps.put(AbstractPacket.Message_Type_ACK|InterfaceIds.VERIFY_CODE, "获取验证码");
		maps.put(AbstractPacket.Message_Type_ACK|InterfaceIds.CHANGE_PHONE, "修改手机号");
		maps.put(AbstractPacket.Message_Type_ACK|InterfaceIds.UNIONPAY_RECHARGE, "银联充值");
		maps.put(AbstractPacket.Message_Type_ACK|InterfaceIds.ALIPAY_RECHARGE, "支付宝充值");
		maps.put(AbstractPacket.Message_Type_ACK|InterfaceIds.CHANGE_USER_INFO, "修改用户信息");
		maps.put(AbstractPacket.Message_Type_ACK|InterfaceIds.GET_USER_INFO, "获取用户信息");
		maps.put(AbstractPacket.Message_Type_ACK|InterfaceIds.GET_FRIENT_LIST, "获取好友列表");
		maps.put(AbstractPacket.Message_Type_ACK|InterfaceIds.ADD_FRIENT_REQUEST, "请求添加好友");
		maps.put(AbstractPacket.Message_Type_ACK|InterfaceIds.AGREE_FRIENT_REQUEST, "同意添加好友");
		maps.put(AbstractPacket.Message_Type_ACK|InterfaceIds.CHART, "聊天");
		maps.put(AbstractPacket.Message_Type_ACK|InterfaceIds.DELETE_FRIEND, "删除好友");
		maps.put(AbstractPacket.Message_Type_ACK|InterfaceIds.RANDOM_FRIEND, "随机获取在线好友");
		maps.put(AbstractPacket.Message_Type_ACK|InterfaceIds.UPLOAD_LOCATION, "上传位置信息");
		maps.put(AbstractPacket.Message_Type_ACK|InterfaceIds.UPLOAD_FILE, "上传文件");
		maps.put(AbstractPacket.Message_Type_ACK|InterfaceIds.UPLOAD_FILE_COMPLETE, "上传文件完成");
		maps.put(AbstractPacket.Message_Type_ACK|InterfaceIds.MACTH_FRIEND_LIKE, "like");
		maps.put(AbstractPacket.Message_Type_ACK|InterfaceIds.INTERACTION_BLUETOOTH, "蓝牙互动");
		maps.put(AbstractPacket.Message_Type_ACK|InterfaceIds.INTERACTION_BLUETOOTH, "申请互动");
		maps.put(AbstractPacket.Message_Type_ACK|InterfaceIds.PHONE_REGIST, "手机注册");
		maps.put(AbstractPacket.Message_Type_ACK|InterfaceIds.PHOTO_INFO, "相册信息");
		maps.put(AbstractPacket.Message_Type_NTF|InterfaceIds.CHART, "推送--聊天");
		maps.put(AbstractPacket.Message_Type_NTF|InterfaceIds.ADD_FRIENT_REQUEST, "推送--添加好友");
		maps.put(AbstractPacket.Message_Type_NTF|InterfaceIds.AGREE_FRIENT_REQUEST, "推送--同意添加好友");
		maps.put(AbstractPacket.Message_Type_NTF|InterfaceIds.UPLOAD_FILE_COMPLETE, "推送--上传完成");
		maps.put(AbstractPacket.Message_Type_NTF|InterfaceIds.MACTH_FRIEND_LIKE, "推送--like");
		maps.put(AbstractPacket.Message_Type_NTF|InterfaceIds.INTERACTION_BLUETOOTH, "推送--蓝牙互动");
		maps.put(AbstractPacket.Message_Type_NTF|InterfaceIds.INTERACT_APPLY, "申请--互动");
		maps.put(AbstractPacket.Message_Type_NTF|InterfaceIds.LOGIN, "推送--登陆");
		
	}
	
	public static InterfaceUtil getInstance() {
		if (instance == null) {
			synchronized (InterfaceUtil.class) {
				if (instance == null) {
					instance = new InterfaceUtil();
				}
			}
		}

		return instance;
	}
public static String getInterfaceName(int interfaceId){
	if(maps==null)
		getInstance();
	String  result=maps.get(interfaceId);
	if(result!=null)
	return result;
	else
		return "未知接口";
}

}
