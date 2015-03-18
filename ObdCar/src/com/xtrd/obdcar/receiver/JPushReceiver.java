package com.xtrd.obdcar.receiver;

import java.util.HashSet;
import java.util.Set;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.xtrd.obdcar.BaseTabActivity;
import com.xtrd.obdcar.HomeActivity;
import com.xtrd.obdcar.XtrdApp;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.MessageOpenHelper;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.EMMessage;
import com.xtrd.obdcar.merchant.MerchantActivity;
import com.xtrd.obdcar.merchant.MerchantDetailActivity;
import com.xtrd.obdcar.nearby.NearShopActivity;
import com.xtrd.obdcar.obdservice.OneKeyHelpActivity;
import com.xtrd.obdcar.oil.OilReportActivity;
import com.xtrd.obdcar.self.NotificationActivity;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.log.LogUtils;
import com.xtrd.obdcar.vc.ProfessorQAActivity;
import com.xtrd.obdcar.vc.TroubleCodeActivity;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
@SuppressLint("NewApi")
public class JPushReceiver extends BroadcastReceiver {
	private static final String TAG = "JPushReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		//		Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
			//send the Registration Id to your server...
			sendRegId(regId,context);

		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
			Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
			processCustomMessage(context, bundle);

		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
			String str = bundle.getString(JPushInterface.EXTRA_EXTRA);
			LogUtils.e(TAG, "receive " + str);
			try {
				JSONObject json = new JSONObject(str);
				if(json!=null&&json.has("type")) {
					int type = json.getInt("type");
					switch (type) {
					case 41:
						String value = bundle.getString(JPushInterface.EXTRA_ALERT);
						MessageOpenHelper.getInstance(context).insertItem(new EMMessage(0, value, System.currentTimeMillis(),1));
						XtrdApp.sendMsg(XtrdApp.ID_RECEIVE_QA_MSG);
						break;
					default:
						break;
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
			Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
			String str = bundle.getString(JPushInterface.EXTRA_EXTRA);
			if(!StringUtils.isNullOrEmpty(str)) {
				try {
					JSONObject json = new JSONObject(str);
					if(json!=null&&json.has("type")) {
						int type = json.getInt("type");
						switch (type) {
						case 41:
							gotoDest(context,ProfessorQAActivity.class,0);
							break;
						case 1:
							gotoDest(context,TroubleCodeActivity.class,1);
							break;
						case 2:
							gotoDest(context,NotificationActivity.class,4);
							break;
						case 3:
							if(XtrdApp.isAppOnForeground()) {
								if(!XtrdApp.isActivityOnForeground(MerchantDetailActivity.class.getName())) {
									//打开自定义的Activity
									Intent i = new Intent(context, MerchantDetailActivity.class);
									//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
									context.startActivity(i);
								}
							}else {
								Intent start = new Intent(context, BaseTabActivity.class);
								//							//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								start.putExtra("currentTab", 3);
								start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
								//打开自定义的Activity
								Intent qa = new Intent(context, MerchantDetailActivity.class);
								qa.putExtra(ParamsKey.MERCHANTID,json.has("id")?json.getInt("id"):0);
								//							//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								qa.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
								//打开自定义的Activity
								Intent[] intents = new Intent[]{start,qa};
								context.startActivities(intents);
							}
							break;
						case 4:
							gotoDest(context,MerchantActivity.class,3);
							break;
						case 5:
							if(XtrdApp.isAppOnForeground()) {
								if(!XtrdApp.isActivityOnForeground(NearShopActivity.class.getName())) {
									XtrdApp.exitOtherActivity();
									Intent start = new Intent(context, NearShopActivity.class);
									//								//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
									start.putExtra("keyword", "加油站");
									//打开自定义的Activity
									context.startActivity(start);
									new AsyncTask<Void, Integer, Long>(){

										@Override
										protected Long doInBackground(Void... params) {
											try {
												Thread.sleep(500);
											} catch (InterruptedException e) {
												e.printStackTrace();
											}
											XtrdApp.sendMsg(XtrdApp.ID_TAB_RESET,2);
											return null;
										}}.execute();

								}
							}else {
								Intent start = new Intent(context, BaseTabActivity.class);
								start.putExtra("currentTab", 2);
								//							//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
								//打开自定义的Activity
								Intent qa = new Intent(context, NearShopActivity.class);
								qa.putExtra("keyword", "加油站");
								qa.putExtra("latitude", SettingLoader.getCarLatLng(context).latitude);
								qa.putExtra("longitude", SettingLoader.getCarLatLng(context).longitude);
								qa.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
								//打开自定义的Activity
								Intent[] intents = new Intent[]{start,qa};
								context.startActivities(intents);
							}
							break;
						case 6:
							gotoDest(context,OneKeyHelpActivity.class,-1);
							break;
						case 7:
							gotoDest(context,NotificationActivity.class,4);

							break;
						case 8:
							gotoDest(context,OilReportActivity.class,2);

							break;
						default:
							break;
						}
					}else{
						Intent bIntent = new Intent(context, BaseTabActivity.class);
						bIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
						context.startActivity(bIntent);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else {
				Intent bIntent = new Intent(context, BaseTabActivity.class);
				bIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
				context.startActivity(bIntent);
			}

		} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
			Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
			//在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

		} else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
			boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
			Log.e(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
		} else {
			Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
		}
	}



	private void gotoDest(Context context,Class classes,final int currentTab) {
		if(XtrdApp.isAppOnForeground()) {
			if(!XtrdApp.isActivityOnForeground(classes.getName())) {
				XtrdApp.exitOtherActivity();
				Intent start = new Intent(context, classes);
				//				//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
				//打开自定义的Activity
				context.startActivity(start);
				new AsyncTask<Void, Integer, Long>(){

					@Override
					protected Long doInBackground(Void... params) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						XtrdApp.sendMsg(XtrdApp.ID_TAB_RESET,currentTab);
						return null;
					}}.execute();

			}
		}else {
			Intent start = new Intent(context, BaseTabActivity.class);
			if(-1!=currentTab) {
				start.putExtra("currentTab", currentTab);
			}
			//			//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
			//打开自定义的Activity
			Intent qa = new Intent(context, classes);
			//			//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			qa.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
			//打开自定义的Activity
			Intent[] intents = new Intent[]{start,qa};
			context.startActivities(intents);
		}
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} 
			else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}

	//send msg to MainActivity
	private void processCustomMessage(Context context, Bundle bundle) {
		String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
		Intent msgIntent = new Intent(HomeActivity.MESSAGE_RECEIVED_ACTION);
		msgIntent.putExtra(HomeActivity.KEY_MESSAGE, message);
		if (!StringUtils.isNullOrEmpty(extras)) {
			try {
				JSONObject extraJson = new JSONObject(extras);
				if (null != extraJson && extraJson.length() > 0) {
					msgIntent.putExtra(HomeActivity.KEY_EXTRAS, extras);
				}
			} catch (JSONException e) {

			}
		}
		context.sendBroadcast(msgIntent);
	}

	private void sendRegId(String regId,final Context context) {

		if(StringUtils.isNullOrEmpty(SettingLoader.getLoginName(context))) {
			SettingLoader.setJpushId(context,regId);
		}else {
			Set<String> tags = new HashSet<String>();
			tags.add(SettingLoader.getChannel(context));
			JPushInterface.setAliasAndTags(context, SettingLoader.getLoginName(context), tags,new TagAliasCallback() {

				@Override
				public void gotResult(int arg0, String arg1, Set<String> arg2) {
					LogUtils.e(TAG, arg0 + " " + arg1 + " " + arg2);
				}
			});
			FinalHttp fh = new FinalHttp();
			AjaxParams params = new AjaxParams();
			params.put(ParamsKey.RegistrationId, regId);
			params.put(ParamsKey.Alias, SettingLoader.getLoginName(context));
			params.put(ParamsKey.Tag, SettingLoader.getChannel(context));
			params.put(ParamsKey.DeviceType, ParamsKey.Android);
			fh.post(ApiConfig.getRequestUrl(ApiConfig.Jpush_Reg_Url) ,params, new AjaxCallBack<String>() {

				private String msg;

				@Override
				public void onSuccess(String t) {
					LogUtils.e(TAG,"jpush"+t.toString());
					try {
						if(!StringUtils.isNullOrEmpty(t)) {
							JSONObject json = new JSONObject(t);
							if(json.has("status")) {
								int status = json.getInt("status");
								if(1==status) {
									SettingLoader.setJpushId(context,null);
								}else {
									msg = json.getString("message");
									LogUtils.e(TAG, msg);
								}
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					super.onSuccess(t);
				}

				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					super.onFailure(t, errorNo, strMsg);
					LogUtils.e(TAG, "error msg "+strMsg);
				}
			});
		}
	}
}
