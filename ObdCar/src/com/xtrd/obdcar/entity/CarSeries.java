package com.xtrd.obdcar.entity;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 车型
 * 
 * @author start
 * 
 */
public class CarSeries {
	private int seriesId;
	private int manufacturerId;
	private int branchId;
	private String category;
	private String name;
	private String technology;
	private String vehicleBranch;
	private int hasChildren;
	private ArrayList<CarModel> list = new ArrayList<CarModel>();

	public int getSeriesId() {
		return seriesId;
	}

	public void setSeriesId(int seriesId) {
		this.seriesId = seriesId;
	}

	public int getManufacturerId() {
		return manufacturerId;
	}

	public void setManufacturerId(int manufacturerId) {
		this.manufacturerId = manufacturerId;
	}

	public int getBranchId() {
		return branchId;
	}

	public void setBranchId(int branchId) {
		this.branchId = branchId;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTechnology() {
		return technology;
	}

	public void setTechnology(String technology) {
		this.technology = technology;
	}

	public String getVehicleBranch() {
		return vehicleBranch;
	}

	public void setVehicleBranch(String vehicleBranch) {
		this.vehicleBranch = vehicleBranch;
	}

	public int getHasChildren() {
		return hasChildren;
	}

	public void setHasChildren(int hasChildren) {
		this.hasChildren = hasChildren;
	}

	public ArrayList<CarModel> getList() {
		return list;
	}

	public void setList(ArrayList<CarModel> list) {
		this.list = list;
	}

	public void parser(JSONObject opt) throws JSONException {
		if (opt != null) {
			if (opt.has("seriesId")) {
				setSeriesId(opt.getInt("seriesId"));
			}
			if (opt.has("manufacturerId")) {
				setManufacturerId(opt.getInt("manufacturerId"));
			}
			if (opt.has("branchId")) {
				setBranchId(opt.getInt("branchId"));
			}
			if (opt.has("category")) {
				setCategory(opt.getString("category"));
			}
			if (opt.has("name")) {
				setName(opt.getString("name"));
			}
			if (opt.has("technology")) {
				setTechnology(opt.getString("technology"));
			}
			if (opt.has("vehicleBranch")) {
				setVehicleBranch(opt.getString("vehicleBranch"));
			}
			if (opt.has("hasChildren")) {
				setHasChildren(opt.getInt("hasChildren"));
			}
		}
	}

}
