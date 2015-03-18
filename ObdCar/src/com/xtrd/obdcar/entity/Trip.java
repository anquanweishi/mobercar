package com.xtrd.obdcar.entity;

import java.math.BigDecimal;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 行程
 * 
 * @author start
 * 
 */
public class Trip {
	private int tripId;
	private int vehicleId;
	private double distance;
	private double fuelAmount;
	private double fuelConsumption100;
	private double speedTop;
	private double speedAvg;
	private long suddenAcceleration;
	private long suddenDeceleration;
	private long idleDuration;
	private long duration;
	private int type;
	private double drivingScore;
	private String gpsStartTime;
	private String gpsEndTime;
	private int carStatus;//1代表停车，2代表行驶，3代表离线
	private GPSInfo startGps = new GPSInfo();
	private GPSInfo endGps = new GPSInfo();
	private String slocation;
	private String elocation;
	private double price;

	public int getTripId() {
		return tripId;
	}

	public void setTripId(int tripId) {
		this.tripId = tripId;
	}

	public int getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(int vehicleId) {
		this.vehicleId = vehicleId;
	}

	public double getDistance() {
		BigDecimal b = new BigDecimal(distance);
		return b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getFuelAmount() {
		return fuelAmount;
	}

	public void setFuelAmount(double fuelAmount) {
		this.fuelAmount = fuelAmount;
	}

	public double getFuelConsumption100() {
		return fuelConsumption100;
	}

	public void setFuelConsumption100(double fuelConsumption100) {
		this.fuelConsumption100 = fuelConsumption100;
	}

	public double getSpeedTop() {
		return speedTop;
	}

	public void setSpeedTop(double speedTop) {
		this.speedTop = speedTop;
	}

	public double getSpeedAvg() {
		return speedAvg;
	}

	public void setSpeedAvg(double speedAvg) {
		this.speedAvg = speedAvg;
	}

	public long getSuddenAcceleration() {
		return suddenAcceleration;
	}

	public void setSuddenAcceleration(long suddenAcceleration) {
		this.suddenAcceleration = suddenAcceleration;
	}

	public long getSuddenDeceleration() {
		return suddenDeceleration;
	}

	public void setSuddenDeceleration(long suddenDeceleration) {
		this.suddenDeceleration = suddenDeceleration;
	}

	public long getIdleDuration() {
		return idleDuration;
	}

	public void setIdleDuration(long idleDuration) {
		this.idleDuration = idleDuration;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public double getDrivingScore() {
		return drivingScore;
	}

	public void setDrivingScore(double drivingScore) {
		this.drivingScore = drivingScore;
	}

	public String getGpsStartTime() {
		return gpsStartTime;
	}

	public void setGpsStartTime(String gpsStartTime) {
		this.gpsStartTime = gpsStartTime;
	}

	public String getGpsEndTime() {
		return gpsEndTime;
	}

	public void setGpsEndTime(String gpsEndTime) {
		this.gpsEndTime = gpsEndTime;
	}

	public int getCarStatus() {
		return carStatus;
	}

	public void setCarStatus(int carStatus) {
		this.carStatus = carStatus;
	}

	public GPSInfo getStartGps() {
		return startGps;
	}

	public void setStartGps(GPSInfo startGps) {
		this.startGps = startGps;
	}

	public GPSInfo getEndGps() {
		return endGps;
	}

	public void setEndGps(GPSInfo endGps) {
		this.endGps = endGps;
	}

	public String getSlocation() {
		return slocation;
	}

	public void setSlocation(String slocation) {
		this.slocation = slocation;
	}

	public String getElocation() {
		return elocation;
	}

	public void setElocation(String elocation) {
		this.elocation = elocation;
	}
	
	

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void parser(JSONObject opt) throws JSONException {
		if (opt != null) {
			if (opt.has("tripId")) {
				setTripId(opt.getInt("tripId"));
			}
			if (opt.has("vehicleId")) {
				setVehicleId(opt.getInt("vehicleId"));
			}
			if (opt.has("type")) {
				setType(opt.getInt("type"));
			}
			if (opt.has("distance")) {
				setDistance(opt.getDouble("distance"));
			}
			if (opt.has("fuelAmount")) {
				setFuelAmount(opt.getDouble("fuelAmount"));
			}
			if (opt.has("fuelConsumption100")) {
				setFuelConsumption100(opt.getDouble("fuelConsumption100"));
			}
			if (opt.has("speedTop")) {
				setSpeedTop(opt.getDouble("speedTop"));
			}

			if (opt.has("speedAvg")) {
				setSpeedAvg(opt.getDouble("speedAvg"));
			}

			if (opt.has("suddenAcceleration")) {
				setSuddenAcceleration(opt.getLong("suddenAcceleration"));
			}

			if (opt.has("suddenDeceleration")) {
				setSuddenDeceleration(opt.getLong("suddenDeceleration"));
			}

			if (opt.has("idleDuration")) {
				setIdleDuration(opt.getLong("idleDuration"));
			}
			if (opt.has("duration")) {
				setDuration(opt.getLong("duration"));
			}
			if (opt.has("drivingScore")) {
				setDrivingScore(opt.getDouble("drivingScore"));
			}

			if (opt.has("gpsStartTime")) {
				setGpsStartTime(opt.getString("gpsStartTime"));
			}
			if (opt.has("gpsEndTime")) {
				setGpsEndTime(opt.getString("gpsEndTime"));
			}
			if (opt.has("carStatus")) {
				setCarStatus(opt.getInt("carStatus"));
			}
			if (opt.has("startGps")) {
				setStartGps(parserGps(opt.getJSONObject("startGps")));
			}
			if (opt.has("endGps")) {
				setEndGps(parserGps(opt.getJSONObject("endGps")));
			}
			if (opt.has("price")) {
				setPrice(opt.getDouble("price"));
			}
		}
	}

	private GPSInfo parserGps(JSONObject opt) throws JSONException {
		if (opt != null) {
			GPSInfo gps = new GPSInfo();
			gps.parser(opt);
			return gps;
		}
		return null;
	}
}
