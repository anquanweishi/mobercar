package com.xtrd.obdcar.entity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CarSysItem {
	private String title;
	private int size;
	private ArrayList<CarItem> list = new ArrayList<CarItem>();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public ArrayList<CarItem> getList() {
		return list;
	}

	public void setList(ArrayList<CarItem> list) {
		this.list = list;
	}

	public void parser(JSONObject json) {
		try {
			if (json != null) {
				if (json.has("name")) {
					setTitle(json.getString("name"));
				}
				if (json.has("size")) {
					setSize(json.getInt("size"));
				}

				if (json.has("datas")) {
					parserDatas(json.getJSONArray("datas"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void parserDatas(JSONArray array) {
		if (array != null && array.length() > 0) {
			CarItem item = null;
			for (int i = 0; i < array.length(); i++) {
				item = new CarItem();
				item.parser(array.optJSONObject(i));
				list.add(item);
			}
		}

	}

}
