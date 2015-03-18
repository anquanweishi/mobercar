package com.xtrd.obdcar.entity;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 事故类型
 * 
 * @author Administrator
 * 
 */
public class AccidentType {

	private int id;
	private int num;
	private String name;
	private String description;
	private ArrayList<String> svrUrls = new ArrayList<String>();
	private ArrayList<String> localUrls = new ArrayList<String>();
	private int position;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ArrayList<String> getSvrUrls() {
		return svrUrls;
	}

	public void setSvrUrls(ArrayList<String> svrUrls) {
		this.svrUrls = svrUrls;
	}

	public ArrayList<String> getLocalUrls() {
		return localUrls;
	}

	public void setLocalUrls(ArrayList<String> localUrls) {
		this.localUrls = localUrls;
	}

	public void addLocal(String localUrl) {
		localUrls.add(localUrl);
	}

	public void addSvr(String svrUrl) {
		svrUrls.add(svrUrl);
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public void parser(JSONObject json) {
		try {
			if (json != null) {
				if (json.has("id")) {
					setId(json.getInt("id"));
				}
				if (json.has("num")) {
					setNum(json.getInt("num"));
				}
				if (json.has("name")) {
					setName(json.getString("name"));
				}
				if (json.has("description")) {
					setDescription(json.getString("description"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
