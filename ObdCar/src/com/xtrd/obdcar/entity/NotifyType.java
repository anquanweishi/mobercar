package com.xtrd.obdcar.entity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 通知类型
 * 
 * @author Administrator
 * 
 */
public class NotifyType {
	private int id;
	private String name;
	private int type = 1;
	public NotifyType(){
	}
	
	public NotifyType(String name){
		this.name = name;
	}

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
	
	

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
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
				if(json.has("isClose")) {
					setType(json.getInt("isClose"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

}
