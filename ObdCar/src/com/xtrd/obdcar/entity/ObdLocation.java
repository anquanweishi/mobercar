package com.xtrd.obdcar.entity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.xtrd.obdcar.utils.CharacterParser;

public class ObdLocation implements Parcelable {
	private String areaCode;
	private String type;
	private String name;
	private String displayName;
	private String parent;
	private String province;
	private String city;
	private String district;
	private String provinceArea;
	private String cityArea;
	private String districtArea;
	private int hasChildren;
	private String sortLetters;// 首字母
	private List<ObdLocation> mList = new ArrayList<ObdLocation>();

	public ObdLocation() {
	}

	public ObdLocation(Parcel in) {
		areaCode = in.readString();
		type = in.readString();
		name = in.readString();
		displayName = in.readString();
		parent = in.readString();
		province = in.readString();
		city = in.readString();
		district = in.readString();
		provinceArea = in.readString();
		cityArea = in.readString();
		districtArea = in.readString();
		hasChildren = in.readInt();
		sortLetters = in.readString();
	}

	public static final Parcelable.Creator<ObdLocation> CREATOR = new Creator<ObdLocation>() {
		@Override
		public ObdLocation[] newArray(int size) {
			return new ObdLocation[size];
		}

		@Override
		public ObdLocation createFromParcel(Parcel source) {
			return new ObdLocation(source);
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(areaCode);
		dest.writeString(type);
		dest.writeString(name);
		dest.writeString(displayName);
		dest.writeString(parent);
		dest.writeString(province);
		dest.writeString(city);
		dest.writeString(district);
		dest.writeString(provinceArea);
		dest.writeString(cityArea);
		dest.writeString(districtArea);
		dest.writeInt(hasChildren);
		dest.writeString(sortLetters);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getProvinceArea() {
		return provinceArea;
	}

	public void setProvinceArea(String provinceArea) {
		this.provinceArea = provinceArea;
	}

	public String getCityArea() {
		return cityArea;
	}

	public void setCityArea(String cityArea) {
		this.cityArea = cityArea;
	}

	public String getDistrictArea() {
		return districtArea;
	}

	public void setDistrictArea(String districtArea) {
		this.districtArea = districtArea;
	}

	public int getHasChildren() {
		return hasChildren;
	}

	public void setHasChildren(int hasChildren) {
		this.hasChildren = hasChildren;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public List<ObdLocation> getList() {
		return mList;
	}

	public void setList(List<ObdLocation> mList) {
		this.mList = mList;
	}

	public void parser(JSONObject jsonObject) {
		try {
			if (jsonObject != null) {
				if (jsonObject.has("areaCode")) {
					setAreaCode(jsonObject.getString("areaCode"));
				}
				if (jsonObject.has("type")) {
					setType(jsonObject.getString("type"));
				}

				if (jsonObject.has("name")) {
					setName(jsonObject.getString("name"));
				}
				if (jsonObject.has("displayName")) {
					setDisplayName(jsonObject.getString("displayName"));
					// 汉字转换成拼音
					String pinyin = CharacterParser.getInstance().getSelling(
							getDisplayName());
					// 获取首字母--大写
					String sortString = pinyin.substring(0, 1).toUpperCase();
					// 正则表达式，判断首字母是否是英文字母
					if (sortString.matches("[A-Z]")) {
						setSortLetters(sortString.toUpperCase());
					} else {
						setSortLetters("#");
					}
				}
				if (jsonObject.has("parent")) {
					setParent(jsonObject.getString("parent"));
				}
				if (jsonObject.has("province")) {
					setProvince(jsonObject.getString("province"));
				}
				if (jsonObject.has("city")) {
					setCity(jsonObject.getString("city"));
				}
				if (jsonObject.has("district")) {
					setDistrict(jsonObject.getString("district"));
				}
				if (jsonObject.has("provinceArea")) {
					setProvinceArea(jsonObject.getString("provinceArea"));
				}
				if (jsonObject.has("cityArea")) {
					setCityArea(jsonObject.getString("cityArea"));
				}
				if (jsonObject.has("districtArea")) {
					setDistrictArea(jsonObject.getString("districtArea"));
				}
				if (jsonObject.has("hasChildren")) {
					setHasChildren(jsonObject.getInt("hasChildren"));
				}

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
