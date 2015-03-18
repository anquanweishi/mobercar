package com.xtrd.obdcar.entity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 收藏
 * 
 * @author Administrator
 * 
 */
public class Favorite {
	private int id;
	private String name;
	private double longitude;
	private double latitude;
	private String address;
	private String imgUrl;
	private String phone;
	private String description;
	private String areaCode;
	private double distance;
	private int type;
	private int special;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	

	public int getSpecial() {
		return special;
	}

	public void setSpecial(int special) {
		this.special = special;
	}

	public void parser(JSONObject json) {
		try {
			if(json!=null) {
				if(json.has("id")) {
					setId(json.getInt("id"));
				}
				if(json.has("name")) {
					setName(json.getString("name"));
				}
				if(json.has("longitude")) {
					setLongitude(json.getDouble("longitude"));
				}
				if(json.has("latitude")) {
					setLatitude(json.getDouble("latitude"));
				}
				if(json.has("address")) {
					setAddress(json.getString("address"));
				}
				if(json.has("imgUrl")) {
					setImgUrl(json.getString("imgUrl"));
				}
				if(json.has("phone")) {
					setPhone(json.getString("phone"));
				}
				if(json.has("description")) {
					setDescription(json.getString("description"));
				}
				if(json.has("areaCode")) {
					setAreaCode(json.getString("areaCode"));
				}
				if(json.has("distance")) {
					setDistance(json.getDouble("distance"));
				}
				if(json.has("type")) {
					setType(json.getInt("type"));
				}
				if(json.has("special")) {
					setSpecial(json.getInt("special"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
