package com.xtrd.obdcar.utils;

import java.io.DataOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Locale;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.xtrd.obdcar.XtrdApp;

/**
 * 设备相关
 * 
 * @author start
 * 
 */
public class DeviceUtils {

	/**
	 * 获取设备id
	 * 
	 * @return
	 */
	public static String getDeviceId(Context context) {
		String deviceId = null;
		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (manager != null) {
			deviceId = manager.getDeviceId();
		}
		if (deviceId != null) {
			return deviceId;
		}
		deviceId = Secure.getString(context.getContentResolver(),
				Secure.ANDROID_ID);
		if (deviceId != null) {
			return deviceId;
		}
		deviceId = Installation.id(context);
		return deviceId;
	}

	public static int getSDK_INT() {
		int sdkINT = android.os.Build.VERSION.SDK_INT;
		return sdkINT;
	}

	// 判断是否为android2.2或以上版本
	public static Boolean isAndroid22() {
		boolean Android22 = true;

		int sdkINT = android.os.Build.VERSION.SDK_INT;
		if (sdkINT < 8) {
			Android22 = false;
		}

		return Android22;
	}

	// 系统语言
	public static String getLocalName() {
		Locale locale = Locale.getDefault();
		return locale.getDisplayName();
	}

	// 手机品牌
	public static String getBrand() {
		return android.os.Build.BRAND;
	}

	public static String getModel() {
		return android.os.Build.MODEL;
	}

	public static String getTimeZone() {
		String offset = null;

		Calendar cal = Calendar.getInstance();
		int timezone = cal.getTimeZone().getRawOffset() / 3600000;
		String top = "";
		if (Math.abs(timezone) < 10) {
			top = "0";
		}
		String fen = String
				.valueOf(cal.getTimeZone().getRawOffset() % 3600000 / 60000);
		if (2 > fen.length()) {
			fen = "0" + fen;
		} else {
			fen = fen.substring(0, 2);
		}
		if (timezone >= 0) {
			offset = "+" + top + timezone + ":" + fen;
		} else if (timezone < 0) {
			offset = "-" + top + Math.abs(timezone) + ":" + fen;
		}
		return offset;
	}

	public static String getLanguage() {
		Locale locale = Locale.getDefault();
		String language = locale.getLanguage();
		return language;
	}

	public static String getLocaleCountry() {
		Locale locale = Locale.getDefault();
		return locale.getCountry();
	}

	public static boolean isSIMcardAvailable() {
		TelephonyManager tm = (TelephonyManager) XtrdApp.getAppContext()
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (tm.getSimState() != TelephonyManager.SIM_STATE_READY) {
			return false;
		} else {
			return true;
		}
	}

	public static String getDeviceInfos(Context context) {
		String info = "<br/>";
		try {
			info += "<br/>应用版本 : " + getLocalAppVersion(context);
			info += "<br/>设备型号 : " + Build.PRODUCT;
			info += "<br/>系统版本 : " + Build.VERSION.RELEASE;
			info += "<br/>系统API等级  : " + Build.VERSION.SDK;
			info += "<br/>cpu信息 : " + Build.CPU_ABI;
			info += "<br/>版本号 : " + Build.DISPLAY;
			info += "<br/>分辨率 : " + Utils.getScreenHeight(context) + " * "
					+ Utils.getScreenWidth(context);
		} catch (Exception e) {
		}
		return info + "<br/><br/>";
	}

	/**
	 * 获取sdk api等级
	 */
	public static int getApiLevel() {
		int level = 7;
		try {
			level = Integer.parseInt(Build.VERSION.SDK);
		} catch (Exception e) {
		}
		return level;
	}

	/**
	 * cpu信息 armeabi-v7a
	 */
	public static String getCpu() {
		return Build.CPU_ABI;
	}

	/**
	 * 设备型号 meizu_mx2
	 * 
	 * @param context
	 * @return
	 */
	public static String getDeviceName(Context context) {
		return Build.PRODUCT;
	}

	/**
	 * 系统版本 4.2.1
	 * 
	 * @param context
	 * @return
	 */
	public static String getDeviceVersion(Context context) {
		return Build.VERSION.RELEASE;
	}

	/**
	 * 品牌 Meizu
	 * 
	 * @return
	 */
	public static String getDeviceBrand() {
		return Build.BRAND;
	}

	/**
	 * 版本号 Flyme OS 3.4.1 (A17506)
	 * 
	 * @param context
	 * @return
	 */
	public static String getDeviceVersionName(Context context) {
		return Build.DISPLAY;
	}

	
	/**
	 * imsi
	 * 
	 * @param context
	 * @return
	 */
	public static String getImsi(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getSubscriberId();
	}

