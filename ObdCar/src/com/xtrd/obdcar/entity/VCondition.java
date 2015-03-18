package com.xtrd.obdcar.entity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 车况
 * 
 * @author Administrator
 * 
 */
public class VCondition {

	private String name;
	private String code;
	private String stand;
	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getStand() {
		return stand;
	}

	public void setStand(String stand) {
		this.stand = stand;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void parser(JSONObject json) {
		try {
			if (json != null) {
				if (json.has("name")) {
					setName(json.getString("name"));
				}
				if (json.has("code")) {
					setCode(json.getString("code"));
				}
				if (json.has("faultCode")) {
					setCode(json.getString("faultCode"));
				}
				if (json.has("stand")) {
					setStand(json.getString("stand"));
				}
				if (json.has("description")) {
					setDescription(json.getString("description"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
