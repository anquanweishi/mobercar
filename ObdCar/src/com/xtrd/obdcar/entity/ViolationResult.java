package com.xtrd.obdcar.entity;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class ViolationResult implements Parcelable {
	private int id;
	private String vehicleId;
	private String date;
	private String area;
	private String act;
	private String code;
	private int fen;
	private int money;

	public ViolationResult() {
	}

	public ViolationResult(Parcel in) {
		id = in.readInt();
		vehicleId = in.readString();
		date = in.readString();
		area = in.readString();
		act = in.readString();
		code = in.readString();
		fen = in.readInt();
		money = in.readInt();
	}

	public static final Parcelable.Creator<ViolationResult> CREATOR = new Creator<ViolationResult>() {
		@Override
		public ViolationResult[] newArray(int size) {
			return new ViolationResult[size];
		}

		@Override
		public ViolationResult createFromParcel(Parcel source) {
			return new ViolationResult(source);
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(vehicleId);
		dest.writeString(date);
		dest.writeString(area);
		dest.writeString(act);
		dest.writeString(code);
		dest.writeInt(fen);
		dest.writeInt(money);
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

	public String getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getAct() {
		return act;
	}

	public void setAct(String act) {
		this.act = act;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getFen() {
		return fen;
	}

	public void setFen(int fen) {
		this.fen = fen;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public void parser(JSONObject opt) throws JSONException {
			if (opt != null) {
				if (opt.has("date")) {
					setDate(opt.getString("date"));
				}
				if (opt.has("area")) {
					setArea(opt.getString("area"));
				}
				if (opt.has("act")) {
					setAct(opt.getString("act"));
				}
				if (opt.has("code")) {
					setCode(opt.getString("code"));
				}
				if (opt.has("fen")) {
					setFen(opt.getInt("fen"));
				}
				if (opt.has("money")) {
					setMoney(opt.getInt("money"));
				}
			}
	}

}