	/**
	 * 获取设备版本
	 */
	public static String getPlatform() {
		String releaseversion = Build.VERSION.RELEASE;
		String version = "AR";
		if (releaseversion.contains("1.5")) {
			version = "AR_15";
		} else if (releaseversion.contains("1.6")) {
			version = "AR_16";
		} else if (releaseversion.contains("2.0")) {
			version = "AR_20";
		} else if (releaseversion.contains("2.1")) {
			version = "AR_21";
		} else if (releaseversion.contains("2.2")) {
			version = "AR_22";
		} else if (releaseversion.contains("2.3")) {
			version = "AR_23";
		} else if (releaseversion.contains("3.0")) {
			version = "AR_30";
		} else if (releaseversion.contains("3.1")) {
			version = "AR_31";
		} else if (releaseversion.contains("3.2")) {
			version = "AR_32";
		} else if (releaseversion.contains("4.0")) {
			version = "AR_40";
		}
		return version;
	}

	/**
	 * 获取运营商
	 * 
	 * @return
	 */
	public static String getOperator(Context context) {
		String ProvidersName = null;
		// 返回唯一的用户ID;就是这张卡的编号
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		;
		String IMSI = telephonyManager.getSubscriberId();
		if (StringUtils.isNullOrEmpty(IMSI)) {
			return "未知";
		}
		// IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
		if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
			ProvidersName = "中国移动";
		} else if (IMSI.startsWith("46001")) {

			ProvidersName = "中国联通";
		} else if (IMSI.startsWith("46003")) {

			ProvidersName = "中国电信";
		}
		return ProvidersName;

	}

	/**
	 * 获取本地app版本号
	 * 
	 * @param context
	 * @return
	 */
	public static String getLocalAppVersion(Context context) {
		PackageManager pm = context.getPackageManager();
		String version = null;
		try {
			version = pm.getPackageInfo("com.gsie.statistic", 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return version;
	}

	/**
	 * 手机ip地址
	 * 
	 * @return
	 */
	public static String getPhoneIp() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& inetAddress instanceof Inet4Address) {
						// if (!inetAddress.isLoopbackAddress() && inetAddress
						// instanceof Inet6Address) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (Exception e) {
		}
		return "";
	}

	public static boolean getRootAhth() {
		Process process = null;
		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes("exit\n");
			os.flush();
			int exitValue = process.waitFor();
			if (exitValue == 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (process != null)
					process.destroy();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取手机mac地址<br/>
	 * 错误返回12个0
	 */
	public static String getMacAddress(Context context) {
		// 获取mac地址：
		String macAddress = "000000000000";
		try {
			WifiManager wifiMgr = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = (null == wifiMgr ? null : wifiMgr
					.getConnectionInfo());
			if (null != info) {
				if (!TextUtils.isEmpty(info.getMacAddress()))
					macAddress = info.getMacAddress().replace(":", "");
				else
					return macAddress;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return macAddress;
		}
		return macAddress;
	}

	/**
	 * 获取网络环境
	 * 
	 * @return
	 */
	public static String getNetwork(Context context) {
		ConnectivityManager connectMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectMgr.getActiveNetworkInfo();
		if (info != null) {
			if (ConnectivityManager.TYPE_WIFI == info.getType()) {
				return "WIFI";
			} else if (ConnectivityManager.TYPE_MOBILE == info.getType()) {
				int subtype = info.getSubtype();
				if (TelephonyManager.NETWORK_TYPE_CDMA == subtype) {
					return "电信2G";
				} else if (TelephonyManager.NETWORK_TYPE_EVDO_0 == subtype
						|| TelephonyManager.NETWORK_TYPE_EVDO_A == subtype) {
					return "电信3G";
				} else if (TelephonyManager.NETWORK_TYPE_GPRS == subtype
						|| TelephonyManager.NETWORK_TYPE_EDGE == subtype) {
					return "移动2G";
				} else if (TelephonyManager.NETWORK_TYPE_UMTS == subtype
						|| TelephonyManager.NETWORK_TYPE_HSDPA == subtype) {
					return "联通3G";
				}/*
				 * else if(TelephonyManager.NETWORK_TYPE_HSUPA == subtype ||
				 * TelephonyManager.NETWORK_TYPE_HSPA == subtype) { return
				 * "联通3G"; }
				 */
			}
		}
		return null;
	}

}
