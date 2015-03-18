package com.xtrd.obdcar.entity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 速度时间 报表
 * @author start
 *
 */
public class Velocity {
	
	private String time;
	private double velocity;
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public double getVelocity() {
		return velocity;
	}
	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}
	
	public void parser(JSONObject opt) throws JSONException {
		if(opt!=null) {
			if(opt.has("time")) {
				setTime(opt.getString("time"));
			}
			if(opt.has("velocity")) {
				setVelocity(opt.getDouble("velocity"));
			}
		}
	}
	
	

}
