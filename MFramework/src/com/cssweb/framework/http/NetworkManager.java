
package com.cssweb.framework.http;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


public class NetworkManager {

	private static final String TAG = "NetworkManager";
	private Context mContext;
	private ConnectivityManager mConnectivityManager;

	public NetworkManager(Context context) {
		this.mContext = context;
		mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	private boolean checkAvailableNetworkStatus() {
		initConnectivityManager();
		NetworkInfo mobile = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifi = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (mobile.isConnected() || wifi.isConnected()) {
			if (mobile.isConnected()) {
				Log.d(TAG, "Network connected type :: MOBILE");
			} else {
				Log.d(TAG, "Network connected type :: WIFI");
			}
			return true;
		}
		Log.e(TAG, "NO available network");
		return false;

	}

	public boolean isNetworkAvailable() {
		return checkAvailableNetworkStatus();
	}

	public int getCurrentNetType() {
		initConnectivityManager();
		return mConnectivityManager.getActiveNetworkInfo().getType();
	}

	private void initConnectivityManager() {
		if (mConnectivityManager == null) {
			mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		}
	}
}
