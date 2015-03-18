package com.xtrd.obdcar;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import cn.jpush.android.api.JPushInterface;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.xtrd.obdcar.utils.log.LogUtils;

public class XtrdApp extends Application {
	protected static final String TAG = "BrowserApp";

	public static final int ID_LOGIN = 1;
	public static final int ID_CAR_BIND = 2;
	public static final int ID_SET_DEFAULT = 3;
	public static final int ID_REFRESH_ILLEGAL = 4;
	public static final int ID_HIDE_TRIP = 5;
	public static final int ID_RECEIVE_QA_MSG = 6;
	public static final int ID_TAB_RESET = 7;
	public static final int ID_CARINFO_GET = 8;

	public static final int ID_BIND_CAR = 9;

	private static Context appContext;
	public static ArrayList<Activity> allActivity = new ArrayList<Activity>();
	public static ArrayList<Handler> handlers = new ArrayList<Handler>();
	private static XtrdApp mInstance;

	public LocationClient mLocationClient;
	public MyLocationListener mMyLocationListener;

	@Override
	public void onCreate() {
		mInstance = this;
		appContext = this.getApplicationContext();
		
		//百度
		mLocationClient = new LocationClient(appContext);
		initLocationOption();
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		
		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		SDKInitializer.initialize(appContext);
		JPushInterface.setDebugMode(false); 	// 设置开启日志,发布时请关闭日志
		if(JPushInterface.isPushStopped(getApplicationContext())) {
			JPushInterface.init(getApplicationContext());
		}
		super.onCreate();
	}
	
	private void initLocationOption(){
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);//设置定位模式
		option.setCoorType("bd09ll");//返回的定位结果是百度经纬度，默认值gcj02
		option.setScanSpan(1000);//设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
	}

	public static XtrdApp getInstance() {
		return mInstance;
	}

	public static Context getAppContext() {
		return appContext;
	}

	public static void addHandler(Handler handler) {
		handlers.add(handler);
	}

	public static void sendMsg(int what) {
		for(Handler handler : handlers) {
			if(handler!=null) {
				handler.sendEmptyMessage(what);
			}
		}
	}
	public static void sendMsg(int what,int m) {
		for(Handler handler : handlers) {
			if(handler!=null) {
				Message msg = Message.obtain();
				msg.what = what;
				msg.arg1 = m;
				handler.sendMessage(msg);
			}
		}
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public static boolean isAppOnForeground() {
		ActivityManager activityManager = (ActivityManager) appContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
		if (tasksInfo.size() > 0) {
			// 应用程序位于堆栈的顶层
			if ("com.xtrd.obdcar".equals(tasksInfo.get(0).topActivity
					.getPackageName())) {
				return true;
			}
		}
		return false;
	}

	public static boolean isActivityOnForeground(Activity activity) {
		ActivityManager activityManager = (ActivityManager) appContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
		if (tasksInfo.size() > 0) {
			// activity位于堆栈的顶层
			if (activity.getClass().getName()
					.equals(tasksInfo.get(0).topActivity.getClassName())) {
				return true;
			}
		}
		return false;
	}
	public static boolean isActivityOnForeground(String name) {
		ActivityManager activityManager = (ActivityManager) appContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
		if (tasksInfo.size() > 0) {
			// activity位于堆栈的顶层
			if (name.equals(tasksInfo.get(0).topActivity.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public static void exitApp() {
		for (Activity activity : allActivity) {
			activity.finish();
		}
		allActivity.clear();
		handlers.clear();
	}

	public static void exitOtherActivity() {
		for (Activity activity : allActivity) {
			if(!(activity instanceof BaseTabActivity)&&!(activity.getParent() instanceof BaseTabActivity)) {
				activity.finish();
			}
		}
	}
	
	///////////////////////////////////////////
	public LocationCallBack callback;
	public LocationDetailCallBack dcallback;
	public LocationAddressCallBack addcallback;
	/**
	 * 实现实位回调监听
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			LogUtils.e(TAG, "location" + location);
			if(location!=null) {
				if(callback!=null) {
					callback.callback(location.getLongitude(),location.getLatitude(),location.getCity());
				}
				if(addcallback!=null) {
					addcallback.callback(location.getLongitude(),location.getLatitude(),location.getAddrStr());
				}
				if(dcallback!=null) {
					dcallback.callback(location.getAddrStr());
				}
				mLocationClient.stop();
			}
		}
	}
	
	
	public void getCurrentLocation(LocationCallBack callback) {
		this.callback = callback;
		if(!mLocationClient.isStarted()) {
			mLocationClient.start();
		}else {
			mLocationClient.requestLocation();
		}
	}
	public void getCurrentLocation(LocationDetailCallBack callback) {
		this.dcallback = callback;
		if(!mLocationClient.isStarted()) {
			mLocationClient.start();
		}else {
			mLocationClient.requestLocation();
		}
	}
	
	public void getCurrentLocation(LocationAddressCallBack callback) {
		this.addcallback = callback;
		if(!mLocationClient.isStarted()) {
			mLocationClient.start();
		}else {
			mLocationClient.requestLocation();
		}
	}
	
	public interface LocationCallBack {
		void callback(double longitude,double latitude,String city);
	}
	public interface LocationDetailCallBack {
		void callback(String loc);
	}
	public interface LocationAddressCallBack {
		void callback(double longitude,double latitude,String loc);
	}

}
