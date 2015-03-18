package com.xtrd.obdcar.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class HelpItem {
	private int id;
	private String name;
	private String val;

	private boolean checked;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public void parser(JSONObject json) {
		try {
			if (json != null) {
				if (json.has("id")) {
					setId(json.getInt("id"));
				}
				if (json.has("name")) {
					setName(json.getString("name"));
				}
				if (json.has("val")) {
					setVal(json.getString("val"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
