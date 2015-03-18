package com.xtrd.obdcar.entity;

/**
 * 限号和油价
 * 
 * @author Administrator
 * 
 */
public class LimitAndOil {
	private String title;
	private String oneTitle;
	private String[] oneValue;
	private int oneRes;
	private String twoTitle;
	private String[] twoValue;
	private int twoRes;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOneTitle() {
		return oneTitle;
	}

	public void setOneTitle(String oneTitle) {
		this.oneTitle = oneTitle;
	}

	public String[] getOneValue() {
		return oneValue;
	}

	public void setOneValue(String[] oneValue) {
		this.oneValue = oneValue;
	}

	public int getOneRes() {
		return oneRes;
	}

	public void setOneRes(int oneRes) {
		this.oneRes = oneRes;
	}

	public String getTwoTitle() {
		return twoTitle;
	}

	public void setTwoTitle(String twoTitle) {
		this.twoTitle = twoTitle;
	}

	public String[] getTwoValue() {
		return twoValue;
	}

	public void setTwoValue(String[] twoValue) {
		this.twoValue = twoValue;
	}

	public int getTwoRes() {
		return twoRes;
	}

	public void setTwoRes(int twoRes) {
		this.twoRes = twoRes;
	}

}
