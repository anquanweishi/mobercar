package com.xtrd.obdcar.entity;

/**
 * 制造商
 * 
 * @author start
 * 
 */
public class Manufacturer {

	private int manufacturerId;
	private String name;
	private String shortName;

	public int getManufacturerId() {
		return manufacturerId;
	}

	public void setManufacturerId(int manufacturerId) {
		this.manufacturerId = manufacturerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

}
