package com.xtrd.obdcar.entity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 用户
 * 
 * @author Administrator
 * 
 */
public class CarUser {
	private double val;
	private String unit;
	private String nick;
	private String vid;
	private String mid;
	private String city = "";
	// 个人详情
	private int branchId;
	private String ser = "";
	private String bra = "";
	private double fuelAvg;// 平均油耗
	private int modelId;// 是车型id;
	private double distance;// 车总里程数
	private int my;// 小希盒子里程数
	private int sumDis;// 本周/月总里程数
	private int fsort;// 平均油耗排名
	private int dsort;// 驾驶技能排名

	public double getVal() {
		return val;
	}

	public void setVal(double val) {
		this.val = val;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getVid() {
		return vid;
	}

	public void setVid(String vid) {
		this.vid = vid;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public int getBranchId() {
		return branchId;
	}

	public void setBranchId(int branchId) {
		this.branchId = branchId;
	}

	public String getSer() {
		return ser;
	}

	public void setSer(String ser) {
		this.ser = ser;
	}

	public String getBra() {
		return bra;
	}

	public void setBra(String bra) {
		this.bra = bra;
	}

	public double getFuelAvg() {
		return fuelAvg;
	}

	public void setFuelAvg(double fuelAvg) {
		this.fuelAvg = fuelAvg;
	}

	public int getModelId() {
		return modelId;
	}

	public void setModelId(int modelId) {
		this.modelId = modelId;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public int getMy() {
		return my;
	}

	public void setMy(int my) {
		this.my = my;
	}

	public int getSumDis() {
		return sumDis;
	}

	public void setSumDis(int sumDis) {
		this.sumDis = sumDis;
	}

	public int getFsort() {
		return fsort;
	}

	public void setFsort(int fsort) {
		this.fsort = fsort;
	}

	public int getDsort() {
		return dsort;
	}

	public void setDsort(int dsort) {
		this.dsort = dsort;
	}

	public void parser(JSONObject json) {
		try {
			if (json != null) {
				if (json.has("val")) {
					setVal(json.getDouble("val"));
				}
				if (json.has("unit")) {
					setUnit(json.getString("unit"));
				}
				if (json.has("nick")) {
					setNick(json.getString("nick"));
				}
				if (json.has("vid")) {
					setVid(json.getString("vid"));
				}
				if (json.has("mid")) {
					setMid(json.getString("mid"));
				}
				if (json.has("city")) {//风云榜
					setCity(json.getString("city"));
				}
				if (json.has("one")) {
					setCity(json.getString("one"));
				}
				if (json.has("two")) {
					setCity(getCity() + json.getString("two"));
				}

				if (json.has("bid")) {
					setBranchId(json.getInt("bid"));
				}
				// 个人详情
				if (json.has("sumDis")) {
					setSumDis(json.getInt("sumDis"));
				}
				if (json.has("modelId")) {
					setModelId(json.getInt("modelId"));
				}
				if (json.has("fuelAvg")) {
					setFuelAvg(json.getDouble("fuelAvg"));
				}
				if (json.has("distance")) {
					setDistance(json.getDouble("distance"));
				}
				if (json.has("fsort")) {
					setFsort(json.getInt("fsort"));
				}
				if (json.has("dsort")) {
					setDsort(json.getInt("dsort"));
				}
				if (json.has("my")) {
					setMy(json.getInt("my"));
				}
				if (json.has("ser")) {
					setSer(json.getString("ser"));
				}
				if (json.has("bra")) {
					setBra(json.getString("bra"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
