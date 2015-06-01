
package com.cssweb.framework.http;


import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;

import android.content.Context;
import android.text.TextUtils;

import com.cssweb.framework.app.MApplication;
import com.cssweb.framework.http.model.Request;
import com.cssweb.framework.http.model.Response;
import com.cssweb.framework.preference.MSharedPreference;
import com.cssweb.framework.utils.MLog;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;


public class BaseGateway {

	private static final String TAG = "Gateway";
	private AsyncHttpClient mClient;
	private Gson mGson;
	private MApplication mApp;

	public BaseGateway(MApplication app, String url) {
		if (url.trim().startsWith("https")) {
			mClient = new AsyncHttpClient(true, 80, 443);
		} else {
			mClient = new AsyncHttpClient();
		}
		mApp = app;
		mClient.setTimeout(10000);
		mGson = new Gson();
	}

	public void addHeaders() {
		String headerStr = MSharedPreference.getCookie(mApp);
		if (!TextUtils.isEmpty(headerStr)) {
			mClient.addHeader("Cookie", headerStr.trim());
		}
	}

	public void cancelRequest(Context context, boolean mayInterruptIfRunning) {
		// mClient.cancelRequests(context, mayInterruptIfRunning);
		mClient.cancelAllRequests(true);
	}

	public void setMaxRetriesAndTimeout(int retries, int timeout) {
		mClient.setMaxRetriesAndTimeout(retries, timeout);
	};

	public void setTimeout(int timeout) {
		mClient.setTimeout(timeout);
	}

	private StringEntity converRequest(Request request) throws UnsupportedEncodingException {
		JsonElement localJsonElement = this.mGson.toJsonTree(request);
		JsonObject requstJsonObject = new JsonObject();
		requstJsonObject.add(request.getClass().getSimpleName(), localJsonElement);
		MLog.d(TAG, "sendRequest::request :: " + requstJsonObject.toString());
		return new StringEntity(requstJsonObject.toString(), "UTF-8");
	}

	public void sendRequset(MApplication context, String url, Request request,
			final ResponseMappingHandler<? extends Response> resHandler) {
		if (!context.getNetworkManager().isNetworkAvailable()) {
			MLog.e(TAG, "sendRequset:: network state is not available");
			resHandler.handleNoNetwork();
			return;
		}
		StringEntity strEntity;
		try {
			MLog.d(TAG, "sendRequest:: URL :: " + url);
			strEntity = converRequest(request);
			addHeaders();
			mClient.post(context, url, strEntity, "application/json; charset=UTF-8", new AsyncHttpResponseHandler() {

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					MLog.d(TAG, "sendRequest::onFailuer : " + " HttpCode :: " + statusCode);
					if (responseBody != null) {
						MLog.d(TAG, "sendRequest::onFailuer responseBody :: " + new String(responseBody));
					}
					if (error != null) {
						MLog.d(TAG, "sendRequest::onFailuer error :: " + error.getMessage());
					}
					resHandler.onFailure(statusCode, headers, responseBody, error);
				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					MLog.d(TAG, "sendRequest::onSuccess:: HttpCode :: " + statusCode);
					if (responseBody != null) {
						MLog.d(TAG, "sendRequest::onSuccess:: Response :: " + new String(responseBody));
					}
					resHandler.onSuccess(statusCode, headers, new String(responseBody));
				}
			});
		} catch (UnsupportedEncodingException error) {
			error.printStackTrace();
			resHandler.onFailure(9876, null, null, error);
		}
	}
}
