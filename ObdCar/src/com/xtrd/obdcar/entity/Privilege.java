package com.xtrd.obdcar.entity;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 优惠
 */
public class Privilege implements Parcelable {
	private int id;
	private String name;
	private int type;// 0普通优惠 1专享优惠
	private String merchantName;
	private String imgUrl;
	private String owner;
	private String description;
	private String startTime;
	private String endTime;

	public Privilege() {
	}

	public Privilege(Parcel in) {
		id = in.readInt();
		name = in.readString();
		type = in.readInt();
		merchantName = in.readString();
		imgUrl = in.readString();
		owner = in.readString();
		description = in.readString();
		startTime = in.readString();
		endTime = in.readString();
	}

	public static final Parcelable.Creator<Privilege> CREATOR = new Creator<Privilege>() {
		@Override
		public Privilege[] newArray(int size) {
			return new Privilege[size];
		}

		@Override
		public Privilege createFromParcel(Parcel source) {
			return new Privilege(source);
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeInt(type);
		dest.writeString(merchantName);
		dest.writeString(imgUrl);
		dest.writeString(owner);
		dest.writeString(description);
		dest.writeString(startTime);
		dest.writeString(endTime);
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public void parser(JSONObject json) throws JSONException {
		if (json != null) {
			if (json.has("name")) {
				setName(json.getString("name"));
			}
			if (json.has("id")) {
				setId(json.getInt("id"));
			}
			if (json.has("type")) {
				setType(json.getInt("type"));
			}
			if (json.has("merchantName")) {
				setMerchantName(json.getString("merchantName"));
			}
			if (json.has("imgUrl")) {
				setImgUrl(json.getString("imgUrl"));
			}
			if (json.has("owner")) {
				setOwner(json.getString("owner"));
			}
			if (json.has("description")) {
				setDescription(json.getString("description"));
			}
			if (json.has("startTime")) {
				setStartTime(json.getString("startTime"));
			}
			if (json.has("endTime")) {
				setEndTime(json.getString("endTime"));
			}
		}
	}

}
