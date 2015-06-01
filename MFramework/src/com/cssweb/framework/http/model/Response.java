
package com.cssweb.framework.http.model;

public class Response {

	Result result;

	public Result getResult() {
		if (this.result == null)
			this.result = new Result();
		return this.result;
	}

	public void setResult(Result paramResult) {
		this.result = paramResult;
	}

	public String toString() {
		return "Response [result=" + this.result + "]";
	}
}
