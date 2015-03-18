package com.xtrd.obdcar.entity;

import java.math.BigDecimal;

import org.json.JSONException;
import org.json.JSONObject;

public class FuelReport {

	private int vehicleId;
	private String year;
	private String month;
	private String day;
	private double fuelAmount;
	private double distance;

	private double fuelConmsumeInstant;
	private double velocity;
	private String gpsTime;

	public int getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(int vehicleId) {
		this.vehicleId = vehicleId;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month<10?"0"+month:month+"";
	}

	public String getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day<10?"0"+day:day+"";
	}

	public double getFuelAmount() {
		BigDecimal b = new BigDecimal(fuelAmount);  
		return b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public void setFuelAmount(double fuelAmount) {
		this.fuelAmount = fuelAmount;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getFuelConmsumeInstant() {
		BigDecimal b = new BigDecimal(fuelConmsumeInstant);  
		return b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public void setFuelConmsumeInstant(double fuelConmsumeInstant) {
		this.fuelConmsumeInstant = fuelConmsumeInstant;
	}

	public double getVelocity() {
		return velocity;
	}

	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}

	public String getGpsTime() {
		return gpsTime;
	}

	public void setGpsTime(String gpsTime) {
		this.gpsTime = gpsTime;
	}

	public void parser(JSONObject opt) throws JSONException {
		if (opt.has("vehicleId")) {
			setVehicleId(opt.getInt("vehicleId"));
		}
		if (opt.has("year")) {
			setYear(opt.getInt("year") + "");
		}
		if (opt.has("month")) {
			setMonth(opt.getInt("month"));
		}
		if (opt.has("day")) {
			setDay(opt.getInt("day"));
		}
		if (opt.has("fuelAmount")) {
			setFuelAmount(opt.getDouble("fuelAmount"));
		}
		if (opt.has("distance")) {
			setDistance(opt.getDouble("distance"));
		}
		if (opt.has("fuelComsumeInstant")) {
			setFuelConmsumeInstant(opt.getDouble("fuelComsumeInstant"));
		}
		if (opt.has("velocity")) {
			setVelocity(opt.getDouble("velocity"));
		}
		if (opt.has("gpsTime")) {
			setGpsTime(opt.getString("gpsTime"));
		}
	}

}
