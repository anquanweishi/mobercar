package com.xtrd.obdcar.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class VersionEntity {
	private int appId;
	private int majorVersion;
	private int minorVersion;
	private int revision;
	private String uri;

	public int getAppId() {
		return appId;
	}

	
	public int getMajorVersion() {
		return majorVersion;
	}


	public void setMajorVersion(int majorVersion) {
		this.majorVersion = majorVersion;
	}


	public int getMinorVersion() {
		return minorVersion;
	}


	public void setMinorVersion(int minorVersion) {
		this.minorVersion = minorVersion;
	}


	public int getRevision() {
		return revision;
	}


	public void setRevision(int revision) {
		this.revision = revision;
	}


	public void setAppId(int appId) {
		this.appId = appId;
	}


	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public void parser(JSONObject opt) throws JSONException {
		if(opt.has("appId")) {
			setAppId(opt.getInt("appId"));
		}
		if(opt.has("majorVersion")) {
			setMajorVersion(opt.getInt("majorVersion"));
		}
		if(opt.has("minorVersion")) {
			setMinorVersion(opt.getInt("minorVersion"));
		}
		if(opt.has("revision")) {
			setRevision(opt.getInt("revision"));
		}
		if(opt.has("uri")) {
			setUri(opt.getString("uri"));
		}
	}

}
