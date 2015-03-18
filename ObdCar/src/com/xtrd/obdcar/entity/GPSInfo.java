package com.xtrd.obdcar.entity;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class GPSInfo implements Parcelable {
	private int dataId;
	private int vehicleId;
	private int tripId;
	private double longitude;
	private double latitude;
	private int altitude;
	private int orientation;
	private int velocity;
	private String status;
	private String gpsTime;
	
	public GPSInfo() {
	}

	public GPSInfo(Parcel in) {
		dataId = in.readInt();
		vehicleId = in.readInt();
		tripId = in.readInt();
		longitude = in.readDouble();
		latitude = in.readDouble();
		altitude = in.readInt();
		orientation = in.readInt();
		velocity = in.readInt();
		status = in.readString();
		gpsTime = in.readString();
		
	}
	
	public static final Parcelable.Creator<GPSInfo> CREATOR = new Creator<GPSInfo>() {
		@Override
		public GPSInfo[] newArray(int size) {
			return new GPSInfo[size];
		}

		@Override
		public GPSInfo createFromParcel(Parcel source) {
			return new GPSInfo(source);
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(dataId);
		dest.writeInt(vehicleId);
		dest.writeInt(tripId);
		dest.writeDouble(longitude);
		dest.writeDouble(latitude);
		dest.writeInt(altitude);
		dest.writeInt(orientation);
		dest.writeInt(velocity);
		dest.writeString(status);
		dest.writeString(gpsTime);
	}

	@Override
	public int describeContents() {
		return 0;
	}


	public int getDataId() {
		return dataId;
	}

	public void setDataId(int dataId) {
		this.dataId = dataId;
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

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public int getAltitude() {
		return altitude;
	}

	public void setAltitude(int altitude) {
		this.altitude = altitude;
	}

	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	public int getVelocity() {
		return velocity;
	}

	public void setVelocity(int velocity) {
		this.velocity = velocity;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getGpsTime() {
		return gpsTime;
	}

	public void setGpsTime(String gpsTime) {
		this.gpsTime = gpsTime;
	}
	
	public void parser(JSONObject opt) throws JSONException {
		if(opt!=null) {
			if(opt.has("dataId")) {
				setDataId(opt.getInt("dataId"));
			}
			if(opt.has("gpsDataId")) {
				setDataId(opt.getInt("gpsDataId"));
			}
			if(opt.has("vehicleId")) {
				setVehicleId(opt.getInt("vehicleId"));
			}
//			if(opt.has("tripId")) {
//				setTripId(opt.getInt("tripId"));
//			}
			if(opt.has("longitude")) {
				setLongitude(opt.getDouble("longitude"));
			}
			if(opt.has("latitude")) {
				setLatitude(opt.getDouble("latitude"));
			}
			if(opt.has("altitude")) {
				setAltitude(opt.getInt("altitude"));
			}
			if(opt.has("orientation")) {
				setOrientation(opt.getInt("orientation"));
			}
			if(opt.has("velocity")) {
				setVelocity(opt.getInt("velocity"));
			}
			if(opt.has("status")) {
				setStatus(opt.getString("status"));
			}
			if(opt.has("gpsTime")) {
				setGpsTime(opt.getString("gpsTime"));
			}
		}
	}

}
