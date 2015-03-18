package com.xtrd.obdcar.entity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 一键救援
 * 
 * @author Administrator
 * 
 */
public class OnekeyHelp {
	private String name;
	private ArrayList<HelpItem> list = new ArrayList<HelpItem>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<HelpItem> getList() {
		return list;
	}

	public void setList(ArrayList<HelpItem> list) {
		this.list = list;
	}

	public void parser(JSONObject json) {
		try {
			if (json != null) {
				if (json.has("name")) {
					setName(json.getString("name"));
				}
				if (json.has("list")) {
					parserList(json.getJSONArray("list"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void parserList(JSONArray jsonArray) {
		if (jsonArray != null && jsonArray.length() > 0) {
			HelpItem item = null;
			for (int i = 0; i < jsonArray.length(); i++) {
				item = new HelpItem();
				item.parser(jsonArray.optJSONObject(i));
				list.add(item);
			}
		}
	}
}
