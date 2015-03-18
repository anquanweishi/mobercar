package com.xtrd.obdcar.entity;

import com.baidu.mapapi.search.core.PoiInfo;

public class MyPoiInfo extends PoiInfo {

	private boolean checked;
	private String distance;

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

}
