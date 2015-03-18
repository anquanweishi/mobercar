package com.xtrd.obdcar.entity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 保养记录
 * 
 * @author Administrator
 * 
 */
public class Recoder {

	private String icon;
	private String plateNumber;
	private String lastTime;
	private int price;
	private String distance;
	private String time;
	private ArrayList<MainTainChildItem> list = new ArrayList<MainTainChildItem>();

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getPlateNumber() {
		return plateNumber;
	}

	public void setPlateNumber(String plateNumber) {
		this.plateNumber = plateNumber;
	}

	public String getLastTime() {
		return lastTime;
	}

	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
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
				if(json.has("icon")) {
					setIcon(json.getString("icon"));
				}
				if(json.has("plateNumber")) {
					setPlateNumber(json.getString("plateNumber"));
				}
				if(json.has("lastTime")) {
					setLastTime(json.getString("lastTime"));
				}
				if(json.has("price")) {
					setPrice(json.getInt("price"));
				}
				if(json.has("distance")) {
					setDistance(json.getString("distance"));
				}
				if(json.has("time")) {
					setTime(json.getString("time"));
				}
				if(json.has("list")) {
					parserItems(json.getJSONArray("list"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void parserItems(JSONArray jsonArray) {
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
