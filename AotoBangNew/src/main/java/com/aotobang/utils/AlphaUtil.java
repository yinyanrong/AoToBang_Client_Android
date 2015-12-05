package com.aotobang.utils;

import java.util.regex.Pattern;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class AlphaUtil {
	/**
	 * 处理首字母
	 * 
	 * @return 字符串的首字母，不是A~Z范围的返回#
	 */
	public static String formatAlpha(String str) {
		if (str == null) {
			return "#";
		}
		if (str.trim().length() == 0) {
			return "#";
		}

		char c = str.trim().substring(0, 1).charAt(0);
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase();
		} else {
			return "#";
		}
	}
	/**
	 * 去除电话号码 前缀和-
	 * 
	 * @return 处理后的电话号码
	 */
	public static String formatNumber(String number) {
		String returnStr;
		if (number == null || number.length() == 0) {
			return null;
		}
		returnStr = number.replaceAll("-", "");
		if (returnStr.startsWith("+86")) {
			return returnStr.substring(3, number.length());
		} else if (returnStr.startsWith("17951")) {
			return returnStr.substring(5, number.length());
		} else if (returnStr.startsWith("12593")) {
			return returnStr.substring(5, number.length());
		} else if (returnStr.startsWith("17911")) {
			return returnStr.substring(5, number.length());
		} else if (returnStr.startsWith("17900")) {
			return returnStr.substring(5, number.length());
		} else {
			return returnStr;
		}
	}
	public static String getAlpha(char c){
		LogUtil.info(AlphaUtil.class, String.valueOf(c));
		HanyuPinyinOutputFormat format= new HanyuPinyinOutputFormat();
		 
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		 
		format.setVCharType(HanyuPinyinVCharType.WITH_V);
		format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		String[] pinyinArray = null;
		try
		{
		     pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, format);
		} 
		catch(BadHanyuPinyinOutputFormatCombination e)
		{
		     e.printStackTrace();
		}
		/*char min='Z';
		for(int i = 0; i < pinyinArray.length; ++i)
		{
			if(Character.getNumericValue(pinyinArray[i].charAt(0))<Character.getNumericValue(min))
				min=pinyinArray[i].charAt(0);
		}*/
	
		return String.valueOf(pinyinArray[0].charAt(0));
	}
	public static String getNextAlpha(char c,int count){
		
		HanyuPinyinOutputFormat format= new HanyuPinyinOutputFormat();
		 
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		 
		format.setVCharType(HanyuPinyinVCharType.WITH_V);
		format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		String[] pinyinArray = null;
		try
		{
		     pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, format);
		} 
		catch(BadHanyuPinyinOutputFormatCombination e)
		{
		     e.printStackTrace();
		}
		if(count>=pinyinArray.length){
			return null;
		}
		return pinyinArray[count];
	}
public	enum CharType{   
	    DELIMITER, //非字母截止字符，例如，．）（　等等　（ 包含U0000-U0080）   
	    NUM, //2字节数字１２３４   
	    LETTER, //gb2312中的，例如:ＡＢＣ，2字节字符同时包含 1字节能表示的 basic latin and latin-1 OTHER,// 其他字符   
	    OTHER,
	    CHINESE;//中文字  
	}  
	/** 
	* 判断输入char类型变量的字符类型 
	* @param c char类型变量 
	* @return CharType 字符类型 
	*/  
	public static CharType checkType(char c){   
	        CharType ct =null;  
	  
	        //中文，编码区间0x4e00-0x9fbb                 
	        if ((c >=   
	        0x4e00)&&(c <= 0x9fbb)){ ct = CharType.CHINESE; }  
	  
	        //Halfwidth and Fullwidth Forms， 编码区间0xff00-0xffef   
	        else if ( (c >= 0xff00) &&(c <= 0xffef)) { //        2字节英文字   
	        if ((( c >= 0xff21 )&&( c <= 0xff3a)) || (( c >= 0xff41 )&&( c   
	        <= 0xff5a))){ ct = CharType.LETTER; }  
	  
	                //2字节数字                        
	        else if (( c >=0xff10 )&&( c <= 0xff19)  ){ ct = CharType.NUM; }  
	  
	                //其他字符，可以认为是标点符号   
	                else ct = CharType.DELIMITER; }  
	  
	        //basic latin，编码区间 0000-007f                 
	        else if ( (c  >= 0x0021) &&(c <= 0x007e)){ //1字节数字 
	       if (( c >= 0x0030 )&&(   
	        c <= 0x0039)  ){ ct = CharType.NUM; } //1字节字符 
	        else if ((( c   
	        >= 0x0041 )&&( c <= 0x005a)) || (( c >= 0x0061 )&&( c <=   
	        0x007a)))        { ct = CharType.LETTER; }   
	        //其他字符，可以认为是标点符号 
	        else ct = CharType.DELIMITER; }  
	  
	        //latin-1，编码区间0080-00ff                 
	        else if ( (c >=   
	        0x00a1) &&(c <= 0x00ff)){ if (( c >= 0x00c0 )&&( c <= 0x00ff)){   
	        ct = CharType.LETTER; } else ct = CharType.DELIMITER; } else ct   
	        = CharType.OTHER;  
	  
	        return ct;  
	}  

}
