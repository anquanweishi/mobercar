package com.xtrd.obdcar.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class MComment {

	private String title;
	private int status;
	private String desc;
	private String createTime;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public void parser(JSONObject json) {
		if(json!=null) {
			try {
				if(json.has("content")) {
					setDesc(json.getString("content"));
				}
				if(json.has("type")) {
					setStatus(json.getInt("type"));
				}
				if(json.has("time")) {
					setCreateTime(json.getString("time"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

}
