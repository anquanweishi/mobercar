package com.xtrd.obdcar.entity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 加油站评论
 * 
 * @author Administrator
 * 
 */
public class GasComment implements Parcelable{

	private int id;
	private String content;
	private String userName;
	private String userId;
	private String createTime;
	private ArrayList<OilPrice> prices = new ArrayList<OilPrice>();

	
	public GasComment() {
	}

	public GasComment(Parcel in) {
		id = in.readInt();
		content = in.readString();
		userName = in.readString();
		userId = in.readString();
		createTime = in.readString();
		Parcelable[] pricesArray = in.readParcelableArray(OilPrice.class.getClassLoader());
		if (pricesArray != null && pricesArray.length > 0) {
			this.prices = new ArrayList<OilPrice>();
			for (int i = 0; i < pricesArray.length; i++) {
				this.prices.add((OilPrice) pricesArray[i]);
			}
		}
	}
	
	public static final Parcelable.Creator<GasComment> CREATOR = new Creator<GasComment>() {
		@Override
		public GasComment[] newArray(int size) {
			return new GasComment[size];
		}

		@Override
		public GasComment createFromParcel(Parcel source) {
			return new GasComment(source);
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(content);
		dest.writeString(userName);
		dest.writeString(userId);
		dest.writeString(createTime);
		OilPrice[] pricesArray = new OilPrice[prices == null ? 0 : prices.size()];
		for (int i = 0; i < pricesArray.length; i++) {
			pricesArray[i] = prices.get(i);
		}
		dest.writeParcelableArray(pricesArray, flags);
		
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public ArrayList<OilPrice> getPrices() {
		return prices;
	}

	public void setPrices(ArrayList<OilPrice> prices) {
		this.prices = prices;
	}

	public void parser(JSONObject json) {
		try {
			if (json != null) {
				if (json.has("id")) {
					setId(json.getInt("id"));
				}
				if (json.has("content")) {
					setContent(json.getString("content"));
				}
				if (json.has("userId")) {
					setUserId(json.getString("userId"));
				}
				if (json.has("nickName")) {
					setUserName(json.getString("nickName"));
				}
				if (json.has("createTime")) {
					setCreateTime(json.getString("createTime"));
				}
				if (json.has("gasPriceList")) {
					parser(json.getJSONArray("gasPriceList"));
				}

			}
		} catch (JSONException e) {
			e.printStackTrace();
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
