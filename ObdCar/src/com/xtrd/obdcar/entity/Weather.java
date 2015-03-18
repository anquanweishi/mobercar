package com.xtrd.obdcar.entity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 三天天气
 * 以,号分割
 * @author start
 * 
 */
public class Weather {
	private int id;
	private String city;
	private String cacheTime;
	private String dayPictureUrl;
	private String nightPictureUrl;
	private String weather;
	private String wind;
	private String temperature;

	// 2.0
	private String date;// 日期

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCacheTime() {
		return cacheTime;
	}

	public void setCacheTime(String cacheTime) {
		this.cacheTime = cacheTime;
	}

	public String getDayPictureUrl() {
		return dayPictureUrl;
	}

	public void setDayPictureUrl(String dayPictureUrl) {
		this.dayPictureUrl = dayPictureUrl;
	}

	public String getNightPictureUrl() {
		return nightPictureUrl;
	}

	public void setNightPictureUrl(String nightPictureUrl) {
		this.nightPictureUrl = nightPictureUrl;
	}

	public String getWeather() {
		return weather;
	}

	public void setWeather(String weather) {
		this.weather = weather;
	}

	public String getWind() {
		return wind;
	}

	public void setWind(String wind) {
		this.wind = wind;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void parser(JSONObject opt) throws JSONException {
		if (opt != null) {
			if (opt.has("id")) {
				setId(opt.getInt("id"));
			}
			if (opt.has("city")) {
				setCity(opt.getString("city"));
			}
			if (opt.has("")) {
				setCacheTime(opt.getString("cacheTime"));
			}
			if (opt.has("dayPictureUrl")) {
				setDayPictureUrl(opt.getString("dayPictureUrl"));
			}
			if (opt.has("nightPictureUrl")) {
				setNightPictureUrl(opt.getString("nightPictureUrl"));
			}
			if (opt.has("weather")) {
				setWeather(opt.getString("weather"));
			}
			if (opt.has("wind")) {
				setWind(opt.getString("wind"));
			}
			if (opt.has("temperature")) {
				setTemperature(opt.getString("temperature"));
			}
		}
	}

}
