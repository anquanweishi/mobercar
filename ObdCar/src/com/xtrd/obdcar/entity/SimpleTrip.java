package com.xtrd.obdcar.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class SimpleTrip {

	private int dataId;
	private int vehicleId;
	private int tripId;
	private long troubleCodeNumber;
	private String gpsTime;

	public int getDataId() {
		return dataId;
	}

	public void setDataId(int dataId) {
		this.dataId = dataId;
	}

	public int getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(int vehicleId) {
		this.vehicleId = vehicleId;
	}

	public int getTripId() {
		return tripId;
	}

	public void setTripId(int tripId) {
		this.tripId = tripId;
	}

	public long getTroubleCodeNumber() {
		return troubleCodeNumber;
	}

	public void setTroubleCodeNumber(long troubleCodeNumber) {
		this.troubleCodeNumber = troubleCodeNumber;
	}

	public String getGpsTime() {
		return gpsTime;
	}

	public void setGpsTime(String gpsTime) {
		this.gpsTime = gpsTime;
	}
	
	public void parser(JSONObject opt) throws JSONException {
		if(opt!=null) {
			if(opt.has("dataId")) {
				setDataId(opt.getInt("dataId"));
			}
			if(opt.has("vehicleId")) {
				setVehicleId(opt.getInt("vehicleId"));
			}
			if(opt.has("tripId")) {
				setTripId(opt.getInt("tripId"));
			}
			if(opt.has("troubleCodeNumber")) {
				setTroubleCodeNumber(opt.getLong("troubleCodeNumber"));
			}
			if(opt.has("gpsTime")) {
				setGpsTime(opt.getString("gpsTime"));
			}
		}
	}

}
