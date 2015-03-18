package com.xtrd.obdcar.obdservice;


import java.util.ArrayList;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.XtrdApp;
import com.xtrd.obdcar.adapter.HelpAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.HelpItem;
import com.xtrd.obdcar.entity.OnekeyHelp;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.setting.MyReservationActivity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.view.ObdDialog;

public class OneKeyHelpActivity extends BaseActivity {

	private LinearLayout layout_button;
	private ListView listView;
	private TextView text_onekey_help;
	protected ArrayList<OnekeyHelp> list = new ArrayList<OnekeyHelp>();

	public OneKeyHelpActivity() {
		layout_id = R.layout.activity_onekey_help;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg, R.string.title_onekey_help, R.string.btn_send, 0);
		initView();
		getViewData();
	}



	private void initView() {
		btn_right.setVisibility(View.GONE);
		layout_button = (LinearLayout)findViewById(R.id.layout_button);
		listView = (ListView)findViewById(R.id.listView);
		text_onekey_help = (TextView)findViewById(R.id.text_onekey_help);
	}


	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_right:
			getLocation();
			break;
		default:
			break;
		}
	}


	private void getLocation() {
		String items = getSelectItems();
		if(StringUtils.isNullOrEmpty(items)) {
			Utils.showToast(this, "请选择救援项");
			return;
		}
		showLoading();
		XtrdApp.getInstance().getCurrentLocation(new XtrdApp.LocationCallBack() {

			@Override
			public void callback(double longitude, double latitude, String city) {
				sendHelp(longitude, latitude);
			}
		});
	}

	private void sendHelp(double longitude,double latitude) {
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, SettingLoader.getVehicleId(this));
		params.put(ParamsKey.LONGITUDE, longitude+"");
		params.put(ParamsKey.LATITUDE, latitude+"");
		params.put(ParamsKey.IDS,getSelectItems());
		NetRequest.requestUrl(this, ApiConfig.getRequestUrl(ApiConfig.Onekey_Help_Send_Url),params, new NetRequest.NetCallBack() {

			@Override
			public void sucCallback(String str) {
				try {
					if(!StringUtils.isNullOrEmpty(str)) {
						JSONObject json = new JSONObject(str);
						if(json.has("status")) {
							int status = json.getInt("status");
							if(1==status) {
								Utils.showToast(OneKeyHelpActivity.this, getResources().getString(R.string.tips_send_suc));
								showSucDialog();
							}else {
								Utils.showToast(OneKeyHelpActivity.this, getResources().getString(R.string.tips_send_fail));
							}
						}else {
							Utils.showToast(OneKeyHelpActivity.this, getResources().getString(R.string.tips_send_fail));
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void failCallback(int errorNo, String strMsg) {
				Utils.showToast(OneKeyHelpActivity.this, getResources().getString(R.string.tips_send_fail));
			}
		});
	}

	protected void showSucDialog() {
		ObdDialog dialog = new ObdDialog(this).setTitle("发送成功")
				.setMessage("您的救援请求已发送，请耐心等待商家确认。")
				.setPositiveButton(getResources().getString(R.string.btn_cancel), new ObdDialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				})
				.setNegativeButton("查看 ", new ObdDialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(OneKeyHelpActivity.this,MyReservationActivity.class);
						startActivity(intent);
					}
				});
		if(!dialog.isShowing()) {
			dialog.show();
		}
	}

	private String getSelectItems() {
		String ids = "";
		if(list.size()==2) {
			ArrayList<HelpItem> items = list.get(1).getList();
			if(items!=null&&items.size()>0) {
				for(HelpItem item : items) {
					if(item.isChecked()) {
						ids+=item.getId()+",";
					}
				}
			}
		}
		return ids;
	}

	private void getViewData() {
		NetRequest.requestUrl(this, ApiConfig.getRequestUrl(ApiConfig.Onekey_Help_Url), new NetRequest.NetCallBack() {

			@Override
			public void sucCallback(String str) {
				try {
					if(!StringUtils.isNullOrEmpty(str)) {
						JSONObject json = new JSONObject(str);
						if(json.has("status")) {
							int status = json.getInt("status");
							if(1==status) {
								if(json.has("result")) {
									JSONArray array = json.getJSONArray("result");
									if(array!=null&&array.length()>0) {
										OnekeyHelp item = null;
										for(int i=0;i<array.length();i++) {
											item = new OnekeyHelp();
											item.parser(array.optJSONObject(i));
											list .add(item);
										}
									}
								}
							}
						}
						updateButton();
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


	private void updateButton() {
		if(list!=null&&list.size()>0) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Utils.dipToPixels(this, 300),LayoutParams.WRAP_CONTENT);
			params.weight = 1;
			Button button = null;
			for(int i=0;i<list.size();i++) {
				button = new Button(this);
				button.setText(list.get(i).getName());
				button.setTextColor(getResources().getColor(R.color.white));
				if(0==i) {
					button.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
				}else {
					button.setBackgroundColor(getResources().getColor(R.color.transparent));
				}
				button.setTextSize(16.f);
				button.setId(i);

				layout_button.addView(button,params);
				button.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						switch (v.getId()) {
						case 0:
							(layout_button.findViewById(0)).setBackgroundColor(getResources().getColor(R.color.top_bar_color));
							(layout_button.findViewById(1)).setBackgroundColor(getResources().getColor(R.color.transparent));
							btn_right.setVisibility(View.GONE);
							updateList(0);
							break;
						case 1:
							btn_right.setVisibility(View.VISIBLE);
							(layout_button.findViewById(0)).setBackgroundColor(getResources().getColor(R.color.transparent));
							(layout_button.findViewById(1)).setBackgroundColor(getResources().getColor(R.color.top_bar_color));
							updateList(1);
							break;

						default:
							break;
						}
					}
				});
			}
			updateList(0);
		}
	}

	protected void updateList(int i) {
		if(0==i) {
			text_onekey_help.setVisibility(View.VISIBLE);
		}else {
			text_onekey_help.setVisibility(View.GONE);
		}
		if(list==null||list.size()==0) {

		}else {
			HelpAdapter adapter = new HelpAdapter(this, list.get(i).getList());
			adapter.setDail(0==i?true:false);
			listView.setAdapter(adapter);
		}
	}
}
