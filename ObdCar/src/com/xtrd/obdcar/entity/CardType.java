package com.xtrd.obdcar.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class CardType {
	private String id;
	private String key;
	private String code;
	private String value;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void parser(JSONObject opt) throws JSONException {
		if (opt != null) {
			if (opt.has("id")) {
				setId(opt.getString("id"));
			}
			if (opt.has("code")) {
				setCode(opt.getString("code"));
			}
			if (opt.has("value")) {
				setValue(opt.getString("value"));
			}
		}
	}

}
