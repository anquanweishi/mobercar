package com.xtrd.obdcar.entity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VConditionGroup {
	private String time;
	private String count;
	private ArrayList<VCondition> list = new ArrayList<VCondition>();

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public ArrayList<VCondition> getList() {
		return list;
	}

	public void setList(ArrayList<VCondition> list) {
		this.list = list;
	}

	public void parser(JSONObject opt) {
		try {
			if (opt != null) {
				if (opt.has("datas")) {
					JSONArray array = opt.getJSONArray("datas");
					if (array != null && array.length() > 0) {
						VCondition item = null;
						for (int i = 0; i < array.length(); i++) {
							item = new VCondition();
							item.parser(array.optJSONObject(i));
							list.add(item);
						}
					}
				}
				if(opt.has("time")) {
					setTime(opt.getString("time"));
				}
				if(opt.has("size")) {
					setCount(opt.getInt("size")+"");
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
