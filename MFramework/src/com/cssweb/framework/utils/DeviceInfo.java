
package com.cssweb.framework.utils;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.telephony.TelephonyManager;


public class DeviceInfo {

	/**
	 * Returns the ICCID.
	 * 
	 * @return
	 */
	public static String getICCID(Context context) {
		return ((TelephonyManager) context.getSystemService("phone")).getSimSerialNumber();
		// return "898600680113";

		/*
		 * String iccid_1 = "89860114831003220318"; return iccid_1;
		 */

		// String iccid_1 = "898600680113";
		// return iccid_1;
	}

	/**
	 * Returns the device model name.
	 * 
	 * @return
	 */
	public static String getDeviceModelName() {
		return android.os.Build.MODEL;
	}

	/**
	 * Returns the IMEI.
	 * 
	 * @return
	 */
	public static String getIMEI(Context context) {
		return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
	}

	/**
	 * Returns the IMSI
	 * 
	 * @return
	 */
	public static String getIMSI(Context context) {
		return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getSubscriberId();
	}

	/**
	 * Returns the versionCode.
	 * 
	 * @param Context
	 * @return AppVersionCode
	 */
	public static int getAppVersionCode(Context context) {
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return packInfo.versionCode;
	}

	/**
	 * Returns the versionName.
	 * 
	 * @param Context
	 * @return AppVersionName
	 */
	public static String getAppVersionName(Context context) {
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return packInfo.versionName;
	}

	public static String getSignature(Context context) {
		String signature = "";
		android.content.pm.Signature[] callerSigs = getSignature(context, context.getApplicationInfo().uid);
		try {
			signature = HexConverter.bytesToHexString(MessageDigest.getInstance("SHA1").digest(
					callerSigs[0].toByteArray()));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return signature;
	}

	private static android.content.pm.Signature[] getSignature(Context context, int uid) {
		PackageManager pm = context.getPackageManager();
		String[] packages = pm.getPackagesForUid(uid);
		if (packages != null) {
			{
				try {
					return pm.getPackageInfo(packages[0], PackageManager.GET_SIGNATURES).signatures;
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static int getScreenWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	public static int getScreenHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	public static float getDensity(Context context) {
		return context.getResources().getDisplayMetrics().density;
	}

	public static int getStatusBarHeight(Context context) {
		int statusHeight = 0;
		Class<?> localClass;
		try {
			localClass = Class.forName("com.android.internal.R$dimen");
			Object localObject = localClass.newInstance();
			int statusBarHeight = Integer
					.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
			statusHeight = context.getResources().getDimensionPixelSize(statusBarHeight);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusHeight;
	}

	public static boolean isNfcAvailable(Context context) {
		NfcManager manager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);
		NfcAdapter nfcAdapter = manager.getDefaultAdapter();
		return nfcAdapter == null ? false : true;
	}

	public static boolean isOMApiAvailable() {
		// org.simalliance.openmobileapi.SEService;
		// org.simalliance.openmobileapi.Session;
		// org.simalliance.openmobileapi.Channel;
		// org.simalliance.openmobileapi.Reader;
		Class<?> SEService = null;
		Class<?> session = null;
		Class<?> channel = null;
		Class<?> reader = null;

		try {
			SEService = Class.forName("org.simalliance.openmobileapi.SEService");
			session = Class.forName("org.simalliance.openmobileapi.Session");
			channel = Class.forName("org.simalliance.openmobileapi.Channel");
			reader = Class.forName("org.simalliance.openmobileapi.Reader");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		};
		if (SEService != null && session != null && channel != null && reader != null) {
			return true;
		}
		return false;
	}
}
