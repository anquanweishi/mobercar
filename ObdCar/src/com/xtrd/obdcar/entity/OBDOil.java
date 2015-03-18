package com.xtrd.obdcar.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class OBDOil {

	private int id;
	private String category;
	private String name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void parser(JSONObject opt) {
		try {
			if(opt!=null) {
				if(opt.has("id")) {
					setId(opt.getInt("id"));
				}
				if(opt.has("category")) {
					setCategory(opt.getString("category"));
				}
				if(opt.has("name")) {
					setName(opt.getString("name"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
