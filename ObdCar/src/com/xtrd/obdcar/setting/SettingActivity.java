package com.xtrd.obdcar.setting;

import java.io.File;
import java.util.HashSet;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.http.PreferencesCookieStore;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.ToggleButton;
import cn.jpush.android.api.JPushInterface;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.XtrdApp;
import com.xtrd.obdcar.active.CarListActivity;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.CacheConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.CarOpenHelper;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.VersionEntity;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.passport.LoginActivity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.NetUtils;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.view.ObdDialog;

public class SettingActivity extends BaseActivity implements OnCheckedChangeListener{

	protected static final String TAG = "SettingActivity";

	private LinearLayout layout_info,layout_car_detail,layout_car,layout_car_bind,layout_car_unbind,layout_auth,
	layout_notify_manage,layout_feedback,layout_offline,layout_update,layout_help;

	private ToggleButton btn_refresh;
	private ToggleButton btn_hide_trip;
	private Button btn_quit;


	public SettingActivity() {
		layout_id = R.layout.activity_setting;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg, R.string.title_setting, 0, 0);
		initView();
		regClick();
	}



	private void initView() {
		layout_info = (LinearLayout)findViewById(R.id.layout_info);
		layout_car_detail = (LinearLayout)findViewById(R.id.layout_car_detail);
		layout_car = (LinearLayout)findViewById(R.id.layout_car);
		layout_car_bind = (LinearLayout)findViewById(R.id.layout_car_bind);
		layout_car_unbind = (LinearLayout)findViewById(R.id.layout_car_unbind);
		layout_auth = (LinearLayout)findViewById(R.id.layout_auth);
		layout_notify_manage = (LinearLayout)findViewById(R.id.layout_notify_manage);
		layout_feedback = (LinearLayout)findViewById(R.id.layout_feedback);
		btn_refresh = (ToggleButton)findViewById(R.id.btn_refresh);
		btn_refresh.setChecked(SettingLoader.isRefresh(this));
		btn_refresh.setOnCheckedChangeListener(this);
		btn_hide_trip = (ToggleButton)findViewById(R.id.btn_hide_trip);
		btn_hide_trip.setChecked(SettingLoader.isTripHide(this));
		btn_hide_trip.setOnCheckedChangeListener(this);
		layout_offline = (LinearLayout)findViewById(R.id.layout_offline);
		layout_update = (LinearLayout)findViewById(R.id.layout_update);
		layout_help = (LinearLayout)findViewById(R.id.layout_help);
		btn_quit = (Button)findViewById(R.id.btn_quit);

	}

	private void regClick() {
		layout_info.setOnClickListener(this);
		layout_car_detail.setOnClickListener(this);
		layout_car.setOnClickListener(this);
		layout_car_bind.setOnClickListener(this);
		layout_car_unbind.setOnClickListener(this);
		layout_auth.setOnClickListener(this);
		layout_notify_manage.setOnClickListener(this);
		layout_feedback.setOnClickListener(this);
		btn_refresh.setOnClickListener(this);
		btn_hide_trip.setOnClickListener(this);
		layout_offline.setOnClickListener(this);
		layout_update.setOnClickListener(this);
		layout_help.setOnClickListener(this);
		btn_quit.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		super.onClick(v);
		Intent intent = null;
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.layout_info:
			intent = new Intent(this,InfoActivity.class);
			startActivity(intent);
			break;
		case R.id.layout_car_detail:
			intent = new Intent(this,CarDetailActivity.class);
			startActivity(intent);
			break;
		case R.id.layout_car:
			if(!NetUtils.IsNetworkAvailable()) {
				Utils.showNetTips(this);
				return;
			}

			intent = new Intent(this,DefaultCarActivity.class);
			intent.putExtra("from", true);
			startActivity(intent);
			break;
		case R.id.layout_car_bind:
			if(SettingLoader.hasLogin(this)) {
				intent = new Intent(this,CarListActivity.class);
			}else {
				intent = new Intent(this,LoginActivity.class);
			}
			startActivity(intent);
			break;
		case R.id.layout_car_unbind:
			if(!NetUtils.IsNetworkAvailable()) {
				Utils.showNetTips(this);
				return;
			}

			intent = new Intent(this,DefaultCarActivity.class);
			intent.putExtra("from", false);
			startActivity(intent);
			break;
		case R.id.layout_auth:
			intent = new Intent(this,AuthManageActivity.class);
			startActivity(intent);
			break;
		case R.id.layout_notify_manage:
			intent = new Intent(this,NotifyManageActivity.class);
			startActivity(intent);
			break;
		case R.id.layout_feedback:
			intent = new Intent(this,FeedbackActivity.class);
			startActivity(intent);
			break;
		case R.id.layout_offline:
			intent = new Intent(this,OffLineActivity.class);
			startActivity(intent);
			break;
		case R.id.layout_update:
			if(!NetUtils.IsNetworkAvailable()) {
				Utils.showNetTips(this);
				return;
			}
			getUpdateInfo();
			break;
		case R.id.layout_help:
			intent = new Intent(this,HelpActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_quit:
			showQuitDialog();
			break;
		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.btn_refresh:
			SettingLoader.setRefresh(this,isChecked);
			break;
		case R.id.btn_hide_trip:
			hideTrip(isChecked?"1":"0");
			
			break;
		default:
			break;
		}
	}


	private void hideTrip(final String type) {
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, SettingLoader.getVehicleId(this));
		params.put(ParamsKey.TYPE, type);
		NetRequest.requestUrl(this, ApiConfig.getRequestUrl(ApiConfig.Trip_Hide_Url), params, new NetRequest.NetCallBack() {

			@Override
			public void sucCallback(String str) {
				try {
					if(!StringUtils.isNullOrEmpty(str)) {
						JSONObject json = new JSONObject(str);
						if(json.has("status")) {
							int status = json.getInt("status");
							if(1==status) {
								SettingLoader.setTripHide(SettingActivity.this,("0".equals(type)?false:true));
								XtrdApp.sendMsg(XtrdApp.ID_HIDE_TRIP);
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void failCallback(int errorNo, String strMsg) {

			}
		});
	}

	/**
	 *  版本更新
	 * @param vehicleId
	 */
	private void getUpdateInfo() {
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.App_Version, Utils.getLocalAppVersion(this));
		params.put(ParamsKey.App_Type, "B");
		fh.post(ApiConfig.getRequestUrl(ApiConfig.APP_Update_Url) ,params, new AjaxCallBack<String>() {

			private String msg;

			@Override
			public void onStart() {
				showLoading();
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				dismissLoading();
				if("<".equals(String.valueOf(t.charAt(0)))) {
					showLoginDialog();
				}else {
					try {
						if(!StringUtils.isNullOrEmpty(t)) {
							JSONObject json = new JSONObject(t);
							int errorCode = 0;
							if(json.has("errorCode")) {
								errorCode = json.getInt("errorCode");
							}
							if(ApiConfig.InvilidSession==errorCode) {
								showLoginDialog();
							}else {
								if(json.has("status")) {
									int status = json.getInt("status");
									VersionEntity version = null;
									if(1==status) {
										if(json.has("result")) {
											json = json.getJSONObject("result");
											version = new VersionEntity();
											version.parser(json);
										}else {
											Utils.showToast(SettingActivity.this, "已经是最新版本");
										}
									}else {
										msg = json.getString("message");
										Utils.showToast(SettingActivity.this, msg);
									}

									if(checkVersion(version)) {
										showUpdateDialog(version);
									}
								}
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				super.onSuccess(t);
			}


			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				Utils.showToast(SettingActivity.this,getResources().getString(R.string.data_load_fail));
				dismissLoading();
			}

		});
	}

	/**
	 * 版本检测更新
	 * @param version
	 * @return
	 */
	private boolean checkVersion(VersionEntity version) {
		if(version==null) {
			return false;
		}
		String localAppVersion = Utils.getLocalAppVersion(this);
		String[] split = localAppVersion.split("\\.");
		if(version.getMajorVersion()>Integer.parseInt(split[0])) {
			return true;
		}else if(version.getMajorVersion()==Integer.parseInt(split[0])){
			if(version.getMinorVersion()>Integer.parseInt(split[1])) {
				return true;
			}else if(version.getMinorVersion()==Integer.parseInt(split[1])){
				if(version.getRevision()>Integer.parseInt(split[2])) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 弹出更新dialog
	 * @param version
	 */
	private void showUpdateDialog(final VersionEntity version) {
		ObdDialog tipsDialog = new ObdDialog(SettingActivity.this)
		.setTitle(getResources().getString(R.string.update_title))
		.setMessage(String.format(getResources().getString(R.string.update_content),version.getMajorVersion()+"."+version.getMinorVersion()+"."+version.getRevision(),""))
		.setPositiveButton(getResources().getString(R.string.app_update_cancel),new ObdDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog,
					int which) {
				dialog.dismiss();
			}

		})
		.setNegativeButton(getResources().getString(R.string.app_update_ok),new ObdDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				downloadFile(version.getUri());
				dialog.dismiss();
			}
		});

		tipsDialog.setCancelable(false);
		if(!isFinishing()) {
			tipsDialog.show();
		}
	}

	/**
	 * 文件下载
	 * @param downloadurl
	 */
	private void downloadFile(String downloadurl){
		FinalHttp fh = new FinalHttp();
		String dbFile = System.currentTimeMillis()+".apk";
		File file = new File(CacheConfig.getDownloadDir(),dbFile);
		fh.download(downloadurl, file.getAbsolutePath(),true, new AjaxCallBack<File>() {
			private Notification notif;
			private NotificationManager manager;
			@Override
			public void onStart() {
				//点击通知栏后打开的activity  
				Intent intent = new Intent("download_apk");
				intent.putExtra("download", true);
				manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				//				PendingIntent pIntent = PendingIntent.getActivity(HomeActivity.this, 0, intent, 0);
				PendingIntent pIntent = PendingIntent.getBroadcast(SettingActivity.this, 1, intent, 0);
				notif = new Notification();
				notif.icon = R.drawable.ic_launcher;
				//				notif.tickerText = String.format(getResources().getString(R.string.download_text), "0%");
				notif.when = java.lang.System.currentTimeMillis();
				// 通知栏显示所用到的布局文件
				notif.contentView = new RemoteViews(getPackageName(),R.layout.content_view);
				notif.contentView.setTextViewText(R.id.content_view_text1, String.format(getResources().getString(R.string.download_text), "0%"));  
				notif.contentIntent = pIntent;
				notif.flags = Notification.FLAG_NO_CLEAR;
				manager.notify(0, notif);
				super.onStart();
			}

			@Override
			public void onLoading(long count, long current) {
				notif.contentView.setTextViewText(R.id.content_view_text1, String.format(getResources().getString(R.string.download_text), (int)(current*100/count)+"%"));  
				notif.contentView.setProgressBar(R.id.content_view_progress, 100,  (int)(current*100/count), false);  
				manager.notify(0, notif);  
				super.onLoading(count, current);
			}

			@Override
			public void onSuccess(File t) {
				manager.cancel(0);
				if(t!=null&&t.exists()) {
					Utils.installApk(SettingActivity.this, t.getAbsolutePath());
				}
				super.onSuccess(t);
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
			}
		});
	}


	private void showQuitDialog() {
		new ObdDialog(this).setTitle("温馨提示")
		.setMessage("确定退出当前用户吗？")
		.setPositiveButton(getResources().getString(R.string.btn_cancel), new ObdDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		})
		.setNegativeButton(getResources().getString(R.string.btn_confirm), new ObdDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				PreferencesCookieStore store = new PreferencesCookieStore(SettingActivity.this);
				store.clear();
				CarOpenHelper openHelper = CarOpenHelper.getInstance(SettingActivity.this);
				openHelper.deleteCars();
				//					RuleBreakOpenHelper.getInstance(SettingActivity.this).deleteRuleByVehicleId(SettingLoader.getVehicleId(SettingActivity.this));
				SettingLoader.setHasLogin(SettingActivity.this, false);
				SettingLoader.clearAll(SettingActivity.this);
				JPushInterface.setAliasAndTags(SettingActivity.this, "", new HashSet<String>());
				XtrdApp.exitApp();
			}
		}).show();

	}


}
