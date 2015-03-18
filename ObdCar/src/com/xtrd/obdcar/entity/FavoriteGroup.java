package com.xtrd.obdcar.entity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FavoriteGroup {

	private String name;
	private int type;
	private ArrayList<Favorite> list = new ArrayList<Favorite>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public ArrayList<Favorite> getList() {
		return list;
	}

	public void setList(ArrayList<Favorite> list) {
		this.list = list;
	}

	public void parser(JSONObject json) {
		try {
			if (json != null) {
				if (json.has("name")) {
					setName(json.getString("name"));
				}
				if (json.has("type")) {
					setType(json.getInt("type"));
				}
				if (json.has("list")) {
					parserList(json.getJSONArray("list"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void parserList(JSONArray array) {
		if (array != null && array.length() > 0) {
			Favorite item = null;
			for (int i = 0; i < array.length(); i++) {
				item = new Favorite();
				item.parser(array.optJSONObject(i));
				list.add(item);
			}
		}
	}

}
