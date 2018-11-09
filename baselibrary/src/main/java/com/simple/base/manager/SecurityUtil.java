package com.simple.base.manager;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityUtil {

	/**
	 * 得到一个MD5的加密码串
	 * @param plainText 普通的文本
	 * @return 经过MD5加密码的串.
	 * @throws NoSuchAlgorithmException
	 */
	public static String getMD5HashText(String plainText){
		return getHashText(plainText,"MD5");
	}


	/**
	 * 加密码文本
	 * @param plainText 普通通文本.
	 * @param algorithm 具体实现算法.
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	private static String getHashText(String plainText, String algorithm) {
		MessageDigest mdAlgorithm = null;
		try {
			mdAlgorithm = MessageDigest.getInstance(algorithm);
			mdAlgorithm.update(plainText.getBytes("GB2312"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
//		mdAlgorithm.update(plainText.getBytes());
		byte[] digest = mdAlgorithm.digest();
		StringBuffer hexString = new StringBuffer();
	for (int i = 0; i < digest.length; i++) {
			plainText = Integer.toHexString(0xFF & digest[i]);
			if (plainText.length() < 2) {
				plainText = "0" + plainText;
			}
			hexString.append(plainText);
		}
		return hexString.toString();
	}  
	
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',  

        'A', 'B', 'C', 'D', 'E', 'F' };  

public static String toHexString(byte[] b) {

    //String to  byte  

    StringBuilder sb = new StringBuilder(b.length * 2);

    for (int i = 0; i < b.length; i++) {    

        sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);    

        sb.append(HEX_DIGITS[b[i] & 0x0f]);    

    }    

    return sb.toString();    

}  

public   static String md5(String s) {

    try {  

        // Create MD5 Hash  

        MessageDigest digest = MessageDigest.getInstance("MD5");

        digest.update(s.getBytes());  

        byte messageDigest[] = digest.digest();  

                                  

        return toHexString(messageDigest);  

    } catch (NoSuchAlgorithmException e) {

        e.printStackTrace();  

    }  

                          

    return "";  

}  
}
