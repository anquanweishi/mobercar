package com.xtrd.obdcar.entity;

/**
 * 车辆
 * 
 * @author start
 * 
 */
public class CarEntity {
	private int vehicleId;
	private String drivingAreaCode;
	private String drivingArea;
	private String plateNumber = "";
	private String vin;
	private String engineNumber;
	private int defaultFuelTypeId;
	private int ownerId;
	private int modelId;
	private String year;
	private String series = "";
	private int branchId;
	private String branch = "";
	private int obdDeviceId;
	private int channelId;
	private int support;

	public int getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(int vehicleId) {
		this.vehicleId = vehicleId;
	}

	public String getDrivingAreaCode() {
		return drivingAreaCode;
	}

	public void setDrivingAreaCode(String drivingAreaCode) {
		this.drivingAreaCode = drivingAreaCode;
	}

	public String getDrivingArea() {
		return drivingArea;
	}

	public void setDrivingArea(String drivingArea) {
		this.drivingArea = drivingArea;
	}

	public String getPlateNumber() {
		return plateNumber;
	}

	public void setPlateNumber(String plateNumber) {
		this.plateNumber = plateNumber;
	}

	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	public String getEngineNumber() {
		return engineNumber;
	}

	public void setEngineNumber(String engineNumber) {
		this.engineNumber = engineNumber;
	}

	public int getDefaultFuelTypeId() {
		return defaultFuelTypeId;
	}

	public void setDefaultFuelTypeId(int defaultFuelTypeId) {
		this.defaultFuelTypeId = defaultFuelTypeId;
	}

	public int getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}

	public int getModelId() {
		return modelId;
	}

	public void setModelId(int modelId) {
		this.modelId = modelId;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public int getBranchId() {
		return branchId;
	}

	public void setBranchId(int branchId) {
		this.branchId = branchId;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public int getObdDeviceId() {
		return obdDeviceId;
	}

	public void setObdDeviceId(int obdDeviceId) {
		this.obdDeviceId = obdDeviceId;
	}

	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}

	public int getSupport() {
		return support;
	}

	public void setSupport(int support) {
		this.support = support;
	}

}
