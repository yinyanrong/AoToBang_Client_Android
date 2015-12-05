package com.aotobang.utils;

public class Commonutils {
	public static String ToDBC(String input) {          
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {              
        if (c[i] == 12288) {                 
        c[i] = (char) 32;                  
        continue;
         }
         if (c[i] > 65280 && c[i] < 65375)
            c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }  
	/**  
	    * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序，和和intToBytes（）配套使用 
	    *   
	    * @param src  
	    *            byte数组  
	    * @param offset  
	    *            从数组的第offset位开始  
	    * @return int数值  
	    */    
	public static int bytesToInt(byte[] src, int offset) {  
	    int value;    
	    value = (int) ((src[offset] & 0xFF)   
	            | ((src[offset+1] & 0xFF)<<8)   
	            | ((src[offset+2] & 0xFF)<<16)   
	            | ((src[offset+3] & 0xFF)<<24));  
	    return value;  
	}  
	  
	 /**  
	    * byte数组中取int数值，本方法适用于(低位在后，高位在前)的顺序。和intToBytes2（）配套使用 
	    */  
	public static int bytesToInt2(byte[] src, int offset) {  
	    int value;    
	    value = (int) ( ((src[offset] & 0xFF)<<24)  
	            |((src[offset+1] & 0xFF)<<16)  
	            |((src[offset+2] & 0xFF)<<8)  
	            |(src[offset+3] & 0xFF));  
	    return value;  
	}  
	/**
	 * 16进制字符串转换成字节数组 
	 * @param hex
	 * @return
	 */
	public static byte[] hexStringToByte(String hex) {    
		int len = (hex.length() / 2);    
		byte[] result = new byte[len];    
		char[] achar = hex.toCharArray();    
		for (int i = 0; i < len; i++) { 
			int pos = i * 2;      
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
			
		
		}     return result; 
		
	}
	private static byte toByte(char c) {   
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b; 
		
	} 
	 /** 
     * byte数组转成16进制字符串数组
     * @param src 
     * @return 
     */  
    public static String[] bytesToHexStrings(byte[] src){       
           if (src == null || src.length <= 0) {       
               return null;       
           }
           String[] str = new String[src.length];
           
           for (int i = 0; i < src.length; i++) {       
               int v = src[i] & 0xFF;       
               String hv = Integer.toHexString(v);       
               if (hv.length() < 2) {       
                   str[i] = "0";       
               }       
               str[i] = hv;        
           }       
           return str;       
       }
    /** 
     * 字节数组转成16进制字符串
     * @param src 
     * @return 
     */  
    public static String bytesToHexString(byte[] src){       
           StringBuilder stringBuilder = new StringBuilder();       
           if (src == null || src.length <= 0) {       
               return null;       
           }       
           for (int i = 0; i < src.length; i++) {       
               int v = src[i] & 0xFF;       
               String hv = Integer.toHexString(v);       
               if (hv.length() < 2) {       
                   stringBuilder.append(0);       
               }       
               stringBuilder.append(hv);       
           }       
           return stringBuilder.toString();       
       }
    /** 
     * 合并字节数组
     * @param  byte[] bytes1 
     * @param byte[] bytes2 
     */  
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){  
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];  
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);  
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);  
        return byte_3;  
    }  
    
    
   /** 
    * 合并字节数组
    * @param  byte[] bytes1 
    * @param byte[] bytes2 
    * @return byte[] 
    */  
   public   byte[]   combineTowTytes(byte[] bytes1,byte[] bytes2){      
       byte[] bytes3 = new byte[bytes1.length+bytes2.length];  
         System.arraycopy(bytes1,0,bytes3,0,bytes1.length);  
         System.arraycopy(bytes2,0,bytes3,bytes1.length,bytes2.length);  
         return bytes3 ;  
   }  
}
