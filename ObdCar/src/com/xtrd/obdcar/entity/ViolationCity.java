package com.xtrd.obdcar.entity;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.xtrd.obdcar.utils.CharacterParser;

public class ViolationCity implements Parcelable {

	private String provinceCode;
	private String province;
	private String sortLetters;// 首字母
	// ///////////////////////////
	private String cityName;
	private String cityCode;
	private String abbr;
	private int engine;
	private int engineno;//0代表传全部 x代表传后几位
	private int vin;
	private int vinno;
	private int regist;
	private int registno;

	public ViolationCity() {
	}

	public ViolationCity(Parcel in) {
		provinceCode = in.readString();
		province = in.readString();
		sortLetters = in.readString();
		cityName = in.readString();
		cityCode = in.readString();
		abbr = in.readString();
		engine = in.readInt();
		engineno = in.readInt();
		vin = in.readInt();
		vinno = in.readInt();
		regist = in.readInt();
		registno = in.readInt();

	}

	public static final Parcelable.Creator<ViolationCity> CREATOR = new Creator<ViolationCity>() {
		@Override
		public ViolationCity[] newArray(int size) {
			return new ViolationCity[size];
		}

		@Override
		public ViolationCity createFromParcel(Parcel source) {
			return new ViolationCity(source);
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(provinceCode);
		dest.writeString(province);
		dest.writeString(sortLetters);
		dest.writeString(cityName);
		dest.writeString(cityCode);
		dest.writeString(abbr);
		dest.writeInt(engine);
		dest.writeInt(engineno);
		dest.writeInt(vin);
		dest.writeInt(vinno);
		dest.writeInt(regist);
		dest.writeInt(registno);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getAbbr() {
		return abbr;
	}

	public void setAbbr(String abbr) {
		this.abbr = abbr;
	}

	public int getEngine() {
		return engine;
	}

	public void setEngine(int engine) {
		this.engine = engine;
	}

	public int getEngineno() {
		return engineno;
	}

	public void setEngineno(int engineno) {
		this.engineno = engineno;
	}

	public int getVin() {
		return vin;
	}

	public void setVin(int vin) {
		this.vin = vin;
	}

	public int getVinno() {
		return vinno;
	}

	public void setVinno(int vinno) {
		this.vinno = vinno;
	}

	public int getRegist() {
		return regist;
	}

	public void setRegist(int regist) {
		this.regist = regist;
	}

	public int getRegistno() {
		return registno;
	}

	public void setRegistno(int registno) {
		this.registno = registno;
	}

	public void parser(JSONObject opt) throws JSONException {
		if (opt != null) {
			if (opt.has("provinceCode")) {
				setProvinceCode(opt.getString("provinceCode"));
			}
			if (opt.has("province")) {
				setProvince(opt.getString("province"));
				// 汉字转换成拼音
				String pinyin = CharacterParser.getInstance().getSelling(
						getProvince());
				// 获取首字母--大写
				String sortString = pinyin.substring(0, 1).toUpperCase();
				// 正则表达式，判断首字母是否是英文字母
				if (sortString.matches("[A-Z]")) {
					setSortLetters(sortString.toUpperCase());
				} else {
					setSortLetters("#");
				}
			}

			if (opt.has("cityName")) {
				setCityName(opt.getString("cityName"));
			}
			if (opt.has("cityCode")) {
				setCityCode(opt.getString("cityCode"));
			}
			if (opt.has("abbr")) {
				setAbbr(opt.getString("abbr"));
			}
			if (opt.has("engine")) {
				setEngine(opt.getBoolean("engine") ? 1 : 0);
			}
			if (opt.has("engineno")) {
				setEngineno(opt.getInt("engineno"));
			}
			if (opt.has("vin")) {
				setVin(opt.getBoolean("vin") ? 1 : 0);
			}
			if (opt.has("vinNo")) {
				setVinno(opt.getInt("vinNo"));
			}
			if (opt.has("regist")) {
				setRegist(opt.getBoolean("regist") ? 1 : 0);
			}
			if (opt.has("registno")) {
				setRegistno(opt.getInt("registno"));
			}
		}
	}

}
