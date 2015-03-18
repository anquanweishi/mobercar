package com.xtrd.obdcar.entity;

import android.app.Activity;

/**
 * 首页导航
 * 
 * @author Administrator
 * 
 */
public class BarItem {

	private int img;
	private String title;
	private String desc;
	private Class<Activity> dest;

	public BarItem() {
	}

	public BarItem(int img, String title, String desc) {
		this.img = img;
		this.title = title;
		this.desc = desc;
	}

	public int getImg() {
		return img;
	}

	public void setImg(int img) {
		this.img = img;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Class<Activity> getDest() {
		return dest;
	}

	public void setDest(Class dest) {
		this.dest = dest;
	}

}
