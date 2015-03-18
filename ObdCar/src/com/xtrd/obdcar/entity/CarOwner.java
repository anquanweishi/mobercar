package com.xtrd.obdcar.entity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 车主信息
 * 
 * @author start
 * 
 */
public class CarOwner {

	private int ownerId;
	private String name;
	private String idType;
	private String idNumber;
	private String telephone;
	private String email;
	private String address;
	private int branchId;
	private int seriesId;
	private int modelId;

	public int getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}

	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getBranchId() {
		return branchId;
	}

	public void setBranchId(int branchId) {
		this.branchId = branchId;
	}

	public int getSeriesId() {
		return seriesId;
	}

	public void setSeriesId(int seriesId) {
		this.seriesId = seriesId;
	}

	public int getModelId() {
		return modelId;
	}

	public void setModelId(int modelId) {
		this.modelId = modelId;
	}

	public void parser(JSONObject opt) throws JSONException {
		if(opt!=null) {
			if(opt.has("ownerId")) {
				setOwnerId(opt.getInt("ownerId"));
			}
			if(opt.has("name")) {
				setName(opt.getString("name"));
			}
			if(opt.has("idType")) {
				setIdType(opt.getString("idType"));
			}
			if(opt.has("idNumber")) {
				setIdNumber(opt.getString("idNumber"));
			}
			if(opt.has("telephone")) {
				setTelephone(opt.getString("telephone"));
			}
			if(opt.has("email")) {
				setEmail(opt.getString("email"));
			}
			if(opt.has("address")) {
				setAddress(opt.getString("address"));
			}
			if(opt.has("branchId")) {
				setBranchId(opt.getInt("branchId"));
			}
			if(opt.has("seriesId")) {
				setSeriesId(opt.getInt("seriesId"));
			}
			if(opt.has("modelId")) {
				setModelId(opt.getInt("modelId"));
			}
		}
	}
}
