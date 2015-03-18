package com.xtrd.obdcar;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.umeng.analytics.MobclickAgent;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.DeviceUtils;
import com.xtrd.obdcar.utils.Utils;

public class WelcomeActivity extends Activity{

	private DataLoaderTask task;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Crashlytics.start(this);
		setContentView(R.layout.welcome);

		if( SettingLoader.getCreateShortcut(this) == 0 ) {
			if( !hasShortCut() ) {
				createShortcuts();
			}
			SettingLoader.setCreateShortcut(this,1);
		}
		MobclickAgent.setDebugMode(true);

		if (task != null) {
			task.cancel(true);
			task = null;
		}
		task = new DataLoaderTask();
		task.execute();
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}


	class DataLoaderTask extends AsyncTask<Void, Integer, Long> {

		@Override
		protected Long doInBackground(Void... arg0) {

			for (int i = 0; i < 10; i++) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Long result) {
			gotoGuide();
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			task = null;
			super.onPreExecute();
		}

	}


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



	private void gotoGuide() {
		Intent intent = null;
		if(SettingLoader.isFirstLaunch(this)) {
			intent = new Intent(WelcomeActivity.this, GuideActivity.class);
			SettingLoader.setFirstLaunch(this, false);
		}else {
			intent = new Intent(WelcomeActivity.this,BaseTabActivity.class);
		}
		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		finish();
	}

	private void createShortcuts() {
		Intent localIntent2 = new Intent("android.intent.action.MAIN");
		String str1 = "com.xtrd.obdcar.WelcomeActivity";
		localIntent2.setClassName(this, str1);
		localIntent2.addCategory("android.intent.category.LAUNCHER");
		Intent localIntent3 = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		localIntent3.putExtra("duplicate", false);
		localIntent3.putExtra("android.intent.extra.shortcut.INTENT",
				localIntent2);
		String str2 = getString(R.string.app_name);
		localIntent3.putExtra("android.intent.extra.shortcut.NAME", str2);
		Intent.ShortcutIconResource localShortcutIconResource = Intent.ShortcutIconResource
				.fromContext(this, R.drawable.ic_launcher);
		localIntent3.putExtra("android.intent.extra.shortcut.ICON_RESOURCE",
				localShortcutIconResource);
		sendBroadcast(localIntent3);
	}

	private boolean hasShortCut( ) { 
		boolean exist = false;
		String url = "";  
		int sdk_int = DeviceUtils.getSDK_INT();  
		if(sdk_int < 8){  
			url = "content://com.android.launcher.settings/favorites?notify=true";  
		} else {  
			url = "content://com.android.launcher2.settings/favorites?notify=true";  
		}  
		ContentResolver resolver = getContentResolver();  
		Cursor cursor = resolver.query(Uri.parse(url), null, "title=?",  
				new String[] {getString(R.string.app_name)}, null);  

		if (cursor != null && cursor.moveToFirst()) {  
			exist = true; 
		} 

		if( cursor != null ) {
			cursor.close();
			cursor = null;
		}

		return exist;  
	}

}
