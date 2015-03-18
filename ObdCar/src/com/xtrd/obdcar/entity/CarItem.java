package com.xtrd.obdcar.entity;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 车况页面 下部按钮
 * 
 * @author Administrator
 * 
 */
public class CarItem implements Parcelable{

	private String code;
	private String name;
	private String description;
	private String suggest;

	public CarItem() {
	}

	public CarItem(Parcel in) {
		code = in.readString();
		name = in.readString();
		description = in.readString();
		suggest = in.readString();
	}
	
	public static final Parcelable.Creator<CarItem> CREATOR = new Creator<CarItem>() {
		@Override
		public CarItem[] newArray(int size) {
			return new CarItem[size];
		}

		@Override
		public CarItem createFromParcel(Parcel source) {
			return new CarItem(source);
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeString(code);
		dest.writeString(name);
		dest.writeString(description);
		dest.writeString(suggest);
	}

	@Override
	public int describeContents() {
		return 0;
	}



	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSuggest() {
		return suggest;
	}

	public void setSuggest(String suggest) {
		this.suggest = suggest;
	}

	public void parser(JSONObject json) {
		try {
			if (json != null) {
				
				if (json.has("code")) {
					setCode(json.getString("code"));
				}
				if (json.has("name")) {
					setName(json.getString("name"));
				}
				if (json.has("description")) {
					setDescription(json.getString("description"));
				}
				if (json.has("suggest")) {
					setSuggest(json.getString("suggest"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
