package com.xtrd.obdcar.db;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.baidu.mapapi.model.LatLng;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.entity.CarInfo;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.LocationDecoder;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.log.LogUtils;

public class SettingLoader {
	private static final String TAG = "SettingLoader";

	private static final String CONFIG = "settings";
	private static final String FIRSTLAUNCH = "firstLaunch";
	private static final String SHORTCUT = "shortcut";
	private static final String SVR_URL = "svr_url";
	private static final String Login_User = "login_user";
	private static final String Has_Login = "has_login";
	private static final String Channel = "channel";
	//设置相关
	private static final String SECUREOPEN = "secureopen";
	private static final String REFRESH = "refresh";
	private static final String TRIP_HIDE = "trip_hide";

	//车辆相关
	private static final String VEHICLEID = "vehicleId";
	private static final String PLATENUMBER = "platenumber";
	private static final String BRANCHNAME = "branch_name";
	private static final String BRANCHID = "branchId";
	private static final String ENGINENUMBER = "engineNumber";
	private static final String VINNUM = "vinNum";
	private static final String Illegal_City_AND_CODE = "city_and_area_code";
	private static final String Illegal_Query_Date = "query_date";
	private static final String CURRENT_CITY = "current_city";

	private static final String LATITUDE = "latitude";
	private static final String LONGITUDE = "longitude";
	private static final String CONVERT = "convert";

	private static final String Jpush_Id = "jpush_id";

	private static final String BOX_BIND = "box_bind";

	private static final String Service_Data = "Service_Data";

	//油价
	private static final String Oil_Price_One = "one_price_value";
	private static final String Oil_Price_Two = "two_price_value";

	public static void clearAll(Context context) {
		SharedPreferences settings = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		Editor edit = settings.edit();
		edit.remove(SECUREOPEN);
		edit.remove(REFRESH);

		//		edit.remove(Login_User);
		edit.remove(Channel);
		edit.remove(VEHICLEID);
		edit.remove(PLATENUMBER);
		edit.remove(BRANCHID);
		edit.remove(ENGINENUMBER);
		edit.remove(VINNUM);
		//		edit.remove(Illegal_City_AND_CODE);
		//		edit.remove(Illegal_Query_Date);
		edit.remove(CURRENT_CITY);
		edit.remove(LATITUDE);
		edit.remove(LONGITUDE);
		edit.commit();
	}


	public static boolean isFirstLaunch(Context context) {
		SharedPreferences settings = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		return settings.getBoolean(FIRSTLAUNCH, true);
	}

	public static boolean setFirstLaunch(Context context, boolean first) {
		SharedPreferences preferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		Editor edit = preferences.edit();
		edit.putBoolean(FIRSTLAUNCH, first);
		return edit.commit();
	}

	public static int getCreateShortcut(Context context) {
		SharedPreferences settings = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		return settings.getInt(SHORTCUT, 0);
	}
	public static boolean setCreateShortcut(Context context, int create) {
		SharedPreferences preferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		Editor edit = preferences.edit();
		edit.putInt(SHORTCUT, create);
		return edit.commit();
	}
	public static boolean getSecureOpen(Context context) {
		SharedPreferences settings = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		return settings.getBoolean(SECUREOPEN, false);
	}
	public static boolean setSecureOpen(Context context,boolean open) {
		SharedPreferences preferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		Editor edit = preferences.edit();
		edit.putBoolean(SECUREOPEN, open);
		return edit.commit();
	}
	public static boolean saveDefaultCar(Context context,CarInfo carInfo) {
		if(carInfo==null) {
			return false;
		}
		SharedPreferences preferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		Editor edit = preferences.edit();
		edit.putInt(VEHICLEID, carInfo.getVehicleId());
		edit.putString(PLATENUMBER, carInfo.getPlateNumber());
		edit.putString(BRANCHNAME, carInfo.getSeries().startsWith(carInfo.getBranch())?carInfo.getSeries():carInfo.getBranch()+carInfo.getSeries());
		edit.putInt(BRANCHID, carInfo.getBranchId());
		edit.putString(VINNUM , carInfo.getVin());
		edit.putString(ENGINENUMBER , carInfo.getEngineNumber());
		return edit.commit();
	}

