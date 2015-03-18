package com.xtrd.obdcar.utils;

import java.util.List;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.xtrd.obdcar.XtrdApp;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.utils.log.LogUtils;

public class PoiUtils {
	protected static final String TAG = "PoiUtils";
	private static PoiSearch mPoiSearch;

	@Deprecated //废弃该方法
	public static void getPoi(final String key,int pageSize,final PoiCallBack callback) {
		// 初始化搜索模块，注册事件监听
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
			
			@Override
			public void onGetPoiResult(PoiResult result) {
				
				if (result == null
						|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
					if(callback!=null) {
						callback.callbackFail();
					}
					return;
				}
				if (result.error == SearchResult.ERRORNO.NO_ERROR) {
					List<PoiInfo> allPoi = result.getAllPoi();
					if(callback!=null) {
						callback.callback(key,allPoi);
					}
				}
				if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

					// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
					String strInfo = "在";
					for (CityInfo cityInfo : result.getSuggestCityList()) {
						strInfo += cityInfo.city;
						strInfo += ",";
					}
					strInfo += "找到结果";
					LogUtils.e(TAG, strInfo);
					if(callback!=null) {
						callback.callbackFail();
					}
				}
			}
			
			@Override
			public void onGetPoiDetailResult(PoiDetailResult arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		mPoiSearch.searchInCity((new PoiCitySearchOption())
				.city(SettingLoader.getCurrentCity(XtrdApp.getAppContext()))
				.keyword(key)
				.pageNum(pageSize));
	}
	public static void getPoiByNearby(final String key,int page,int pageSize,LatLng latlng,final PoiCallBack callback) {
		try {
			// 初始化搜索模块，注册事件监听
			mPoiSearch = PoiSearch.newInstance();
			mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
				
				@Override
				public void onGetPoiResult(PoiResult result) {
					
					if (result == null
							|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
						if(callback!=null) {
							callback.callbackFail();
						}
						return;
					}
					if (result.error == SearchResult.ERRORNO.NO_ERROR) {
						List<PoiInfo> allPoi = result.getAllPoi();
						if(callback!=null) {
							callback.callback(key,allPoi);
						}
					}
					if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {
						
						// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
						String strInfo = "在";
						for (CityInfo cityInfo : result.getSuggestCityList()) {
							strInfo += cityInfo.city;
							strInfo += ",";
						}
						strInfo += "找到结果";
						LogUtils.e(TAG, strInfo);
						if(callback!=null) {
							callback.callbackFail();
						}
					}
				}
				
				@Override
				public void onGetPoiDetailResult(PoiDetailResult arg0) {
					
				}
			});
			mPoiSearch.searchNearby((new PoiNearbySearchOption()
			.keyword(key)
			.location(latlng))
			.radius(100 * 1000)
			.pageNum(page)
			.pageCapacity(pageSize));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public interface PoiCallBack { 	
		void callback(String key,List<PoiInfo> allPoi);
		void callbackFail();
	}
}
