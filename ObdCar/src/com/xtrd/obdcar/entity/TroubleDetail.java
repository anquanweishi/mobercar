package com.xtrd.obdcar.entity;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class TroubleDetail implements Parcelable {
	private String faultCode;
	private String name;
	private String description;
	private String grade;
	
	public TroubleDetail() {
	}

	public TroubleDetail(Parcel in) {
		faultCode = in.readString();
		name = in.readString();
		description = in.readString();
		grade = in.readString();
	}

	public static final Parcelable.Creator<TroubleDetail> CREATOR = new Creator<TroubleDetail>() {
		@Override
		public TroubleDetail[] newArray(int size) {
			return new TroubleDetail[size];
		}

		@Override
		public TroubleDetail createFromParcel(Parcel source) {
			return new TroubleDetail(source);
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(faultCode);
		dest.writeString(name);
		dest.writeString(description);
		dest.writeString(grade);
	}

	@Override
	public int describeContents() {
		return 0;
	}


	public String getFaultCode() {
		return faultCode;
	}

	public void setFaultCode(String faultCode) {
		this.faultCode = faultCode;
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

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}
	public void parser(JSONObject obj) throws JSONException {
		if(obj!=null) {
			if(obj.has("faultCode")) {
				setFaultCode(obj.getString("faultCode"));
			}
			if(obj.has("name")) {
				setName(obj.getString("name"));
			}
			if(obj.has("description")) {
				setDescription(obj.getString("description"));
			}
			if(obj.has("grade")) {
				setGrade(obj.getString("grade"));
			}
		}
		
	}

}
