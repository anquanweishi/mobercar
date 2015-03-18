package com.xtrd.obdcar.entity;

import org.json.JSONException;
import org.json.JSONObject;

import com.xtrd.obdcar.utils.StringUtils;

public class MainTainChildItem {
	private int id;
	private String name;
	private String count;
	private String range;
	private String time;
	private int price;

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

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
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
				if (json.has("distance")) {
					setRange(json.getDouble("distance") + "");
				}
				if (json.has("times")) {
					setCount(json.getInt("times") + "");
				}
				if (json.has("time")) {
					setTime(!StringUtils.isNullOrEmpty(json.getString("time"))&&!"null".equals(json.getString("time"))?json.getString("time"):"");
				}

				if (json.has("price")) {
					setPrice(json.getInt("price"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
