package com.xtrd.obdcar.entity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 热线
 * @author start
 *
 */
public class HotLine {
	private String name;
	private String telephone;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	
	public void parser(JSONObject opt) throws JSONException {
		if(opt!=null) {
			if(opt.has("name")) {
				setName(opt.getString("name"));
			}
			if(opt.has("telephone")) {
				setTelephone(opt.getString("telephone"));
			}
		}
	}
	

}
