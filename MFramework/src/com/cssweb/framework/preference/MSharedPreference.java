
package com.cssweb.framework.preference;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.cssweb.framework.security.DES3;


public class MSharedPreference {

	/**
	 * 删除所有缓存数据
	 */
	public static void clearCacheData(Context context) {
		removeCookie(context);
	}

	// -----------------------------------------------------------------

	/**
	 * Cookie信息
	 * */
	private static final String COOKIE = "cookie";

	/**
	 * 保存cookie
	 * */
	public static void saveCookie(Context context, String cookie) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(),
				Context.MODE_PRIVATE);
		String encrypt = DES3.encryptMode(context, null, cookie);
		sharedPreferences.edit().putString(COOKIE, encrypt).commit();
	}

	/**
	 * 获取cookie
	 * */
	public static String getCookie(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(),
				Context.MODE_PRIVATE);
		String cookie = sharedPreferences.getString(COOKIE, "");
		if (TextUtils.isEmpty(cookie)) {
			return "";
		}
		return DES3.decryptMode(context, null, cookie);
	}

	/**
	 * 删除cookie
	 * */
	public static void removeCookie(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(),
				Context.MODE_PRIVATE);
		sharedPreferences.edit().remove(COOKIE).commit();
	}

}
