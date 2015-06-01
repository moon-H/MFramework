
package com.cssweb.framework.utils;


import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


public class Utils {

	public static File getCacheDirectory(Context context, String parentCacheDir) {
		File appCacheDir = null;
		if ("mounted".equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)) {
			appCacheDir = new File(Environment.getExternalStorageDirectory(), parentCacheDir);
		}
		if (appCacheDir == null || !appCacheDir.exists() && !appCacheDir.mkdirs()) {
			appCacheDir = context.getCacheDir();
			// appCacheDir = context.getDir(fileName, Context.MODE_PRIVATE);
		}
		return appCacheDir;
	}

	public static File getInternalCacheDir(Context context) {
		return context.getCacheDir();

	}

	private static boolean hasExternalStoragePermission(Context context) {
		int perm = context.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE");
		return perm == 0;
	}

	/**
	 * px 转 dp
	 * */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * dp 转 PX
	 * 
	 * */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;

		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * px 转 sp
	 * 
	 * */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * sp 转 px
	 * 
	 * */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	/**
	 * 隐藏键盘
	 * */
	public static void hideSoftInput(Context context, View view) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public static void showSysSoftInput(EditText edit, boolean show) {
		Class<EditText> cls = EditText.class;
		try {
			Method setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
			setShowSoftInputOnFocus.setAccessible(true);
			setShowSoftInputOnFocus.invoke(edit, show);
			// setShowSoftInputOnFocus.invoke(ePeople, false);
		} catch (NoSuchMethodException e) {
			MLog.e("showSysSoftInput NoSuchMethodException ::", e.getMessage());
		} catch (IllegalArgumentException e) {
			MLog.e("showSysSoftInput IllegalArgumentException ::", e.getMessage());
		} catch (IllegalAccessException e) {
			MLog.e("showSysSoftInput IllegalAccessException ::", e.getMessage());
		} catch (InvocationTargetException e) {
			MLog.e("showSysSoftInput InvocationTargetException ::", e.getMessage());
		}
		try {
			Method setShowSoftInputOnFocus2 = cls.getMethod("setSoftInputShownOnFocus", boolean.class);
			setShowSoftInputOnFocus2.setAccessible(true);
			setShowSoftInputOnFocus2.invoke(edit, show);
		} catch (NoSuchMethodException e) {
			MLog.e("showSysSoftInput NoSuchMethodException :: ", e.getMessage());
		} catch (IllegalArgumentException e) {
			MLog.e("showSysSoftInput IllegalArgumentException :: ", e.getMessage());
		} catch (IllegalAccessException e) {
			MLog.e("showSysSoftInput IllegalAccessException :: ", e.getMessage());
		} catch (InvocationTargetException e) {
			MLog.e("showSysSoftInput InvocationTargetException ::", e.getMessage());
		}
	}

	/**
	 * String[0] is asset ID,String[1] is asset name
	 * */
	public static String[] parseQrCode(String str) throws Exception {
		String[] resultArray = new String[2];
		if (str.startsWith("cssweb") && !TextUtils.isEmpty(str)) {
			String[] array = str.split("\\|");
			resultArray[0] = array[1];
			resultArray[1] = array[2];
			return resultArray;
		} else {
			throw new Exception("Unkown QR CODE");
		}
	}

	public static Bitmap readBitmapFromRes(Context context, int resId) {
		return BitmapFactory.decodeResource(context.getResources(), resId);
	}

	public static String getDecimal2(double data) {
		DecimalFormat decimalFormat = new DecimalFormat("0.00");// 保持小数点下2位
		return decimalFormat.format(data);
	}

	public static boolean isNumberOrEnglish(CharSequence str) {
		Pattern p = Pattern.compile("^[A-Za-z0-9]+$");
		Matcher m = p.matcher(str);
		return m.matches();
	}

	public static String generatePassword(String inputString) {
		return encodeByMD5(inputString);
	}

	private static String encodeByMD5(String originString) {
		if (originString != null) {
			try {
				// 创建具有指定算法名称的信息摘要
				MessageDigest md = MessageDigest.getInstance("MD5");
				// 使用指定的字节数组对摘要进行最后更新，然后完成摘要计算
				byte[] results = md.digest(originString.getBytes());
				// 将得到的字节数组变成字符串返回
				String resultString = byteArrayToHexString(results);
				return resultString.toUpperCase(Locale.getDefault());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 转换字节数组为十六进制字符串
	 * 
	 * @param 字节数组
	 * @return 十六进制字符串
	 */
	private static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	/** 将一个字节转化成十六进制形式的字符串 */
	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}
	// 十六进制下数字到字符的映射数组
	private final static String[] hexDigits = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d",
			"e", "f"};

	public static boolean isBackgroundRunning(Context context) {
		String processName = context.getApplicationInfo().packageName;

		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);

		if (activityManager == null)
			return false;
		// get running application processes
		List<ActivityManager.RunningAppProcessInfo> processList = activityManager.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo process : processList) {
			if (process.processName.startsWith(processName)) {
				// boolean isBackground = process.importance !=
				// android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
				// && process.importance != android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;
				boolean isBackground = process.importance != android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
				boolean isLockedState = keyguardManager.inKeyguardRestrictedInputMode();
				if (isBackground || isLockedState)
					return true;
				else
					return false;
			}
		}
		return false;
	}

	public static boolean isEmail(String email) {
		if (null == email || "".equals(email))
			return false;
		Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");// 复杂匹配
		Matcher m = p.matcher(email);
		return m.matches();
	}

	public static boolean isMobileNO(String mobile) {
		if (null == mobile || "".equals(mobile))
			return false;
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobile);
		return m.matches();
	}

	public static String convertDate2Local(String strDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Date date;
		try {
			date = sdf.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
			return strDate;
		}

		SimpleDateFormat dateFormat;
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		return dateFormat.format(date);
	}

	public static boolean isOkResponse(String response) {
		if (TextUtils.isEmpty(response)) {
			return false;
		} else {
			if (response.endsWith("9000")) {
				return true;
			} else {
				return false;
			}
		}
	}
}
