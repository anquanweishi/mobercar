package com.xtrd.obdcar.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;

public class OffLine extends MKOLSearchRecord {
	/**
	 * 下载的进度
	 */
	private int progress;

	private Flag flag = Flag.NO_STATUS;

	private int type;

	private ArrayList<OffLine> list = new ArrayList<OffLine>();
	private HashMap<String,OffLine> map = new HashMap<String,OffLine>();

	/**
	 * 下载的状态：无状态，暂停，正在下载
	 * 
	 * @author zhy
	 * 
	 */
	public enum Flag {
		NO_STATUS, PAUSE, DOWNLOADING
	}

	public OffLine() {
	}

	public OffLine(String cityName, int cityCode) {
		this.cityName = cityName;
		this.cityID = cityCode;
	}

	public OffLine(String cityName, int cityCode, int size,int cityType) {
		this.size = size;
		this.cityName = cityName;
		this.cityID = cityCode;
		this.cityType = cityType;
	}

	public OffLine(String cityName, int cityCode, int size,int cityType,int progress) {
		this.cityName = cityName;
		this.cityID = cityCode;
		this.size = size;
		this.cityType = cityType;
		this.progress = progress;
	}

	public Flag getFlag() {
		return flag;
	}

	public void setFlag(Flag flag) {
		this.flag = flag;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public ArrayList<OffLine> getList() {
		list.clear();
		if(map.size()>0) {
			Set<String> set = map.keySet();
			Iterator<String> iterator = set.iterator();
			while(iterator.hasNext()) {
				list.add(map.get(iterator.next()));
			}
		}
		return list;
	}

	
	public void addToMap(OffLine line) {
		map.put(line.cityName, line);
	}

}
