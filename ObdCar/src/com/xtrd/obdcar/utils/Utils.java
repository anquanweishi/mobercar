package com.xtrd.obdcar.utils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.xtrd.obdcar.BaseTabActivity;
import com.xtrd.obdcar.XtrdApp;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.view.ObdDialog;
import com.xtrd.obdcar.view.ObdListDialog;

public class Utils {


	public static void showNotification(Context context, int id,
			boolean autoClear,String title,String msg) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notification = new Notification(R.drawable.ic_launcher,
				title, System.currentTimeMillis());
		if (autoClear) {
			notification.flags = Notification.FLAG_AUTO_CANCEL;
		} else {
			notification.flags = Notification.FLAG_ONGOING_EVENT;
		}
		Intent intent = new Intent(context,BaseTabActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("fromNotify", true);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		notification.defaults = Notification.DEFAULT_ALL;
		notification.setLatestEventInfo(context, title, msg,
				contentIntent);
		notificationManager.notify(id, notification);
	}

	public static void deleteNotification(Context context, int id) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(id);
	}
	public static void deleteAllNotification(Context context) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancelAll();
	}



	public static boolean isSDCardEnable() {
		String SDState = Environment.getExternalStorageState();
		if (SDState != null
				&& SDState.equals(android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	/**
	 * 将dip转换为pix
	 * 
	 * @param context
	 * @param dip
	 * @return
	 */
	public static int dipToPixels(Context context, float dip) {
		return (int) (context.getResources().getDisplayMetrics().density * dip);
	}

	/**
	 * 获取屏幕宽度(像素)
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		return windowManager.getDefaultDisplay().getWidth();
	}

	/**
	 * 获取屏幕高度(像素)
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context) {
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		return windowManager.getDefaultDisplay().getHeight();
	}

	/**
	 * 获取状态栏高度
	 * 
	 * @param window
	 * @return
	 */
	public static int getStatusBarHeight(Window window) {
		Rect outRect = new Rect();
		window.getDecorView().getWindowVisibleDisplayFrame(outRect);
		return outRect.top;
	}

	public static int getSystemSdk() {
		try {
			return Integer.valueOf(Build.VERSION.SDK);
		} catch (Exception e) {
		}
		return 7;
	}

	public static boolean hasSmartBar() {

		try {
			// 新型号可用反射调用 Build.hasSmartBar()
			Method method = Class.forName("android.os.Build").getMethod(
					"hasSmartBar");
			return ((Boolean) method.invoke(null)).booleanValue();
		} catch (Exception e) {
		}
		// 反射不到 Build.hasSmartBar() ，则用 Build.DEVICE 判断
		if (Build.DEVICE.equals("mx2")) {
			return true;
		} else if (Build.DEVICE.equals("mx") || Build.DEVICE.equals("m9")) {
			return false;
		}
		return false;
	}

	// base64转码为string

	public static String getMetaValue(Context context, String metaKey) {
		Bundle metaData = null;
		String apiKey = null;
		if (context == null || metaKey == null) {
			return null;
		}
		try {
			ApplicationInfo ai = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			if (null != ai) {
				metaData = ai.metaData;
			}
			if (null != metaData) {
				apiKey = metaData.getString(metaKey);
			}
		} catch (NameNotFoundException e) {

		}
		return apiKey;
	}

	public static void showToast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}

	public static void showToast(Context context, int msgId) {
		Toast.makeText(context, context.getResources().getString(msgId),
				Toast.LENGTH_LONG).show();
	}




	/**
	 * 是否包含-
	 * @deprecated
	 * @param active
	 * @return
	 */
	public static boolean hasSingal(String active) {
		Pattern pattern = Pattern.compile("([^-]+)");
		Matcher matcher = pattern.matcher(active);
		return matcher.matches();
	}

	/**
	 * 验证序列号
	 * 
	 * @param active
	 * @return
	 */
	public static boolean isVilidActive(String active) {
		Pattern pattern = Pattern
				.compile("([a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4})");
		Matcher matcher = pattern.matcher(active);
		return matcher.matches();
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
			version = pm.getPackageInfo("com.xtrd.obdcar.tumi", 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return version;
	}

	/**
	 * 浏览器下载文件
	 * @param download_url
	 */
	public static void openUrl(Context context,String url) {
		if(StringUtils.isNullOrEmpty(url)) {
			return;
		}
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		Uri downUrl = Uri.parse(url);
		intent.setData(downUrl);
		try {
			context.startActivity(intent);
		} catch (Exception e) {
			intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			context.startActivity(intent);
		}
	}

	public static void installApk(Context context, String filepath) {
		File file = new File(filepath);
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		String type = "application/vnd.android.package-archive";
		intent.setDataAndType(Uri.fromFile(file), type);
		context.startActivity(intent);

	}

	public static List<String> stringsToList(String[] paramArrayOfString) {
		if ((paramArrayOfString == null) || (paramArrayOfString.length == 0))
			return null;
		ArrayList<String> localArrayList = new ArrayList<String>();
		for (int i = 0; i < paramArrayOfString.length; ++i)
			localArrayList.add(paramArrayOfString[i]);
		return localArrayList;
	}

	/**
	 * 短信发送
	 * 
	 * @param context
	 * @param phones
	 * @param content
	 */
	public static void sendSMS(Context context, String phones, String content) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.putExtra("address", phones);
		intent.putExtra("sms_body", content);
		intent.setType("vnd.android-dir/mms-sms");
		context.startActivity(intent);
	}

	/**
	 * 打电话
	 * 
	 * @param context
	 * @param phone
	 */
	public static void makePhone(Context context, String phone) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:" + phone));
		context.startActivity(intent);

	}

	public static void showPhoneTips(final Context context,final String phone) {
		new ObdDialog(context).setTitle("温馨提示")
		.setMessage("确定呼叫"+phone)
		.setPositiveButton(context.getResources().getString(R.string.btn_cancel), new ObdDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		})
		.setNegativeButton(context.getResources().getString(R.string.btn_confirm), new ObdDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Utils.makePhone(context, phone);
			}
		}).show();
	}

	public static void showPhoneListTips(final Context context,final String[] phones) {
		new ObdListDialog(context).setTitle("温馨提示")
		.setList(phones)
		.setItemButton(new ObdListDialog.OnClickListener() {

			@Override
			public void onClick(String value) {
				Utils.makePhone(context, value);
			}
		})
		.show();
	}


	public static void showNetTips(final Context context) {
		new ObdDialog(context).setTitle("温馨提示")
		.setMessage("检测到当前无网络，请设置网络连接")
		.setPositiveButton(context.getResources().getString(R.string.btn_cancel), new ObdDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				XtrdApp.exitApp();
			}
		})
		.setNegativeButton(context.getResources().getString(R.string.btn_set_net), new ObdDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					Intent intent=null;
					//判断手机系统的版本  即API大于10 就是3.0或以上版本 
					if(android.os.Build.VERSION.SDK_INT>10){
						intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
					}else{
						intent = new Intent();
						ComponentName component = new ComponentName("com.android.settings","com.android.settings.WirelessSettings");
						intent.setComponent(component);
						intent.setAction("android.intent.action.VIEW");
					}
					context.startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
					Utils.showToast(context, R.string.net_manual_set);
				}	
			}
		}).show();
	}


	/**
	 * 验证邮箱
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(final String str) {
		String regex = "^[a-zA-Z0-9]{1,}[a-zA-Z0-9\\_\\.\\-]{0,}@(([a-zA-Z0-9]){1,}\\.){1,3}[a-zA-Z0-9]{0,}[a-zA-Z]{1,}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}


	/**
	 * 手机号验证
	 * 
	 * @param  str
	 * @return 验证通过返回true
	 */
	public static boolean isMobile(String str) { 
		Pattern p = null;  
		Matcher m = null;  
		boolean b = false;   
		p = Pattern.compile("^[1][3,4,5,8][0-9]{9}$"); // 验证手机号  
		m = p.matcher(str);  
		b = m.matches();   
		return b;  
	}
	/**
	 * 电话号码验证
	 * 
	 * @param  str
	 * @return 验证通过返回true
	 */
	public static boolean isPhone(String str) { 
		Pattern p1 = null,p2 = null;
		Matcher m = null;
		boolean b = false;  
		p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$");  // 验证带区号的
		p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");         // 验证没有区号的
		if(str.length() >9)
		{	m = p1.matcher(str);
		b = m.matches();  
		}else{
			m = p2.matcher(str);
			b = m.matches(); 
		}  
		return b;
	}


	public static String formatDataSize(int size) {
		String ret = "";
		if (size < (1024 * 1024)) {
			ret = String.format("%dK", size / 1024);
		} else {
			ret = String.format("%.1fM", size / (1024 * 1024.0));
		}
		return ret;
	}

	/**
	 * 获取设备信息
	 * @param context
	 * @return
	 */
	public static String getDeviceInfo(Context context) {
		try{
			org.json.JSONObject json = new org.json.JSONObject();
			android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);

			String device_id = tm.getDeviceId();

			android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);

			String mac = wifi.getConnectionInfo().getMacAddress();
			json.put("mac", mac);

			if( TextUtils.isEmpty(device_id) ){
				device_id = mac;
			}

			if( TextUtils.isEmpty(device_id) ){
				device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
			}

			json.put("device_id", device_id);

			return json.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

}
