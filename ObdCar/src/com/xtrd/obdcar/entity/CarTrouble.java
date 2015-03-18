package com.xtrd.obdcar.entity;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class CarTrouble implements Parcelable {
	private String name;
	private String pid;
	private String value;
	private String unit;
	// ////////////////////
	private int alarmId;
	private int vehicleId;
	private int tripId;
	private String type;
	private String gpsTime;
	private String createTime;
	private String obdDataId;
	private TroubleDetail detail = new TroubleDetail();

	public CarTrouble() {
	}

	public CarTrouble(Parcel in) {
		name = in.readString();
		pid = in.readString();
		value = in.readString();
		unit = in.readString();
		alarmId = in.readInt();
		vehicleId = in.readInt();
		tripId = in.readInt();
		type = in.readString();
		gpsTime = in.readString();
		createTime = in.readString();
		obdDataId = in.readString();
		detail = in.readParcelable(TroubleDetail.class.getClassLoader());
	}

	public static final Parcelable.Creator<CarTrouble> CREATOR = new Creator<CarTrouble>() {
		@Override
		public CarTrouble[] newArray(int size) {
			return new CarTrouble[size];
		}

		@Override
		public CarTrouble createFromParcel(Parcel source) {
			return new CarTrouble(source);
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(pid);
		dest.writeString(value);
		dest.writeString(unit);
		dest.writeInt(alarmId);
		dest.writeInt(vehicleId);
		dest.writeInt(tripId);
		dest.writeString(type);
		dest.writeString(gpsTime);
		dest.writeString(createTime);
		dest.writeString(obdDataId);
		dest.writeParcelable(detail,flags);
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

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public int getAlarmId() {
		return alarmId;
	}

	public void setAlarmId(int alarmId) {
		this.alarmId = alarmId;
	}

	public int getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(int vehicleId) {
		this.vehicleId = vehicleId;
	}

	public int getTripId() {
		return tripId;
	}

	public void setTripId(int tripId) {
		this.tripId = tripId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getGpsTime() {
		return gpsTime;
	}

	public void setGpsTime(String gpsTime) {
		this.gpsTime = gpsTime;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getObdDataId() {
		return obdDataId;
	}

	public void setObdDataId(String obdDataId) {
		this.obdDataId = obdDataId;
	}

	public TroubleDetail getDetail() {
		return detail;
	}

	public void setDetail(TroubleDetail detail) {
		this.detail = detail;
	}

	public void parser(JSONObject opt) throws JSONException {
		if (opt != null) {
			if (opt.has("name")) {
				setName(opt.getString("name"));
			}
			if (opt.has("pid")) {
				setPid(opt.getString("pid"));
			}
			if (opt.has("value")) {
				setValue(opt.getString("value"));
			}
			if (opt.has("unit")) {
				setUnit(opt.getString("unit"));
			}
			if (opt.has("alarmId")) {
				setAlarmId(opt.getInt("alarmId"));
			}

			if (opt.has("vehicleId")) {
				setVehicleId(opt.getInt("vehicleId"));
			}
			if (opt.has("tripId")) {
				setTripId(opt.getInt("tripId"));
			}
			if (opt.has("type")) {
				setType(opt.getString("type"));
			}
			if (opt.has("gpsTime")) {
				setGpsTime(opt.getString("gpsTime"));
			}
			if (opt.has("createTime")) {
				setCreateTime(opt.getString("createTime"));
			}
			if (opt.has("obdDataId")) {
				setObdDataId(opt.getString("obdDataId"));
			}
			if (opt.has("obdFault")) {
				detail.parser(opt.getJSONObject("obdFault"));
				setDetail(detail);
			}
		}
	}
}
