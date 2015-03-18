package com.xtrd.obdcar.entity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.baidu.mapapi.model.LatLng;

/**
 * 加油站
 * 
 * @author Administrator
 * 
 */
public class GasStation implements Parcelable{
	private int stationId;
	private String name;
	private double longitude;
	private double latitude;
	private String address;
	private double distance;
	private int reportcount;
	private String time;
	private int attent;// 订阅状态
	private ArrayList<OilPrice> prices = new ArrayList<OilPrice>();
	private ArrayList<GasComment> comments = new ArrayList<GasComment>();

	private boolean checked;
	private LatLng latlng;

	
	public GasStation() {
	}

	public GasStation(Parcel in) {
		stationId = in.readInt();
		name = in.readString();
		longitude = in.readDouble();
		latitude = in.readDouble();
		address = in.readString();
		distance = in.readDouble();
		reportcount = in.readInt();
		time = in.readString();
		attent = in.readInt();
		
		Parcelable[] pricesArray = in.readParcelableArray(OilPrice.class.getClassLoader());
		if (pricesArray != null && pricesArray.length > 0) {
			this.prices = new ArrayList<OilPrice>();
			for (int i = 0; i < pricesArray.length; i++) {
				this.prices.add((OilPrice) pricesArray[i]);
			}
		}
		
		Parcelable[] commentsArray = in.readParcelableArray(GasComment.class.getClassLoader());
		if (commentsArray != null && commentsArray.length > 0) {
			this.comments = new ArrayList<GasComment>();
			for (int i = 0; i < commentsArray.length; i++) {
				this.comments.add((GasComment) commentsArray[i]);
			}
		}
	
	}
	
	public static final Parcelable.Creator<GasStation> CREATOR = new Creator<GasStation>() {
		@Override
		public GasStation[] newArray(int size) {
			return new GasStation[size];
		}

		@Override
		public GasStation createFromParcel(Parcel source) {
			return new GasStation(source);
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeInt(stationId);
		dest.writeString(name);
		dest.writeDouble(longitude);
		dest.writeDouble(latitude);
		dest.writeString(address);
		dest.writeDouble(distance);
		dest.writeInt(reportcount);
		dest.writeString(time);
		dest.writeInt(attent);
		
		OilPrice[] pricesArray = new OilPrice[prices == null ? 0 : prices.size()];
		for (int i = 0; i < pricesArray.length; i++) {
			pricesArray[i] = prices.get(i);
		}
		dest.writeParcelableArray(pricesArray, flags);
		
		GasComment[] commentsArray = new GasComment[comments == null ? 0 : comments.size()];
		for (int i = 0; i < commentsArray.length; i++) {
			commentsArray[i] = comments.get(i);
		}
		dest.writeParcelableArray(commentsArray, flags);
		
	}

	@Override
	public int describeContents() {
		return 0;
	}

	
	public int getStationId() {
		return stationId;
	}

	public void setStationId(int stationId) {
		this.stationId = stationId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public LatLng getLatlng() {
		return latlng;
	}

	public void setLatlng(LatLng latlng) {
		this.latlng = latlng;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public int getReportcount() {
		return reportcount;
	}

	public void setReportcount(int reportcount) {
		this.reportcount = reportcount;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public ArrayList<OilPrice> getPrices() {
		return prices;
	}

	public void setPrices(ArrayList<OilPrice> prices) {
		this.prices = prices;
	}

	public ArrayList<GasComment> getComments() {
		return comments;
	}

	public void setComments(ArrayList<GasComment> comments) {
		this.comments = comments;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public int getAttent() {
		return attent;
	}

	public void setAttent(int attent) {
		this.attent = attent;
	}

	public void parser(JSONObject json) {
		try {
			if (json != null) {
				if (json.has("stationId")) {
					setStationId(json.getInt("stationId"));
				}
				if (json.has("name")) {
					setName(json.getString("name"));
				}
				if (json.has("longitude")) {
					setLongitude(json.getDouble("longitude"));
				}
				if (json.has("latitude")) {
					setLatitude(json.getDouble("latitude"));
				}
				if (json.has("address")) {
					setAddress(json.getString("address"));
				}
				if (json.has("distance")) {
					setDistance(json.getDouble("distance"));
				}
				if (json.has("num")) {
					setReportcount(json.getInt("num"));
				}
				if (json.has("time")) {
					setTime(json.getString("time"));
				}
				if (json.has("attent")) {
					setAttent(json.getInt("attent"));
				}
				if (json.has("gasPriceList")) {
					parser(json.getJSONArray("gasPriceList"));
				}
				if (json.has("comments")) {
					parserComment(json.getJSONArray("comments"));
				}

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private void parserComment(JSONArray jsonArray) {
		if (jsonArray != null && jsonArray.length() > 0) {
			GasComment comment = null;
			for (int i = 0; i < jsonArray.length(); i++) {
				comment = new GasComment();
				comment.parser(jsonArray.optJSONObject(i));
				comments.add(comment);
			}
		}
	}

	private void parser(JSONArray jsonArray) {
		if (jsonArray != null && jsonArray.length() > 0) {
			OilPrice price = null;
			for (int i = 0; i < jsonArray.length(); i++) {
				price = new OilPrice();
				price.parser(jsonArray.optJSONObject(i));
				prices.add(price);
			}
		}
	}

}