	public static String getVehicleId(Context context) {
		SharedPreferences settings = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		return settings.getInt(VEHICLEID, 0)==0?"":settings.getInt(VEHICLEID, 0)+"";
	}
	public static String getCarPlate(Context context) {
		SharedPreferences settings = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		return settings.getString(PLATENUMBER,"");
	}

	public static int getBranchId(Context context) {
		SharedPreferences settings = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		return settings.getInt(BRANCHID,0);
	}

	public static String getBranchName(Context context) {
		SharedPreferences settings = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		return settings.getString(BRANCHNAME, "");
	}

	public static boolean setRefresh(Context context,boolean isChecked) {
		SharedPreferences preferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		Editor edit = preferences.edit();
		edit.putBoolean(REFRESH , isChecked);
		return edit.commit();
	}
	public static boolean isRefresh(Context context) {
		SharedPreferences settings = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		return settings.getBoolean(REFRESH,false);
	}
	public static boolean setTripHide(Context context,boolean isChecked) {
		SharedPreferences preferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		Editor edit = preferences.edit();
		edit.putBoolean(TRIP_HIDE , isChecked);
		return edit.commit();
	}

	public static boolean isTripHide(Context context) {
		SharedPreferences settings = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		return settings.getBoolean(TRIP_HIDE,false);
	}


	public static String getEngineNum(Context context) {
		SharedPreferences settings = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		return settings.getString(ENGINENUMBER,"");
	}

	public static String getVinNum(Context context) {
		SharedPreferences settings = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		return settings.getString(VINNUM,"");
	}

	public static String getSvr(Context context) {
		SharedPreferences settings = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		return settings.getString(SVR_URL,ApiConfig.DEFAULT_API_URL);
	}
	public static boolean setSvr(Context context,String svr) {
		SharedPreferences preferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		Editor edit = preferences.edit();
		edit.putString(SVR_URL , svr);
		return edit.commit();
	}

	public static boolean setLoginName(Context context, String user, String channel) {
		SharedPreferences preferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		Editor edit = preferences.edit();
		if(!StringUtils.isNullOrEmpty(user)) {
			edit.putString(Login_User , user);
		}
		if(!StringUtils.isNullOrEmpty(channel)) {
			edit.putString(Channel , channel);
		}
		return edit.commit();
	}
	public static boolean clearLoginName(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		Editor edit = preferences.edit();
		edit.putString(Login_User , "");
		edit.putString(Channel , "");
		return edit.commit();
	}

	public static String getLoginName(Context context) {
		SharedPreferences settings = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		return settings.getString(Login_User,"");
	}
	public static String getChannel(Context context) {
		SharedPreferences settings = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		return settings.getString(Channel,"");
	}

