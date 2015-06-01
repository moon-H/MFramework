
package com.cssweb.framework.security;


/* 字符串 DESede(3DES) 加密 */
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.content.Context;
import android.text.TextUtils;

import com.cssweb.framework.utils.DeviceInfo;
import com.cssweb.framework.utils.HexConverter;


public class DES3 {

	private static final String Algorithm = "DESede"; // 定义 加密算法,可用

	// DES,DESede,Blowfish
	public static String encryptMode(Context context, byte[] keybyte, String src) {
		if (TextUtils.isEmpty(src)) {
			return null;
		}
		try {
			Security.addProvider(new com.sun.crypto.provider.SunJCE());
			// 生成密钥
			SecretKey deskey = new SecretKeySpec(getKeyByte(context), Algorithm);
			// 加密
			Cipher c1 = Cipher.getInstance(Algorithm);
			c1.init(Cipher.ENCRYPT_MODE, deskey);
			return HexConverter.bytesToHexString(c1.doFinal(src.getBytes()));
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

	public static String decryptMode(Context context, byte[] keybyte, String src) {
		try {
			Security.addProvider(new com.sun.crypto.provider.SunJCE());
			// 生成密钥
			SecretKey deskey = new SecretKeySpec(getKeyByte(context), Algorithm);
			// 解密
			Cipher c1 = Cipher.getInstance(Algorithm);
			c1.init(Cipher.DECRYPT_MODE, deskey);
			return new String(c1.doFinal(HexConverter.hexStringToBytes(src)));
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

	private static byte[] getKeyByte(Context context) {
		String keyOrgStr = DeviceInfo.getSignature(context);
		String keyStr = keyOrgStr.substring(0, 24);
		byte[] key = keyStr.getBytes();
		return key;
	}
}
