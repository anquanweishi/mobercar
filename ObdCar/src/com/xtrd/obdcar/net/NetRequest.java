package com.xtrd.obdcar.net;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.http.PreferencesCookieStore;
import android.content.Context;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.utils.NetUtils;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.utils.log.LogUtils;

public class NetRequest {

	protected static final String TAG = "NetRequest";

	public interface NetCallBack{
		void sucCallback(String str);
		void failCallback(int errorNo, String strMsg);
	}

	public static void requestUrl(final Context context,String url,AjaxParams params,final NetCallBack callback) {
		if(!NetUtils.IsNetworkAvailable()) {
			Utils.showNetTips(context);
			return;
		}
		LogUtils.e(TAG, "url "+url);
		LogUtils.e(TAG,"params "+ params.toString());
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(context);
		fh.configCookieStore(store);

		fh.post(url, params,new AjaxCallBack<String>() {

			@Override
			public void onStart() {
				super.onStart();
				((BaseActivity)context).showLoading();
			}

			@Override
			public void onSuccess(String t) {
				((BaseActivity)context).dismissLoading();
				LogUtils.e(TAG, t.toString());
				if(!StringUtils.isNullOrEmpty(t)) {
					if("<".equals(String.valueOf(t.charAt(0)))) {
						((BaseActivity)context).showLoginDialog();
					}else {
						if(callback!=null) {
							callback.sucCallback(t);
						}
					}
				}
				super.onSuccess(t);
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				((BaseActivity)context).dismissLoading();
				LogUtils.e(TAG, errorNo+strMsg);
				if(callback!=null) {
					callback.failCallback(errorNo, strMsg);
				}
				super.onFailure(t, errorNo, strMsg);
			}
		});
	}
	
	public static void requestUrlNoDialog(final Context context,String url,AjaxParams params,final NetCallBack callback) {
		if(!NetUtils.IsNetworkAvailable()) {
			Utils.showNetTips(context);
			return;
		}
		LogUtils.e(TAG, "url "+url);
		LogUtils.e(TAG,"params "+ params.toString());
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(context);
		fh.configCookieStore(store);

		fh.post(url, params,new AjaxCallBack<String>() {


			@Override
			public void onSuccess(String t) {
				LogUtils.e(TAG, t.toString());
				if(!StringUtils.isNullOrEmpty(t)) {
					if("<".equals(String.valueOf(t.charAt(0)))) {
						((BaseActivity)context).showLoginDialog();
					}else {
						if(callback!=null) {
							callback.sucCallback(t);
						}
					}
				}
				super.onSuccess(t);
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				LogUtils.e(TAG, errorNo+strMsg);
				if(callback!=null) {
					callback.failCallback(errorNo, strMsg);
				}
				super.onFailure(t, errorNo, strMsg);
			}
		});
	}
	
	public static void requestUrl(final Context context,String url,final NetCallBack callback) {
		if(!NetUtils.IsNetworkAvailable()) {
			Utils.showNetTips(context);
			return;
		}
		LogUtils.e(TAG, "url "+url);
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(context);
		fh.configCookieStore(store);
		
		fh.post(url, new AjaxCallBack<String>() {
			
			@Override
			public void onStart() {
				super.onStart();
				((BaseActivity)context).showLoading();
			}
			
			@Override
			public void onSuccess(String t) {
				((BaseActivity)context).dismissLoading();
				LogUtils.e(TAG, t.toString());
				if(!StringUtils.isNullOrEmpty(t)) {
					if("<".equals(String.valueOf(t.charAt(0)))) {
						((BaseActivity)context).showLoginDialog();
					}else {
						if(callback!=null) {
							callback.sucCallback(t);
						}
					}
				}
				super.onSuccess(t);
			}
			
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				((BaseActivity)context).dismissLoading();
				if(callback!=null) {
					callback.failCallback(errorNo, strMsg);
				}
				super.onFailure(t, errorNo, strMsg);
			}
		});
	}

}
