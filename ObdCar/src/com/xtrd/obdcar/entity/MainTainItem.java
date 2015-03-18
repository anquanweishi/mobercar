package com.xtrd.obdcar.entity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author Administrator
 * 
 */
public class MainTainItem {
	private int id;
	private String name;
	private String time;
	private ArrayList<MainTainChildItem> list = new ArrayList<MainTainChildItem>();

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

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public ArrayList<MainTainChildItem> getList() {
		return list;
	}

	public void setList(ArrayList<MainTainChildItem> list) {
		this.list = list;
	}

	public void parser(JSONObject json) {
		try {
			if(json!=null) {
				if(json.has("id")) {
					setId(json.getInt("id"));
				}
				if(json.has("name")) {
					setName(json.getString("name"));
				}
				if(json.has("time")) {
					setTime(json.getString("time"));
				}
				if(json.has("data")) {
					parserArray(json.getJSONArray("data"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void parserArray(JSONArray jsonArray) {
		if(jsonArray!=null&&jsonArray.length()>0) {
			MainTainChildItem item = null;
			for(int i=0;i<jsonArray.length();i++) {
				item = new MainTainChildItem();
				item.parser(jsonArray.optJSONObject(i));
				list.add(item);
			}
		}
	}
}
