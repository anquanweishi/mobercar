package com.xtrd.obdcar.entity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Merchant implements Parcelable{

	private int id;
	private String name;
	private double longitude;
	private double latitude;
	private String address;
	private String imgUrl;
	private String phone;
	private String description;
	private String areaCode;
	private String distance;
	private int special;
	private int real;
	private int gold;
	private int score;
	private int isFav;
	private int isCom;
	private ArrayList<Privilege> list = new ArrayList<Privilege>();
	private ArrayList<String> items = new ArrayList<String>();
	private ArrayList<String> imgs = new ArrayList<String>();

	
	public Merchant() {
	}

	public Merchant(Parcel in) {
		id = in.readInt();
		name = in.readString();
		longitude = in.readDouble();
		latitude = in.readDouble();
		address = in.readString();
		imgUrl = in.readString();
		phone = in.readString();
		description = in.readString();
		areaCode = in.readString();
		distance = in.readString();
		special = in.readInt();
		real = in.readInt();
		gold = in.readInt();
		score = in.readInt();
		isFav = in.readInt();
		isCom = in.readInt();
		Parcelable[] commentArray = in.readParcelableArray(Privilege.class.getClassLoader());
		if (commentArray != null && commentArray.length > 0) {
			this.list = new ArrayList<Privilege>();
			for (int i = 0; i < commentArray.length; i++) {
				this.list.add((Privilege) commentArray[i]);
			}
		}
		items = new ArrayList<String>();
		in.readStringList(items);
		imgs = new ArrayList<String>();
		in.readStringList(imgs);
	
	}
	
	public static final Parcelable.Creator<Merchant> CREATOR = new Creator<Merchant>() {
		@Override
		public Merchant[] newArray(int size) {
			return new Merchant[size];
		}

		@Override
		public Merchant createFromParcel(Parcel source) {
			return new Merchant(source);
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeDouble(longitude);
		dest.writeDouble(latitude);
		dest.writeString(address);
		dest.writeString(imgUrl);
		dest.writeString(phone);
		dest.writeString(description);
		dest.writeString(areaCode);
		dest.writeString(distance);
		dest.writeInt(special);
		dest.writeInt(real);
		dest.writeInt(gold);
		dest.writeInt(score);
		dest.writeInt(isFav);
		dest.writeInt(isCom);
		
		Privilege[] commentArray = new Privilege[list == null ? 0 : list.size()];
		for (int i = 0; i < commentArray.length; i++) {
			commentArray[i] = list.get(i);
		}
		dest.writeParcelableArray(commentArray, flags);
		
		dest.writeStringList(items);
		dest.writeStringList(imgs);
	}

	@Override
	public int describeContents() {
		return 0;
	}


	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public int getSpecial() {
		return special;
	}

	public void setSpecial(int special) {
		this.special = special;
	}

	public int getReal() {
		return real;
	}

	public void setReal(int real) {
		this.real = real;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getIsFav() {
		return isFav;
	}

	public void setIsFav(int isFav) {
		this.isFav = isFav;
	}

	public int getIsCom() {
		return isCom;
	}

	public void setIsCom(int isCom) {
		this.isCom = isCom;
	}

	public ArrayList<Privilege> getList() {
		return list;
	}

	public void setList(ArrayList<Privilege> list) {
		this.list = list;
	}

	public ArrayList<String> getItems() {
		return items;
	}

	public void setItems(ArrayList<String> items) {
		this.items = items;
	}

	public ArrayList<String> getImgs() {
		return imgs;
	}

	public void setImgs(ArrayList<String> imgs) {
		this.imgs = imgs;
	}

	public void parser(JSONObject json) {
		try {
			if (json != null) {
				if (json.has("id")) {
					setId(json.getInt("id"));
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
				if (json.has("imgUrl")) {
					setImgUrl(json.getString("imgUrl"));
				}
				if (json.has("description")) {
					setDescription(json.getString("description"));
				}
				if (json.has("phone")) {
					setPhone(json.getString("phone"));
				}
				if (json.has("areaCode")) {
					setAreaCode(json.getString("areaCode"));
				}
				if (json.has("distance")) {
					setDistance(json.getString("distance"));
				}
				if (json.has("special")) {
					setSpecial(json.getInt("special"));
				}
				if (json.has("real")) {
					setReal(json.getInt("real"));
				}
				if (json.has("gold")) {
					setGold(json.getInt("gold"));
				}
				if (json.has("score")) {
					setScore(json.getInt("score"));
				}
				if (json.has("isFav")) {
					setIsFav(json.getInt("isFav"));
				}
				if (json.has("discounts")) {
					parserPrivilege(json.getJSONArray("discounts"));
				}

				if (json.has("imgUrls")) {
					setImgs(processStrList(json.getJSONArray("imgUrls")));
				}
				if (json.has("items")) {
					setItems(processStrList(json.getJSONArray("items")));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private ArrayList<String> processStrList(JSONArray jsonArray) {
		ArrayList<String> list = new ArrayList<String>();
		if (jsonArray != null && jsonArray.length() > 0) {
			for (int i = 0; i < jsonArray.length(); i++) {
				list.add(jsonArray.optString(i));
			}
		}
		return list;
	}

	private void parserPrivilege(JSONArray jsonArray) throws JSONException {
		if (jsonArray != null && jsonArray.length() > 0) {
			Privilege item = null;
			for (int i = 0; i < jsonArray.length(); i++) {
				item = new Privilege();
				item.parser(jsonArray.optJSONObject(i));
				list.add(item);
			}
			setList(list);
		}

	}

}
