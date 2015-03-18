package com.xtrd.obdcar.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class UserEntity {
	
	private String userId;
	private String userName;
	private String type;
	private String nickName;
	private String telephone;
	private String email;
	private boolean valid;
	private int status;
	private String areaCode;
	private String areaName;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
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
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	
	public void parser(JSONObject opt) throws JSONException {
		if(opt!=null) {
			if(opt.has("userId")) {
				setUserId(opt.getString("userId"));
			}
			if(opt.has("userName")) {
				setUserName(opt.getString("userName"));
			}
			if(opt.has("type")) {
				setType(opt.getString("type"));
			}
			if(opt.has("nickName")) {
				setNickName(opt.getString("nickName"));
			}
			if(opt.has("telephone")) {
				setTelephone(opt.getString("telephone"));
			}
			if(opt.has("email")) {
				setEmail(opt.getString("email"));
			}
			if(opt.has("valid")) {
				setValid(opt.getBoolean("valid"));
			}
			if(opt.has("status")) {
				setStatus(opt.getInt("status"));
			}
			if(opt.has("areaCode")) {
				setAreaCode(opt.getString("areaCode"));
			}
			if(opt.has("areaName")) {
				setAreaName(opt.getString("areaName"));
			}
		}
	}
	

}
