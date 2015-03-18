package com.xtrd.obdcar.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class PlateEntity {

	private int id;
	private String car;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCar() {
		return car;
	}

	public void setCar(String car) {
		this.car = car;
	}
	
	public void parser(JSONObject opt) throws JSONException {
		if(opt!=null) {
			if(opt.has("id")) {
				setId(opt.getInt("id"));
			}
			if(opt.has("car")) {
				setCar(opt.getString("car"));
			}
		}
	}

}
