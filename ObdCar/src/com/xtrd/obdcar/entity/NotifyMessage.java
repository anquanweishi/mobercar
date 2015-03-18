package com.xtrd.obdcar.entity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 通知消息
 * 
 * @author Administrator
 * 
 */
public class NotifyMessage {
	private String content;
	private String time;
	private String title;
	private int vId;
	private int type;
	private String key;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getvId() {
		return vId;
	}

	public void setvId(int vId) {
		this.vId = vId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void parser(JSONObject json) {
		try {
			if (json != null) {
				if (json.has("vId")) {
					setvId(json.getInt("vId"));
				}
				if (json.has("title")) {
					setTitle(json.getString("title"));
				}
				if (json.has("content")) {
					setContent(json.getString("content"));
				}
				if (json.has("time")) {
					setTime(json.getString("time"));
				}
				if (json.has("type")) {
					setType(json.getInt("type"));
				}
				if (json.has("key")) {
					setKey(json.getString("key"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
