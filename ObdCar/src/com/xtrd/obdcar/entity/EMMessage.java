package com.xtrd.obdcar.entity;

public class EMMessage {

	private int branchId;
	private String imgurl;
	private String user;
	private String content;
	private long time;
	private int type;// 0 发送 1 接收

	public EMMessage() {
	}

	public EMMessage(int branchId, String content, long time) {
		this.branchId = branchId;
		this.content = content;
		this.time = time;
	}
	public EMMessage(int branchId, String content, long time,int type) {
		this.branchId = branchId;
		this.content = content;
		this.time = time;
		this.type = type;
	}

	public int getBranchId() {
		return branchId;
	}

	public void setBranchId(int branchId) {
		this.branchId = branchId;
	}

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
