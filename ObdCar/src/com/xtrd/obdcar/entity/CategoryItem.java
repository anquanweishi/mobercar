package com.xtrd.obdcar.entity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 类别子项
 * 
 * @author Administrator
 * 
 */
public class CategoryItem {

	private int id;
	private String word;
	private int keyworkTypeId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getKeyworkTypeId() {
		return keyworkTypeId+"";
	}

	public void setKeyworkTypeId(int keyworkTypeId) {
		this.keyworkTypeId = keyworkTypeId;
	}

	public void parser(JSONObject json) {
		try {
			if (json != null) {
				if (json.has("id")) {
					setId(json.getInt("id"));
				}
				if (json.has("word")) {
					setWord(json.getString("word"));
				}
				if (json.has("keyworkTypeId")) {
					setKeyworkTypeId(json.getInt("keyworkTypeId"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
