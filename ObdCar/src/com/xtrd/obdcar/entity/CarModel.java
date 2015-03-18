package com.xtrd.obdcar.entity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 车型
 * 
 * @author start
 * 
 */
public class CarModel {

	private int modelId;
	private int seriesId;
	private String year;
	private int displacement;
	private String name;
	private int tankCapacity;
	private String vehicleSeries;
	private int support;// 0不支持 1支持

	public int getModelId() {
		return modelId;
	}

	public void setModelId(int modelId) {
		this.modelId = modelId;
	}

	public int getSeriesId() {
		return seriesId;
	}

	public void setSeriesId(int seriesId) {
		this.seriesId = seriesId;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public int getDisplacement() {
		return displacement;
	}

	public void setDisplacement(int displacement) {
		this.displacement = displacement;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTankCapacity() {
		return tankCapacity;
	}

	public void setTankCapacity(int tankCapacity) {
		this.tankCapacity = tankCapacity;
	}

	public String getVehicleSeries() {
		return vehicleSeries;
	}

	public void setVehicleSeries(String vehicleSeries) {
		this.vehicleSeries = vehicleSeries;
	}

	public int getSupport() {
		return support;
	}

	public void setSupport(int support) {
		this.support = support;
	}

	public void parser(JSONObject opt) throws JSONException {
		if (opt != null) {
			if (opt.has("modelId")) {
				setModelId(opt.getInt("modelId"));
			}
			if (opt.has("seriesId")) {
				setSeriesId(opt.getInt("seriesId"));
			}
			if (opt.has("year")) {
				setYear(opt.getString("year"));
			}
			if (opt.has("displacement")) {
				setDisplacement(opt.getInt("displacement"));
			}
			if (opt.has("name")) {
				setName(opt.getString("name"));
			}
			if (opt.has("tankCapacity")) {
				setTankCapacity(opt.getInt("tankCapacity"));
			}
			if (opt.has("vehicleSeries")) {
				setVehicleSeries(opt.getString("vehicleSeries"));
			}
			if (opt.has("support")) {
				setSupport(opt.getInt("support"));
			}
		}
	}

}
