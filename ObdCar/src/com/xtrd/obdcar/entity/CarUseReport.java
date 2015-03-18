package com.xtrd.obdcar.entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CarUseReport {

	private double distance;
	private double fuelAvg;
	private int faultNum;
	private int alarmNum;
	private String sumTime;
	private double price;
	private double speedTop;
	private String[] disUnits;
	private double[] distances;
	private String[] timeUnits;
	private double[] times;
	private String[] tempUnits;
	private double[] temperatures;
	private int acc;
	private int dec;
	private int whe;

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getFuelAvg() {
		return fuelAvg;
	}

	public void setFuelAvg(double fuelAvg) {
		this.fuelAvg = fuelAvg;
	}

	public int getFaultNum() {
		return faultNum;
	}

	public void setFaultNum(int faultNum) {
		this.faultNum = faultNum;
	}

	public int getAlarmNum() {
		return alarmNum;
	}

	public void setAlarmNum(int alarmNum) {
		this.alarmNum = alarmNum;
	}

	public String getSumTime() {
		return sumTime;
	}

	public void setSumTime(String sumTime) {
		this.sumTime = sumTime;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getSpeedTop() {
		return speedTop;
	}

	public void setSpeedTop(double speedTop) {
		this.speedTop = speedTop;
	}

	public String[] getDisUnits() {
		return disUnits;
	}

	public void setDisUnits(String[] disUnits) {
		this.disUnits = disUnits;
	}

	public double[] getDistances() {
		return distances;
	}

	public void setDistances(double[] distances) {
		this.distances = distances;
	}

	public String[] getTimeUnits() {
		return timeUnits;
	}

	public void setTimeUnits(String[] timeUnits) {
		this.timeUnits = timeUnits;
	}

	public double[] getTimes() {
		return times;
	}

	public void setTimes(double[] times) {
		this.times = times;
	}

	public String[] getTempUnits() {
		return tempUnits;
	}

	public void setTempUnits(String[] tempUnits) {
		this.tempUnits = tempUnits;
	}

	public double[] getTemperatures() {
		return temperatures;
	}

	public void setTemperatures(double[] temperatures) {
		this.temperatures = temperatures;
	}

	public int getAcc() {
		return acc;
	}

	public void setAcc(int acc) {
		this.acc = acc;
	}

	public int getDec() {
		return dec;
	}

	public void setDec(int dec) {
		this.dec = dec;
	}

	public int getWhe() {
		return whe;
	}

	public void setWhe(int whe) {
		this.whe = whe;
	}

	public void parser(JSONObject json) {
		try {
			if (json != null) {
				if (json.has("distance")) {
					setDistance(json.getDouble("distance"));
				}
				if (json.has("fuelAvg")) {
					setFuelAvg(json.getDouble("fuelAvg"));
				}
				if (json.has("faultNum")) {
					setFaultNum(json.getInt("faultNum"));
				}
				if (json.has("alarmNum")) {
					setAlarmNum(json.getInt("alarmNum"));
				}
				if (json.has("sumTime")) {
					setSumTime(json.getString("sumTime"));
				}
				if (json.has("price")) {
					setPrice(json.getDouble("price"));
				}
				if (json.has("speedTop")) {
					setSpeedTop(json.getDouble("speedTop"));
				}
				if (json.has("distances")) {
					setDistances(parserArray(json.getJSONArray("distances")));
				}
				if (json.has("times")) {
					setTimes(parserArray(json.getJSONArray("times")));
				}
				if (json.has("temperatures")) {
					setTemperatures(parserArray(json
							.getJSONArray("temperatures")));
				}
				if (json.has("acc")) {
					setAcc(json.getInt("acc"));
				}
				if (json.has("dec")) {
					setDec(json.getInt("dec"));
				}
				if (json.has("whe")) {
					setWhe(json.getInt("whe"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private String[] parserStringArray(JSONArray array) {
		if (array != null && array.length() > 0) {
			String[] data = new String[array.length()];
			for (int i = 0; i < array.length(); i++) {
				data[i] = array.optString(i);
			}
			return data;
		}
		return null;
	}

	private double[] parserArray(JSONArray array) {
		if (array != null && array.length() > 0) {
			double[] data = new double[array.length()];
			for (int i = 0; i < array.length(); i++) {
				data[i] = array.optDouble(i);
			}
			return data;
		}
		return null;
	}

	public void parserUnits(JSONObject json) {
		try {
			if (json != null) {

				if (json.has("distance")) {
					setDisUnits(parserStringArray(json.getJSONArray("distance")));
				}
				if (json.has("time")) {
					setTimeUnits(parserStringArray(json.getJSONArray("time")));
				}
				if (json.has("temperature")) {
					setTempUnits(parserStringArray(json.getJSONArray("temperature")));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
