package com.xtrd.obdcar.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class FuelInfo {

	private int vehicleId;
	private double fuelAmount;
	private double distance;
	private double avgFC100;
	private double maxFC100;
	private double minFC100;
	private double avgDistance;

	public int getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(int vehicleId) {
		this.vehicleId = vehicleId;
	}

	public double getFuelAmount() {
		return fuelAmount;
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

	public double getAvgFC100() {
		return avgFC100;
	}

	public void setAvgFC100(double avgFC100) {
		this.avgFC100 = avgFC100;
	}

	public double getMaxFC100() {
		return maxFC100;
	}

	public void setMaxFC100(double maxFC100) {
		this.maxFC100 = maxFC100;
	}

	public double getMinFC100() {
		return minFC100;
	}

	public void setMinFC100(double minFC100) {
		this.minFC100 = minFC100;
	}

	public double getAvgDistance() {
		return avgDistance;
	}

	public void setAvgDistance(double avgDistance) {
		this.avgDistance = avgDistance;
	}
	
	public void parser(JSONObject opt) throws JSONException {
		if(opt!=null) {
			if(opt.has("vehicleId")) {
				setVehicleId(opt.getInt("vehicleId"));
			}
			if(opt.has("fuelAmount")) {
				setFuelAmount(opt.getDouble("fuelAmount"));
			}

			if(opt.has("distance")) {
				setDistance(opt.getDouble("distance"));
			}
			if(opt.has("avgFC100")) {
				setAvgFC100(opt.getDouble("avgFC100"));
			}
			if(opt.has("maxFC100")) {
				setMaxFC100(opt.getDouble("maxFC100"));
			}
			if(opt.has("minFC100")) {
				setMinFC100(opt.getDouble("minFC100"));
			}
			if(opt.has("avgDistance")) {
				setAvgDistance(opt.getDouble("avgDistance"));
			}
		}
	}

}
