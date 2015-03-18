package com.xtrd.obdcar.entity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 仪表盘
 * 
 * @author Administrator
 * 
 */
public class DashBoard {
	private int time;
	private double distance;

	private int tem;
	private int maxSpeed;
	private double elec;
	private double loc;

	private double fuelC;
	private double fuelAmount;
	private double price;

	private int whe;
	private int dec;
	private int acc;

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public int getTem() {
		return tem;
	}

	public void setTem(int tem) {
		this.tem = tem;
	}

	public int getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(int maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public double getElec() {
		return elec;
	}

	public void setElec(double elec) {
		this.elec = elec;
	}

	public double getLoc() {
		return loc;
	}

	public void setLoc(double loc) {
		this.loc = loc;
	}

	public double getFuelC() {
		return fuelC;
	}

	public void setFuelC(double fuelC) {
		this.fuelC = fuelC;
	}
	
	

	public double getFuelAmount() {
		return fuelAmount;
	}

	public void setFuelAmount(double fuelAmount) {
		this.fuelAmount = fuelAmount;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getWhe() {
		return whe;
	}

	public void setWhe(int whe) {
		this.whe = whe;
	}

	public int getDec() {
		return dec;
	}

	public void setDec(int dec) {
		this.dec = dec;
	}

	public int getAcc() {
		return acc;
	}

	public void setAcc(int acc) {
		this.acc = acc;
	}

	/**
	 * @param json
	 */
	public void parser(JSONObject json) {
		try {
			if (json != null) {
				if (json.has("temper")) {
					setTem(json.getInt("temper"));
				}
				if (json.has("distance")) {
					setDistance(json.getDouble("distance"));
				}
				if (json.has("time")) {
					setTime(json.getInt("time"));
				}
				if (json.has("dec")) {
					setDec(json.getInt("dec"));
				}
				if (json.has("fuelC")) {
					setFuelC(json.getDouble("fuelC"));
				}
				if (json.has("maxSpeed")) {
					setMaxSpeed(json.getInt("maxSpeed"));
				}
				if (json.has("whe")) {
					setWhe(json.getInt("whe"));
				}
				if (json.has("loc")) {
					setLoc(json.getDouble("loc"));
				}
				if (json.has("battery")) {
					setElec(json.getDouble("battery"));
				}
				if (json.has("acc")) {
					setAcc(json.getInt("acc"));
				}
				if (json.has("price")) {
					setPrice(json.getDouble("price"));
				}
				if (json.has("fuelAmount")) {
					setFuelAmount(json.getDouble("fuelAmount"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
