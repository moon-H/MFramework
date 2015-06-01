
package com.cssweb.framework.http;


import org.apache.http.Header;

import android.util.Log;

import com.cssweb.framework.http.model.Response;
import com.cssweb.framework.http.model.Result;
import com.cssweb.framework.utils.MLog;
import com.google.gson.Gson;
import com.google.gson.JsonElement;


public abstract class ResponseMappingHandler<Type extends Response> {

	private static final String TAG = "ResponseMappingHandler";
	private static final Gson gson = new Gson();
	private final Class<Type> parseClass;

	public ResponseMappingHandler(Class<Type> paramClass) {
		this.parseClass = paramClass;
	}

	private Type exceptionObjectMappingCase(String paramString, boolean paramBoolean) {
		try {
			Type localResponse = this.parseClass.newInstance();
			localResponse.setResult(makeExceptionResult(paramString, paramBoolean));
			return localResponse;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return null;
	}

	private Result makeExceptionResult(String paramString, boolean paramBoolean) {
		try {
			Log.d(TAG, "## makeExceptionResult = " + paramString);
			Result localResult2 = gson.fromJson(paramString, parseClass).getResult();
			Log.d(TAG, "## makeExceptionResultl ocalResult2 = " + localResult2.toString());

			return localResult2;
		} catch (Exception localException) {
			Result localResult1 = new Result();
			if (paramBoolean) {
				localResult1.setCode(0);
				localResult1.setMessage("Success but response parsing error");
				return localResult1;
			}
			localResult1.setCode(1);
			localResult1.setMessage("Fail and response parsing error");
			return localResult1;
		}
	}

	private Type mappingJsonToObject(String response, boolean paramBoolean) {
		try {
			int i = response.indexOf(":") + 1;
			int j = response.length() - 1;
			String s1 = response.substring(i, j);
			JsonElement resElement = (JsonElement) gson.fromJson(s1, JsonElement.class);
			Type localResponse = gson.fromJson(resElement, this.parseClass);
			return localResponse;
		} catch (Exception localException) {
			Log.e(TAG, "Error occur while mapping response to object");
			localException.printStackTrace();
		}
		return exceptionObjectMappingCase(response, paramBoolean);
	}

	public abstract void handleFail(int statusCode, Header[] headers, byte[] response, Throwable error);

	public abstract void handleSuccess(int statusCode, Header[] headers, Type response);

	public abstract void handleNoNetwork();

	public void onFailure(int paramInt, Header[] paramArrayOfHeader, byte[] paramString, Throwable paramThrowable) {
		// T localResponse = mappingJsonToObject(paramString, false);
		// Log.e(TAG, "statusCode : " + paramInt);
		handleFail(paramInt, paramArrayOfHeader, paramString, paramThrowable);
		MLog.e(TAG, "error::"+paramThrowable.getMessage());
	}

	public void onSuccess(int paramInt, Header[] paramArrayOfHeader, String paramString) {
		Type localResponse = mappingJsonToObject(paramString, true);
		// Log.d(TAG, "statusCode : " + paramInt);
		// Log.d(TAG, "response : " + localResponse.toString());
		handleSuccess(paramInt, paramArrayOfHeader, localResponse);
	}
}
