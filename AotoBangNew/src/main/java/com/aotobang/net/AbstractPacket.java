package com.aotobang.net;

public abstract class AbstractPacket {
	public static final  int HEADER_LENGTH=12;
	public static final  int Message_Type_REQ=0x00100000;
	public static final  int Message_Type_ACK=0x00200000;//成功
	public static final  int Message_Type_NTF=0x00300000;
	public static final  int Message_Type_Base=0x00000000;
	/**
	 * 将内容转换成字节数组
	 * @return
	 */
	public abstract byte [] getBytes();
	
}
