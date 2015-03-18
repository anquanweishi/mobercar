package com.xtrd.obdcar.entity;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 油价
 * 
 * @author Administrator
 * 
 */
public class OilPrice implements Parcelable{

	private String name;
	private double price;

	
	public OilPrice() {
	}

	public OilPrice(Parcel in) {
		name = in.readString();
		price = in.readDouble();
	}
	
	public static final Parcelable.Creator<OilPrice> CREATOR = new Creator<OilPrice>() {
		@Override
		public OilPrice[] newArray(int size) {
			return new OilPrice[size];
		}

		@Override
		public OilPrice createFromParcel(Parcel source) {
			return new OilPrice(source);
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeDouble(price);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void parser(JSONObject json) {
		try {
			if (json != null) {
				if (json.has("name")) {
					setName(json.getString("name"));
				}
				if (json.has("price")) {
					setPrice(json.getDouble("price"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
