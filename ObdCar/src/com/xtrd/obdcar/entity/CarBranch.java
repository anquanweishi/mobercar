package com.xtrd.obdcar.entity;

import org.json.JSONException;
import org.json.JSONObject;

import com.xtrd.obdcar.utils.CharacterParser;

/**
 * 车辆品牌
 * 
 * @author start
 * 
 */
public class CarBranch {

	private int branchId;
	private String branch;
	private String name;
	private String country;
	private String acronym;
	private int hasChildren;
	private String sortLetters;// 首字母

	public int getBranchId() {
		return branchId;
	}

	public void setBranchId(int branchId) {
		this.branchId = branchId;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getAcronym() {
		return acronym;
	}

	public void setAcronym(String acronym) {
		this.acronym = acronym;
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

	public void parser(JSONObject opt) throws JSONException {
		if(opt!=null) {
			if(opt.has("branchId")) {
				setBranchId(opt.getInt("branchId"));
			}
			if(opt.has("branch")) {
				setBranch(opt.getString("branch"));
				//汉字转换成拼音
				String pinyin = CharacterParser.getInstance().getSelling(getBranch());
				//获取首字母--大写
				String sortString = pinyin.substring(0, 1).toUpperCase();
				// 正则表达式，判断首字母是否是英文字母
				if(sortString.matches("[A-Z]")){
					setSortLetters(sortString.toUpperCase());
				}else{
					setSortLetters("#");
				}
			}
			if(opt.has("name")) {
				setName(opt.getString("name"));
			}
			if(opt.has("country")) {
				setCountry(opt.getString("country"));
			}
			if(opt.has("acronym")) {
				setAcronym(opt.getString("acronym"));
			}
			if(opt.has("hasChildren")) {
				setHasChildren(opt.getInt("hasChildren"));
			}
		}
		
	}

}
