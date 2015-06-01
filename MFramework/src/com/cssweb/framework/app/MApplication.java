
package com.cssweb.framework.app;


import java.io.File;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.graphics.Bitmap;

import com.cssweb.framework.http.BaseGateway;
import com.cssweb.framework.http.NetworkManager;
import com.cssweb.framework.preference.MSharedPreference;
import com.cssweb.framework.utils.MLog;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.StorageUtils;


public class MApplication extends Application {

	private static final String TAG = "MApplication";
	private static BaseGateway sBaseGateway;
	private static NetworkManager sNetworkManager;
	public static final String PARENT_CACHE_DIR = "CssWeb";
	private static MApplication mInstance;
	private boolean needLockscreen;
	private File cacheDir;
	private static List<Activity> sActContainer = new LinkedList<Activity>();

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		MLog.d(TAG, "## onCreate");
		initImageLoader();

	}

	public void addActivity(Activity activity) {
		sActContainer.add(activity);
	}

	/**
	 * 程序异常时使用方法关闭应用
	 * */
	public void exit() {
		MSharedPreference.clearCacheData(mInstance);
		try {
			for (Activity activity : sActContainer) {
				if (activity != null)
					activity.finish();
			}
			sActContainer.clear();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	public boolean isNeedLockscreen() {
		return needLockscreen;
	}

	public void setNeedLockscreen(boolean needLockscreen) {
		this.needLockscreen = needLockscreen;
	}

	public static MApplication getInstance() {
		return mInstance;
	}

	public BaseGateway getBaseGateway(String url) {
		if (sBaseGateway == null) {
			sBaseGateway = new BaseGateway(this, url);
		}
		return sBaseGateway;
	}

	public NetworkManager getNetworkManager() {
		if (sNetworkManager == null) {
			sNetworkManager = new NetworkManager(this);
		}
		return sNetworkManager;
	}

	public void removeFromCache(String url) {
		DiskCacheUtils.removeFromCache(url, new UnlimitedDiscCache(cacheDir));
	}

	private void initImageLoader() {
		cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(), PARENT_CACHE_DIR + "/imageloader/Cache");
		MLog.d(TAG, "cacheDir = " + cacheDir);
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
				.resetViewBeforeLoading(true).bitmapConfig(Bitmap.Config.RGB_565)
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).resetViewBeforeLoading(true).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).threadPoolSize(3)
				.threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.memoryCacheSize(2 * 1024 * 1024)
				.diskCacheSize(50 * 1024 * 1024)
				// 50 Mb
				.tasksProcessingOrder(QueueProcessingType.LIFO).diskCacheFileCount(100)
				.diskCache(new UnlimitedDiscCache(cacheDir)).defaultDisplayImageOptions(defaultOptions)
				.imageDownloader(new BaseImageDownloader(this, 5 * 1000, 30 * 1000)).writeDebugLogs() // Remove for
																										// release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
}
