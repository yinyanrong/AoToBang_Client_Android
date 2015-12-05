package com.aotobang.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密工具  不可逆的
 * @author huanyu
 *
 */
public class MD5Utils {
	public static String encodeMD5(String pwd) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] result = digest.digest(pwd.getBytes());
			StringBuilder sb = new StringBuilder();
			for (byte b : result) {
				int number = b & 0xff;
				String hexstr = Integer.toHexString(number);
				if (hexstr.length() == 1) {
					sb.append("0");
				}
				sb.append(hexstr);
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}

	}
}
