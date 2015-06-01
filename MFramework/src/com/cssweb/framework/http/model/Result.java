
package com.cssweb.framework.http.model;

public class Result {

	public static final int OK = 0;
	private int code;
	private String message;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "Result [code=" + code + ", message=" + message + "]";
	}

}
