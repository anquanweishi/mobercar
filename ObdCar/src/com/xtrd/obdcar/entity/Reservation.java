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
public class Reservation {
	private String orderId;
	private int vehicleId;
	private String icon;
	private String plateNumber;
	private String lastTime="";
	private String name;
	private String phone;
	private int status;
	private double distance;
	private String time;
	private ArrayList<MainTainChildItem> list = new ArrayList<MainTainChildItem>();

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public int getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(int vehicleId) {
		this.vehicleId = vehicleId;
	}

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
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
			if (json != null) {
				if (json.has("id")) {
					setOrderId(json.getString("id"));
				}
				if (json.has("vehicleId")) {
					setVehicleId(json.getInt("vehicleId"));
				}

				if (json.has("icon")) {
					setIcon(json.getString("icon"));
				}
				if (json.has("plateNumber")) {
					setPlateNumber(json.getString("plateNumber"));
				}
				if (json.has("lastTime")) {
					setLastTime(json.getString("lastTime"));
				}
				if (json.has("name")) {
					setName(json.getString("name"));
				}
				if (json.has("phone")) {
					setPhone(json.getString("phone"));
				}
				if (json.has("status")) {
					setStatus(json.getInt("status"));
				}
				if (json.has("distance")) {
					setDistance(json.getDouble("distance"));
				}
				if (json.has("time")) {
					setTime(json.getString("time"));
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
			MainTainChildItem item = null;
			for (int i = 0; i < jsonArray.length(); i++) {
				item = new MainTainChildItem();
				item.parser(jsonArray.optJSONObject(i));
				list.add(item);
			}
			setList(list);
		}
	}

}
