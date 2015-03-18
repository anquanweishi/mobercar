package com.xtrd.obdcar.entity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 车况
 * 
 * @author Administrator
 * 
 */
public class CarCondtion {
	private String price;
	private int check;
	private int speed;
	private int status;
	private double dis;
	private double fuel;
	private int ttl;
	private ArrayList<CarSysItem> list = new ArrayList<CarSysItem>();

	
	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public int getCheck() {
		return check;
	}

	public void setCheck(int check) {
		this.check = check;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public double getDis() {
		return dis;
	}

	public void setDis(double dis) {
		this.dis = dis;
	}

	public double getFuel() {
		return fuel;
	}

	public void setFuel(double fuel) {
		this.fuel = fuel;
	}

	public int getTtl() {
		return ttl;
	}

	public void setTtl(int ttl) {
		this.ttl = ttl;
	}

	public ArrayList<CarSysItem> getList() {
		return list;
	}

	public void setList(ArrayList<CarSysItem> list) {
		this.list = list;
	}

	public void parser(JSONObject json) {
		try {
			if (json != null) {
				if (json.has("price")) {
					setPrice(json.getString("price"));
				}
				if (json.has("check")) {
					setCheck(json.getInt("check"));
				}
				if (json.has("status")) {
					setStatus(json.getInt("status"));
				}
				if (json.has("dis")) {
					setDis(json.getDouble("dis"));
				}
				if (json.has("fuel")) {
					setFuel(json.getDouble("fuel"));
				}
				if (json.has("speed")) {
					setSpeed(json.getInt("speed"));
				}
				if (json.has("ttl")) {
					setTtl(json.getInt("ttl"));
				}

				if (json.has("data")) {
					parserData(json.getJSONArray("data"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void parserData(JSONArray array) {
		if (array != null && array.length() > 0) {
			CarSysItem item = null;
			for (int i = 0; i < array.length(); i++) {
				item = new CarSysItem();
				item.parser(array.optJSONObject(i));
				list.add(item);
			}
		}
	}

}