	public static boolean setIllegalCityAndCode(Context context, String city, String areaCode) {
		try {
			SharedPreferences preferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
			String cache = preferences.getString(Illegal_City_AND_CODE, null);
			Editor edit = preferences.edit();
			StringBuilder sb = new StringBuilder();
			if(!StringUtils.isNullOrEmpty(cache)) {
				if(cache.contains(SettingLoader.getVehicleId(context))) {
					String[] split = cache.split(";");
					if(split!=null&&split.length>0) {
						for(int i=0;i<split.length;i++) {
							if(split[i].contains(SettingLoader.getVehicleId(context))) {
								sb.append(SettingLoader.getVehicleId(context)+","+city+","+areaCode+";");
								continue;
							}
							sb.append(split[i]);
							sb.append(";");
						}
					}
				}else {
					sb.append(SettingLoader.getVehicleId(context)+","+city+","+areaCode+";"+cache+";");
				}
				if(!StringUtils.isNullOrEmpty(sb.toString())) {
					String str = sb.toString().substring(0,sb.toString().lastIndexOf(";"));
					LogUtils.e(TAG, "str " + str);
					edit.putString(Illegal_City_AND_CODE, str);
				}else {
					edit.putString(Illegal_City_AND_CODE, sb.toString());
				}
			}else {
				edit.putString(Illegal_City_AND_CODE, SettingLoader.getVehicleId(context)+","+city+","+areaCode);
			}
			return edit.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String getIllegalCity(Context context) {
		try {
			SharedPreferences settings = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
			String str = settings.getString(Illegal_City_AND_CODE,"");
			if(!StringUtils.isNullOrEmpty(str)) {
				String[] split = str.split(";");
				for(int i=0;i<split.length;i++) {
					if(split[i].contains(SettingLoader.getVehicleId(context))) {
						return split[i].split(",")[1];
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String getIllegalCityCode(Context context) {
		try {
			SharedPreferences settings = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
			String str = settings.getString(Illegal_City_AND_CODE,"");
			if(!StringUtils.isNullOrEmpty(str)) {
				String[] split = str.split(";");
				for(int i=0;i<split.length;i++) {
					if(split[i].contains(SettingLoader.getVehicleId(context))) {
						return split[i].split(",")[2];
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}


	public static boolean setQueryDate(Context context, String date) {
		try {
			SharedPreferences preferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
			String cache = preferences.getString(Illegal_Query_Date, null);
			Editor edit = preferences.edit();
			StringBuilder sb = new StringBuilder();
			if(!StringUtils.isNullOrEmpty(cache)) {
				if(cache.contains(SettingLoader.getVehicleId(context))) {
					String[] split = cache.split(";");
					if(split!=null&&split.length>0) {
						for(int i=0;i<split.length;i++) {
							if(split[i].contains(SettingLoader.getVehicleId(context))) {
								sb.append(SettingLoader.getVehicleId(context)+","+date+";");
								continue;
							}
							sb.append(split[i]);
							sb.append(";");
						}
					}
				}else {
					sb.append(SettingLoader.getVehicleId(context)+","+date+";"+cache+";");
				}
				String str = sb.toString().substring(0,sb.toString().lastIndexOf(";"));
				LogUtils.e(TAG, "date " + str);
				edit.putString(Illegal_Query_Date, str);
			}else {
				edit.putString(Illegal_Query_Date, SettingLoader.getVehicleId(context)+","+date);
			}
			return edit.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String getQueryDate(Context context) {
		try {
			SharedPreferences settings = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
			String str = settings.getString(Illegal_Query_Date,"");
			if(!StringUtils.isNullOrEmpty(str)) {
				String[] split = str.split(";");
				for(int i=0;i<split.length;i++) {
					if(split[i].contains(SettingLoader.getVehicleId(context))) {
						return split[i].split(",")[1];
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static boolean setCurrentCity(Context context,String city) {
		SharedPreferences preferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		Editor edit = preferences.edit();
		edit.putString(CURRENT_CITY , city);
		return edit.commit();
	}

	public static String getCurrentCity(Context context) {
		SharedPreferences settings = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		return settings.getString(CURRENT_CITY,"");
	}

	public static boolean setCarLatLng(Context context, double latitude,double longitude, boolean convert) {
		SharedPreferences preferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		Editor edit = preferences.edit();
		edit.putString(LATITUDE , latitude+"");
		edit.putString(LONGITUDE , longitude+"");
		edit.putBoolean(CONVERT ,convert);
		return edit.commit();
	}

	public static LatLng getCarLatLng(Context context) {
		SharedPreferences settings = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		String lat =  settings.getString(LATITUDE,"");
		String lng =  settings.getString(LONGITUDE,"");
		if(!StringUtils.isNullOrEmpty(lat)&&!StringUtils.isNullOrEmpty(lng)) {
			double latitude = Double.parseDouble(lat);
			double longitue = Double.parseDouble(lng);
			if(latitude>0&&longitue>0) {
				if(settings.getBoolean(CONVERT,false)) {
					return LocationDecoder.convertLatLng(new LatLng(latitude,longitue));
				}else {
					return new LatLng(latitude,longitue);
				}
			}else {
				return null;
			}
		}else {
			return null;
		}
	}

	public static boolean setJpushId(Context context, String regId) {
		SharedPreferences preferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		Editor edit = preferences.edit();
		edit.putString(Jpush_Id , regId);
		return edit.commit();
	}

	public static String getJpushId(Context context) {
		SharedPreferences settings = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		return settings.getString(Jpush_Id,"");
	}


	public static boolean hasBindBox(Context context) {
		SharedPreferences settings = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		String str = settings.getString(BOX_BIND,"");
		if(!StringUtils.isNullOrEmpty(str)) {
			String[] split = str.split(",");
			if(split.length>1) {
				if(getVehicleId(context).equals(split[0])) {
					return "0".equals(split[1])?false:true;//0未绑定  1绑定
				}
			}
		}
		return false;
	}

	public static boolean setBindBox(Context context,String hasBind) {
		SharedPreferences preferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		Editor edit = preferences.edit();
		edit.putString(BOX_BIND , hasBind);
		return edit.commit();
	}


	/**
	 * service数据缓存
	 * @param serviceActivity
	 * @param array
	 */
	public static boolean saveService(Context context,JSONArray array) {
		SharedPreferences preferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		Editor edit = preferences.edit();
		edit.putString(Service_Data , array.toString());
		return edit.commit();
	}


	public static JSONArray getLocalService(Context context) {
		SharedPreferences settings = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		String str = settings.getString(Service_Data,"");
		if(!StringUtils.isNullOrEmpty(str)) {
			try {
				return new JSONArray(str);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;

	}


	public static boolean setHasLogin(Context context, boolean haslogin) {
		SharedPreferences preferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		Editor edit = preferences.edit();
		edit.putBoolean(Has_Login , haslogin);
		return edit.commit();
	}

	public static boolean hasLogin(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		return preferences.getBoolean(Has_Login, false);
	}


	public static boolean setOnePriceValue(Context context, String value) {
		if(StringUtils.isNullOrEmpty(value)) {
			return false;
		}
		SharedPreferences preferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		Editor edit = preferences.edit();
		edit.putString(Oil_Price_One , value);
		return edit.commit();
	}
	public static String getOnePriceValue(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		return preferences.getString(Oil_Price_One , "");
	}
	public static int getIconForOnePrice(Context context,String name,double price) {
		String priceValue = getOnePriceValue(context);
		if(!StringUtils.isNullOrEmpty(priceValue)) {
			String[] split = priceValue.split("#");
			if(split!=null&&split.length==2) {
				if(split[0].equals(name)) {
					if(price>Double.parseDouble(split[1])) {
						return R.drawable.ic_oil_price_up;
					}else if(price==Double.parseDouble(split[1])) {
						return 0;
					}else if(price<Double.parseDouble(split[1])) {
						return R.drawable.ic_oil_price_down;
					}
				}
			}
		}
		return 0;
	}

	public static boolean setTwoPriceValue(Context context, String value) {
		if(StringUtils.isNullOrEmpty(value)) {
			return false;
		}
		SharedPreferences preferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		Editor edit = preferences.edit();
		edit.putString(Oil_Price_Two, value);
		return edit.commit();
	}
	public static String getTwpPriceValue(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		return preferences.getString(Oil_Price_Two , "");
	}
	public static int getIconForTwoPrice(Context context,String name,double price) {
		String priceValue = getTwpPriceValue(context);
		if(!StringUtils.isNullOrEmpty(priceValue)) {
			String[] split = priceValue.split("#");
			if(split!=null&&split.length==2) {
				if(split[0].equals(name)) {
					if(price>Double.parseDouble(split[1])) {
						return R.drawable.ic_oil_price_up;
					}else if(price==Double.parseDouble(split[1])) {
						return 0;
					}else if(price<Double.parseDouble(split[1])) {
						return R.drawable.ic_oil_price_down;
					}
				}
			}
		}
		return 0;
	}
}
