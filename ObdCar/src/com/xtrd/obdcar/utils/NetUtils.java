package com.xtrd.obdcar.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.xtrd.obdcar.XtrdApp;

public class NetUtils {

	public enum NetType {
		WIFI, MOBILE, UNKONW
	}

	public static NetType getNetType(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo == null) {
			return NetType.UNKONW;
		} else if (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return NetType.WIFI;
		} else {
			return NetType.MOBILE;
		}
	}

	public static boolean isWifiNet(Context context) {
		if (getNetType(context) == NetUtils.NetType.WIFI) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean IsNetworkAvailable() {
		boolean flag = false;
		ConnectivityManager cm = (ConnectivityManager) XtrdApp.getAppContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = null;
		if (cm != null) {
			netInfo = cm.getActiveNetworkInfo();
		}
		if (netInfo != null && netInfo.isAvailable() && netInfo.isConnected()) {
			flag = true;
		}

		return flag;
	}

}
