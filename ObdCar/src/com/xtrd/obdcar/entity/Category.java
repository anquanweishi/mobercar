package com.xtrd.obdcar.entity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 分类
 * 
 * @author start
 * 
 */
public class Category {
	private int id;
	private String name;
	private String imgUrl;
	private ArrayList<CategoryItem> list = new ArrayList<CategoryItem>();

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

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public ArrayList<CategoryItem> getList() {
		return list;
	}

	public void setList(ArrayList<CategoryItem> list) {
		this.list = list;
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
				if (json.has("imgUrl")) {
					setImgUrl(json.getString("imgUrl"));
				}
				if (json.has("keywordList")) {
					parser(json.getJSONArray("keywordList"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private void parser(JSONArray array) {
		if (array != null && array.length() > 0) {
			CategoryItem item = null;
			for (int i = 0; i < array.length(); i++) {
				item = new CategoryItem();
				item.parser(array.optJSONObject(i));
				list.add(item);
			}
		}
	}
}
