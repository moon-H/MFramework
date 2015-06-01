
package com.cssweb.framework.download;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Random;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLSocketFactory;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.cssweb.framework.utils.MLog;
import com.cssweb.framework.utils.Utils;


public class Downloader {

	private static final String TAG = Downloader.class.getSimpleName();
	private static final String CACHE_DIR = "css_download";

	public static final int STATUS_DOWNLOADING = 100;
	public static final int STATUS_COMPLETE = 101;
	public static final int STATUS_CANCEL = 102;
	public static final int STATUS_ERROR = 103;

	private byte[] buffer = new byte[4 * 1024];
	private boolean externalCache;
	private int fileSize = 0;
	private Context context;
	private DownloadInfo downloadInfo;
	private Handler handler;
	private int state = 0;

	public Downloader(Context context, DownloadInfo downloadInfo, Handler handler) {
		super();
		this.context = context;
		this.downloadInfo = downloadInfo;
		this.handler = handler;
	}

	private class DownloadThread extends Thread {

		private URL url;
		private FileOutputStream output = null;
		private InputStream input = null;
		private Random random = new Random();
		private final int TICK = 10;

		@Override
		public void run() {
			try {
				input = getInputStreamFromUrl(downloadInfo);
				writeFile(System.currentTimeMillis() + "", input);
			} catch (IOException e) {
				MLog.e(TAG, "an error occured while writing file...", e);
				sendErrorMsg();
			} catch (KeyManagementException e) {
				e.printStackTrace();
				sendErrorMsg();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				sendErrorMsg();
			} finally {
				try {
					if (input != null) {
						input.close();
					}
					if (output != null) {
						output.close();
					}
				} catch (IOException e) {
					MLog.e(TAG, "an error occured while close stream...", e);
				}
			}
		}

		private void sendErrorMsg() {
			Message message = getMessage();
			message.what = STATUS_ERROR;
			message.obj = downloadInfo.getUrl();
			downloadInfo.setStatus(STATUS_ERROR);
			handler.sendMessage(message);
		}

		public InputStream getInputStreamFromUrl(DownloadInfo downloadInfo) throws IOException,
				NoSuchAlgorithmException, KeyManagementException {
			url = new URL(downloadInfo.getUrl());
			HttpURLConnection conn = null;
			if (url.getProtocol().equals("HTTPS")) {
				SSLContext sc;
				sc = SSLContext.getInstance("TLS");
				sc.init(null, new TrustManager[] {new MyTrustManager()}, new SecureRandom());
				HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
				HttpsURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());
				conn = (HttpsURLConnection) url.openConnection();
			} else {
				conn = (HttpURLConnection) url.openConnection();
			}
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(20000);
			conn.setUseCaches(false);
			InputStream inputStream = conn.getInputStream();
			fileSize = conn.getContentLength();
			Message message = getMessage();
			message.what = STATUS_DOWNLOADING;
			message.arg1 = fileSize;
			downloadInfo.setStatus(STATUS_DOWNLOADING);
			handler.sendMessage(message);
			return inputStream;
		}

		private void writeFile(String fileName, InputStream inputStream) throws IOException {
			File cashDir = Utils.getCacheDirectory(context, CACHE_DIR);
			if (!cashDir.exists()) {
				cashDir.mkdirs();
			}
			if (cashDir.getAbsolutePath().equals(Utils.getInternalCacheDir(context).getAbsolutePath())) {
				externalCache = false;
			}
			String newPath = cashDir.getAbsolutePath() + "/" + fileName;
			File newFile = new File(newPath);
			if (newFile.exists()) {
				newFile.delete();
			}
			output = new FileOutputStream(newFile);
			int completeSize = 0;
			int downloadCount = 0;
			int count = -1;
			while ((count = inputStream.read(buffer)) != -1) {
				if (state == STATUS_CANCEL) {
					Message message = getMessage();
					message.what = STATUS_CANCEL;
					message.obj = downloadInfo.getUrl();
					downloadInfo.setStatus(STATUS_CANCEL);
					handler.sendMessage(message);
					output.close();
					inputStream.close();
					return;
				}
				completeSize += count;
				output.write(buffer, 0, count);
				int tick = random.nextInt(TICK);
				if ((completeSize * 100 / fileSize) - tick > downloadCount || completeSize == fileSize
						|| completeSize == 0) {
					downloadCount += tick + random.nextInt(TICK);
					Message message = getMessage();
					message.what = STATUS_DOWNLOADING;
					message.arg1 = fileSize;
					message.arg2 = completeSize;
					message.obj = downloadInfo.getUrl();
					handler.sendMessage(message);
				}
			}
			output.flush();
			if (newFile.length() == fileSize) {
				Message message = getMessage();
				message.what = STATUS_COMPLETE;
				message.obj = downloadInfo.getUrl();
				downloadInfo.setInstallPath(newPath);
				downloadInfo.setStatus(STATUS_COMPLETE);
				message.arg1 = fileSize;
				message.arg2 = completeSize;
				handler.sendMessage(message);
				install(newFile);
			} else {
				Message message = getMessage();
				message.what = STATUS_ERROR;
				message.obj = downloadInfo.getUrl();
				downloadInfo.setStatus(STATUS_ERROR);
				handler.sendMessage(message);
			}
		}
	}

	private void install(File file) {
		String[] command = {"chmod", "777", file.getAbsolutePath()};
		if (!externalCache) {
			ProcessBuilder builder = new ProcessBuilder(command);
			try {
				builder.start();
			} catch (IOException e) {
				MLog.e(TAG, "an error occured while get R/W permision...", e);
			}
		}
		Uri uri = Uri.fromFile(file);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(uri, "application/vnd.android.package-archive");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	public void startDownload() {
		state = STATUS_DOWNLOADING;
		new DownloadThread().start();
	}

	public void stopDownload() {
		state = STATUS_CANCEL;
	}

	private Message getMessage() {
		return handler.obtainMessage();
	}

	public DownloadInfo getDownloadInfo() {
		return downloadInfo;
	}
	private class MyHostnameVerifier implements HostnameVerifier {

		@Override
		public boolean verify(String hostname, SSLSession session) {
			// TODO Auto-generated method stub
			return true;
		}
	}

	private class MyTrustManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			// TODO Auto-generated method stub

		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			// TODO Auto-generated method stub

		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
