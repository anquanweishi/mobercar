package com.xtrd.obdcar.utils;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult.AddressComponent;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.baidu.mapapi.utils.DistanceUtil;
import com.xtrd.obdcar.entity.GPSInfo;
import com.xtrd.obdcar.utils.log.LogUtils;

public class LocationDecoder {

	protected static final String TAG = "LocationDecoder";
	private static GeoCoder mSearch;
	private static GPSInfo start,end;

	public static LatLng convertLatLng(LatLng source) {
		// 将GPS设备采集的原始GPS坐标转换成百度坐标  
		CoordinateConverter converter  = new CoordinateConverter();  
		converter.from(CoordType.GPS);  
		// sourceLatLng待转换坐标  
		converter.coord(source);  
		return converter.convert();
	}

	public static void decodeLocation(double lat,double lon,boolean convert, final LocationCallBack callback) {
		// 初始化搜索模块，注册事件监听
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {

			@Override
			public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
				if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
					LogUtils.e(TAG, "location erro");
				}
				LogUtils.e(TAG, "location " + result.getAddress());
				if(callback!=null) {
					callback.callback(result.getAddress(),result.getAddressDetail());
				}
			}

			@Override
			public void onGetGeoCodeResult(GeoCodeResult result) {
				if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
					LogUtils.e(TAG, "location erro");
				}
			}
		});
		if(convert) {
			mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(convertLatLng(new LatLng(lat, lon))));
		}else {
			mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng(lat, lon)));
		}

	}
	
	public static void decodeLocation(LatLng latlng,final LocationCallBack callback) {
		// 初始化搜索模块，注册事件监听
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
			
			@Override
			public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
				if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
					LogUtils.e(TAG, "location erro");
					return;
				}
				LogUtils.e(TAG, "location " + result.getAddress());
				if(callback!=null) {
					callback.callback(result.getAddress(),result.getAddressDetail());
				}
			}
			
			@Override
			public void onGetGeoCodeResult(GeoCodeResult result) {
				if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
					LogUtils.e(TAG, "location erro");
				}
			}
		});
		mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(latlng));
		
	}

	public interface LocationCallBack {
		void callback(String str, AddressComponent addressComponent);
	}
	
	public static LatLng getStart() {
		return convertLatLng(new LatLng(start.getLatitude(), start.getLongitude()));
	}
	public static LatLng getEnd() {
		return convertLatLng(new LatLng(end.getLatitude(), end.getLongitude()));
	}

	/**
	 * 
	 * @param mDistance 行驶距离
	 * @param start 起点
	 * @param end 终点 
	 * @return
	 */
	public static float getZoomLevel(ArrayList<GPSInfo> list) {
		double max=0,min=Math.sqrt (Math.pow(list.get(0).getLatitude(),2)+Math.pow(list.get(0).getLongitude(),2));
		int maxPos=0,minPos=0;
		GPSInfo info = null;
		for(int i=0;i<list.size();i++) {
			info= list.get(i);
			double pot = Math.sqrt (Math.pow(info.getLatitude(),2)+Math.pow(info.getLongitude(),2));
			if(max<pot) {
				max = pot;
				maxPos = i;
			}
			if(pot<min) {	
				min = pot;
				minPos = i;
			}
		}
		// max 19.0 min 3.0
		start = list.get(minPos);
		end = list.get(maxPos);
		double distance = DistanceUtil.getDistance(convertLatLng(new LatLng(start.getLatitude(), start.getLongitude())), convertLatLng(new LatLng(end.getLatitude(), end.getLongitude())));
		LogUtils.e(TAG, "distance " + distance);
		//19 18  17   16  15	14		13
		//50 100 200  500  1公里    2公里		5公里
		if(distance<50) {
			return 19;
		}else if(50<distance&&100>distance) {
			return 18;
		}else if(100<distance&&300>distance) {
			return 16;
		}else if(300<distance&&500>distance) {
			return 16;
		}else if(500<distance&&1000>distance) {
			return 16;
		}else if(1*1000<distance&&3*1000>distance) {
			return 15;
		}else if(3*1000<distance&&5*1000>distance) {
			return 14;
		}else if(5*1000<distance&&10*1000>distance) {
			return 13;
		}else if(10*1000<distance&&20*1000>distance) {
			return 12;
		}else if(20*1000<distance&&25*1000>distance) {
			return 11;
		}else if(25*1000<distance&&50*1000>distance) {
			return 10;
		}else if(50*1000<distance&&100*1000>distance) {
			return 9;
		}else if(100*1000<distance&&200*1000>distance) {
			return 8;
		}else if(200*1000<distance&&500*1000>distance) {
			return 7;
		}else if(500*1000<distance&&1000*1000>distance) {
			return 6;
		}else if(1000*1000<distance&&2000*1000>distance) {
			return 5;
		}else if(2000*1000<distance) {
			return 4;
		}
		/*if(distance<20) {
			return 19;
		}else if(20<distance&&50>distance) {
			return 18;
		}else if(50<distance&&100>distance) {
			return 17;
		}else if(100<distance&&200>distance) {
			return 16;
		}else if(200<distance&&500>distance) {
			return 15;
		}else if(1*1000<distance&&2*1000>distance) {
			return 14;
		}else if(2*1000<distance&&5*1000>distance) {
			return 13;
		}else if(5*1000<distance&&10*1000>distance) {
			return 13;
		}else if(10*1000<distance&&20*1000>distance) {
			return 11;
		}else if(20*1000<distance&&25*1000>distance) {
			return 10;
		}else if(25*1000<distance&&50*1000>distance) {
			return 9;
		}else if(50*1000<distance&&100*1000>distance) {
			return 8;
		}else if(100*1000<distance&&200*1000>distance) {
			return 7;
		}else if(200*1000<distance&&500*1000>distance) {
			return 6;
		}else if(500*1000<distance&&1000*1000>distance) {
			return 5;
		}else if(1000*1000<distance&&2000*1000>distance) {
			return 4;
		}else if(2000*1000<distance) {
			return 3;
		}*/
		return 16;
	}

	public static String getDistance(LatLng latLng1, LatLng latLng2) {
		if(latLng1==null||latLng2==null) {
			return null;
		}
		double distance = DistanceUtil.getDistance(latLng1,latLng2);
		if(distance<1000) {
			BigDecimal b = new BigDecimal(distance);
			return b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue()+"米";
		}else {
			BigDecimal b = new BigDecimal(distance/1000);
			return b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue()+"千米";
		}
	}
}
