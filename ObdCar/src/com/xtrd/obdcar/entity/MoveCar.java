package com.xtrd.obdcar.entity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 移动车辆
 * @author start
 *
 */
public class MoveCar {
	
	private int vehicleId;
	private String plateNumber;
	private int carStatus;//1代表停车，2代表行驶，3代表离线
	private boolean driving;
	private boolean isOnline;
	private String updateTime;
	public int getVehicleId() {
		return vehicleId;
	}
	public void setVehicleId(int vehicleId) {
		this.vehicleId = vehicleId;
	}
	public String getPlateNumber() {
		return plateNumber;
	}
	public void setPlateNumber(String plateNumber) {
		this.plateNumber = plateNumber;
	}
	public int getCarStatus() {
		return carStatus;
	}
	public void setCarStatus(int carStatus) {
		this.carStatus = carStatus;
	}
	public boolean isDriving() {
		return driving;
	}
	public void setDriving(boolean driving) {
		this.driving = driving;
	}
	public boolean isOnline() {
		return isOnline;
	}
	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	
	public void parser(JSONObject opt) throws JSONException {
		if(opt!=null) {
			if(opt.has("vehicleId")) {
				setVehicleId(opt.getInt("vehicleId"));
			}
			if(opt.has("plateNumber")) {
				setPlateNumber(opt.getString("plateNumber"));
			}
			if(opt.has("carStatus")) {
				setCarStatus(opt.getInt("carStatus"));
			}
			if(opt.has("driving")) {
				setDriving(opt.getBoolean("driving"));
			}
			if(opt.has("isOnline")) {
				setOnline(opt.getBoolean("isOnline"));
			}
			if(opt.has("updateTime")) {
				setUpdateTime(opt.getString("updateTime"));
			}
		}
	}

}
