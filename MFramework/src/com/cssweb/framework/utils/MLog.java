
package com.cssweb.framework.utils;


import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.content.Context;
import android.util.Log;


public class MLog {

	private final static boolean LOG_DEBUG = true;

	public static void d(String tag, String msg) {
		if (LOG_DEBUG) {
			Log.d(tag, msg);
		}
	}

	public static void d(String tag, String msg, Throwable tr) {
		if (LOG_DEBUG) {
			Log.d(tag, msg, tr);
		}
	}

	public static void i(String tag, String msg) {
		if (LOG_DEBUG) {
			Log.i(tag, msg);
		}
	}

	public static void i(String tag, String msg, Throwable tr) {
		if (LOG_DEBUG) {
			Log.i(tag, msg, tr);
		}
	}

	public static void w(String tag, String msg) {
		if (LOG_DEBUG) {
			Log.w(tag, msg);
		}
	}

	public static void w(String tag, Throwable tr) {
		if (LOG_DEBUG) {
			Log.w(tag, tr);
		}
	}

	public static void w(String tag, String msg, Throwable tr) {
		if (LOG_DEBUG) {
			Log.w(tag, msg, tr);
		}
	}

	public static void e(String tag, String msg) {
		if (LOG_DEBUG) {
			Log.e(tag, msg);
		}
	}

	public static void e(String tag, String msg, Throwable tr) {
		if (LOG_DEBUG) {
			Log.e(tag, msg, tr);
		}
	}

	public static void v(String tag, String msg) {
		if (LOG_DEBUG) {
			Log.v(tag, msg);
		}
	}

	public static void writeLog(Context context, String msg) {
		if (!LOG_DEBUG) {
			return;
		}
		File dirFile = Utils.getCacheDirectory(context, "闪客蜂" + "/Log");
		if (!dirFile.exists()) {
			dirFile.mkdir();
		}
		String filePath = dirFile.getAbsolutePath() + "/" + "css_log.txt";

		// File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		// if (!dir.exists()) {
		// dir.mkdir();
		// }
		//
		// String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/"
		// + "css_log.txt";

		// d("writeLog", "file path = " + filePath);
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(filePath, "rw");
			raf.seek(raf.length());
			raf.writeBytes("\n" + msg);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (raf != null) {
				try {
					raf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
