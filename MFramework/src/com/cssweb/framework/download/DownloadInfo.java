
package com.cssweb.framework.download;


import java.io.Serializable;


public class DownloadInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -316399903230939040L;
	private int id;// 下载器id
	private String url;// 下载器网络标识
	private int startPos;// 开始点
	private int endPos;// 结束点
	private int compeleteSize;// 完成度
	private int status;
	private String installPath;
	private boolean isMendetory;
	private String appName;

	public DownloadInfo(int id, String url, int startPos, int endPos, int compeleteSize) {
		super();
		this.id = id;
		this.url = url;
		this.startPos = startPos;
		this.endPos = endPos;
		this.compeleteSize = compeleteSize;
	}

	public DownloadInfo() {
		super();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getStartPos() {
		return startPos;
	}

	public void setStartPos(int startPos) {
		this.startPos = startPos;
	}

	public int getEndPos() {
		return endPos;
	}

	public void setEndPos(int endPos) {
		this.endPos = endPos;
	}

	public int getCompeleteSize() {
		return compeleteSize;
	}

	public void setCompeleteSize(int compeleteSize) {
		this.compeleteSize = compeleteSize;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getInstallPath() {
		return installPath;
	}

	public void setInstallPath(String installPath) {
		this.installPath = installPath;
	}

	public boolean isMendetory() {
		return isMendetory;
	}

	public void setMendetory(boolean isMendetory) {
		this.isMendetory = isMendetory;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	@Override
	public String toString() {
		return "DownloadInfo [id=" + id + ", url=" + url + ", startPos=" + startPos + ", endPos=" + endPos
				+ ", compeleteSize=" + compeleteSize + ", status=" + status + ", installPath=" + installPath
				+ ", isMendetory=" + isMendetory + ", appName=" + appName + "]";
	}

}
