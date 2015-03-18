package com.xtrd.obdcar.db;



import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.AssetManager;

import com.xtrd.obdcar.entity.CarCondtion;
import com.xtrd.obdcar.entity.CarUseReport;
import com.xtrd.obdcar.entity.FuelInfo;
import com.xtrd.obdcar.entity.FuelReport;
import com.xtrd.obdcar.entity.GPSInfo;
import com.xtrd.obdcar.entity.Trip;

public class CacheManager {
	private static final String TAG = "CacheManager";
	private static CacheManager instance = null;
	private static Context mContext;
	public static final String PARTYTYPE_INFO = "plist";


	/**
	 * 获取分类
	 * @return
	 */
	public CarCondtion getVcDefault(String filename) {
		try {
			JSONObject json = new JSONObject(load(mContext,filename));
			if(json.has("result"))  {
				json = json.getJSONObject("result");
				CarCondtion condition = new CarCondtion();
				condition.parser(json);
				return condition;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取模拟用车报告数据
	 * @return
	 */
	public CarUseReport getCarUseDefault(String filename) {
		try {
			JSONObject json = new JSONObject(load(mContext,filename));
			if(json!=null) {
				CarUseReport report = new CarUseReport();
				if(json.has("result")) {
					report.parser(json.getJSONObject("result"));
				}
				
				if(json.has("datas")) {
					report.parserUnits(json.getJSONObject("datas"));
				}
				return report;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * 读取数据
	 * @param path 相对路径，不可以/开头
	 * @return
	 */
	synchronized public final byte[] readCategory(String filename) {
		FileInputStream fis = null;
		byte[] data = null;
		try {
			fis = mContext.openFileInput(filename);
			data = new byte[(int) fis.available()];
			fis.read(data);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (null != fis) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return data;
	}

	public boolean del(String pathname) {
		return mContext.deleteFile(pathname);
	}

	public final static CacheManager getInstance(Context context) {
		mContext = context;
		if (null == instance) {
			instance = new CacheManager();
		}
		return instance;
	}


	/**
	 * @param 读取assets分类文件
	 * @return
	 */
	synchronized public final static String load(Context context,String filename) {
		AssetManager assets = context.getAssets();
		try {
			InputStream ips = assets.open(filename);
			byte[] buffer = new byte[1024];
			int number = 0;
			StringBuffer sb = new StringBuffer();
			while((number=ips.read(buffer))!=-1) {
				sb.append(new String(buffer,0,number));
			}
			ips.close();
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 加载个人默认数据
	 * @return
	 */
	public JSONObject getMyInfo(String filename) {
		try {
			JSONObject json = new JSONObject(load(mContext,filename));
			if(json.has("result"))  {
				return json.getJSONObject("result");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<GPSInfo> getGpsData(String filename) {
		ArrayList<GPSInfo> list = new ArrayList<GPSInfo>();
		try {
			JSONObject json = new JSONObject(load(mContext,filename));
			if(json.has("result"))  {

				JSONArray array = json.getJSONArray("result");
				if(array!=null&&array.length()>0) {
					GPSInfo info = null;
					for(int i=0;i<array.length();i++) {
						info = new GPSInfo();
						info.parser(array.optJSONObject(i));
						list.add(info);
					}
					return list;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public ArrayList<Trip> getTripData(String filename) {
		ArrayList<Trip> list = new ArrayList<Trip>();
		try {
			JSONObject json = new JSONObject(load(mContext,filename));
			if(json.has("result"))  {
				JSONArray array = json.getJSONArray("result");
				if(array!=null&&array.length()>0) {
					Trip info = null;
					for(int i=0;i<array.length();i++) {
						info = new Trip();
						info.parser(array.optJSONObject(i));
						list.add(info);
					}
					return list;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public ArrayList<FuelReport> getReportDate(String filename) {
		ArrayList<FuelReport> list = new ArrayList<FuelReport>();
		try {
			JSONObject json = new JSONObject(load(mContext,filename));
			if(json.has("result"))  {
				JSONArray array = json.getJSONArray("result");
				if(array!=null&&array.length()>0) {
					FuelReport info = null;
					for(int i=0;i<array.length();i++) {
						info = new FuelReport();
						info.parser(array.optJSONObject(i));
						list.add(info);
					}
					return list;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public FuelInfo getReportTopData(String filename) {
		FuelInfo info = new FuelInfo();
		try {
			JSONObject json = new JSONObject(load(mContext,filename));
			if(json.has("result"))  {
				json = json.getJSONObject("result");
				info = new FuelInfo();
				info.parser(json);
			}
			return info;
		} catch (JSONException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}
}
