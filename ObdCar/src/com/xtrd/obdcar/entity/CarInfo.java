package com.xtrd.obdcar.entity;

import java.math.BigDecimal;

import org.json.JSONException;
import org.json.JSONObject;

import com.xtrd.obdcar.XtrdApp;
import com.xtrd.obdcar.db.SettingLoader;

/**
 * 车辆
 * 
 * @author start
 * 
 */
public class CarInfo extends CarEntity {
	private double fuelAmount;
	private double fuelConsumption100;
	private long continueDistance;
	private double distance;
	private double batteryRemain;
	private long troubleCodeNumber;
	private long drivingScore;
	private double drivingScoreRank;
	private double longitude;
	private double latitude;
	private int carStatus;// 1代表停车，2代表行驶，3代表离线
	private boolean driving;
	private boolean isOnline;
	private int sn;
	private String coolantTemperature;
	private boolean isBind;
	// 是否被选中
	private boolean checked;
	private String driveDistance;// 校准里程

	public double getFuelAmount() {
		BigDecimal b = new BigDecimal(fuelAmount);
		return b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public void setFuelAmount(double fuelAmount) {
		this.fuelAmount = fuelAmount;
	}

	public double getFuelConsumption100() {
		BigDecimal b = new BigDecimal(fuelConsumption100);
		return b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public void setFuelConsumption100(double fuelConsumption100) {
		this.fuelConsumption100 = fuelConsumption100;
	}

	public long getContinueDistance() {
		return continueDistance;
	}

	public void setContinueDistance(long continueDistance) {
		this.continueDistance = continueDistance;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getBatteryRemain() {
		BigDecimal b = new BigDecimal(batteryRemain);
		return b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public void setBatteryRemain(double batteryRemain) {
		this.batteryRemain = batteryRemain;
	}

	public long getTroubleCodeNumber() {
		return troubleCodeNumber;
	}

	public void setTroubleCodeNumber(long troubleCodeNumber) {
		this.troubleCodeNumber = troubleCodeNumber;
	}

	public long getDrivingScore() {
		return drivingScore;
	}

	public void setDrivingScore(long drivingScore) {
		this.drivingScore = drivingScore;
	}

	public int getDrivingScoreRank() {
		BigDecimal bg = new BigDecimal(drivingScoreRank);
		return (int) (bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() * 100);
	}

	public void setDrivingScoreRank(double drivingScoreRank) {
		this.drivingScoreRank = drivingScoreRank;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
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

	public int getSn() {
		return sn;
	}

	public void setSn(int sn) {
		this.sn = sn;
	}

	public String getCoolantTemperature() {
		return coolantTemperature;
	}

	public void setCoolantTemperature(String coolantTemperature) {
		this.coolantTemperature = coolantTemperature;
	}

	public boolean isBind() {
		return isBind;
	}

	public void setBind(boolean isBind) {
		this.isBind = isBind;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getDriveDistance() {
		return driveDistance;
	}

	public void setDriveDistance(String driveDistance) {
		this.driveDistance = driveDistance;
	}

	public void parser(JSONObject opt) throws JSONException {
		if (opt != null) {
			if (opt.has("vehicleId")) {
				setVehicleId(opt.getInt("vehicleId"));
			}
		}
		if (opt != null) {
			if (opt.has("drivingAreaCode")) {
				setDrivingAreaCode(opt.getString("drivingAreaCode"));
			}
		}
		if (opt != null) {
			if (opt.has("plateNumber")) {
				setPlateNumber(opt.getString("plateNumber"));
			}
		}
		if (opt != null) {
			if (opt.has("vin")) {
				setVin(opt.getString("vin"));
			}
		}
		if (opt != null) {
			if (opt.has("engineNumber")) {
				setEngineNumber(opt.getString("engineNumber"));
			}
		}
		if (opt != null) {
			if (opt.has("defaultFuelTypeId")) {
				setDefaultFuelTypeId(opt.getInt("defaultFuelTypeId"));
			}
		}
		if (opt != null) {
			if (opt.has("ownerId")) {
				setOwnerId(opt.getInt("ownerId"));
			}
		}
		if (opt != null) {
			if (opt.has("modelId")) {
				setModelId(opt.getInt("modelId"));
			}
		}
		if (opt != null) {
			if (opt.has("year")) {
				setYear(opt.getString("year"));
			}
		}
		if (opt != null) {
			if (opt.has("series")) {
				setSeries(opt.getString("series"));
			}
		}
		if (opt != null) {
			if (opt.has("branchId")) {
				setBranchId(opt.getInt("branchId"));
			}
		}
		if (opt != null) {
			if (opt.has("branch")) {
				setBranch(opt.getString("branch"));
			}
		}
		if (opt != null) {
			if (opt.has("obdDeviceId")) {
				setObdDeviceId(opt.getInt("obdDeviceId"));
			}
		}
		if (opt != null) {
			if (opt.has("channelId")) {
				setChannelId(opt.getInt("channelId"));
			}
		}
		// ////////////////////////////////////////////////////////
		if (opt != null) {
			if (opt.has("fuelAmount")) {
				setFuelAmount(opt.getDouble("fuelAmount"));
			}
		}
		if (opt != null) {
			if (opt.has("fuelConsumption100")) {
				setFuelConsumption100(opt.getDouble("fuelConsumption100"));
			}
		}
		if (opt != null) {
			if (opt.has("continueDistance")) {
				setContinueDistance(opt.getLong("continueDistance"));
			}
		}
		if (opt != null) {
			if (opt.has("distance")) {
				setDistance(opt.getDouble("distance"));
			}
		}

		if (opt != null) {
			if (opt.has("batteryRemain")) {
				setBatteryRemain(opt.getDouble("batteryRemain"));
			}
		}
		if (opt != null) {
			if (opt.has("troubleCodeNumber")) {
				setTroubleCodeNumber(opt.getInt("troubleCodeNumber"));
			}
		}
		if (opt != null) {
			if (opt.has("drivingScore")) {
				setDrivingScore(opt.getInt("drivingScore"));
			}
		}
		if (opt != null) {
			if (opt.has("drivingScoreRank")) {
				setDrivingScoreRank(opt.getDouble("drivingScoreRank"));
			}
		}
		if (opt != null) {
			if (opt.has("longitude")) {
				setLongitude(opt.getDouble("longitude"));
			}
		}
		if (opt != null) {
			if (opt.has("latitude")) {
				setLatitude(opt.getDouble("latitude"));
			}
		}
		if (opt != null) {
			if (opt.has("carStatus")) {
				setCarStatus(opt.getInt("carStatus"));
			}
		}
		if (opt != null) {
			if (opt.has("driving")) {
				setDriving(opt.getBoolean("driving"));
			}
		}
		if (opt != null) {
			if (opt.has("isOnline")) {
				setOnline(opt.getBoolean("isOnline"));
			}
		}
		if (opt != null) {
			if (opt.has("sn")) {
				setSn(opt.getInt("sn"));
			}
		}
		if (opt != null) {
			if (opt.has("isBind")) {
				setBind(opt.getBoolean("isBind"));
			}
		}
		if (opt != null) {
			if (opt.has("coolantTemperature")) {
				setCoolantTemperature(opt.getString("coolantTemperature"));
			}
		}
		if (opt != null) {
			if (opt.has("support")) {
				setSupport(opt.getInt("support"));
			}
		}
		if (SettingLoader.getVehicleId(XtrdApp.getAppContext()).equals(
				getVehicleId() + "")) {
			setChecked(true);
		}
	}
}
