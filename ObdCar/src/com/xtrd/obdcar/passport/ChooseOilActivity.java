package com.xtrd.obdcar.passport;

import java.util.ArrayList;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.adapter.OilAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.entity.OBDOil;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.NetUtils;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.utils.log.LogUtils;
import com.xtrd.obdcar.view.ObdDialog;

public class ChooseOilActivity extends BaseActivity {

	protected static final String TAG = "ChooseOilActivity";
	private ListView listView;
	protected ArrayList<OBDOil> list;

	private OBDOil currentOil;
	private TextView choose_text;


	public ChooseOilActivity() {
		layout_id = R.layout.activity_oil;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg,R.string.title_oil_add, 0,0);
		initView();
		if(!NetUtils.IsNetworkAvailable()) {
			Utils.showNetTips(this);
			return;
		}
		getOil();
	}

	private void getOil() {
		FinalHttp fh = new FinalHttp();
		fh.post(ApiConfig.getRequestUrl(ApiConfig.OIL_Url),new AjaxCallBack<String>() {

			@Override
			public void onStart() {
				showLoading();
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				dismissLoading();
				LogUtils.e(TAG, t.toString());
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						JSONObject json = new JSONObject(t);
						if(json.has("status")) {
							int status = json.getInt("status");
							if(1==status) {
								if(json.has("result")) {
									JSONArray array = json.getJSONArray("result");
									list = new ArrayList<OBDOil>();
									OBDOil oil = null;
									if(array!=null&&array.length()>0) {
										for(int i=0;i<array.length();i++) {
											oil = new OBDOil();
											oil.parser((JSONObject)array.opt(i));
											list.add(oil);
										}
									}
								}
							}else {
								String message = json.getString("message");
								Utils.showToast(ChooseOilActivity.this, message);
							}
							updateUI();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				super.onSuccess(t);
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				dismissLoading();
				updateUI();
				super.onFailure(t, errorNo, strMsg);
			}

		});
	}

	protected void updateUI() {
		if(list==null||list.size()==0) {
			Utils.showToast(this, getResources().getString(R.string.tips_no_data));
		}else {
			OilAdapter adapter = new OilAdapter(this, list);
			listView.setAdapter(adapter);
		}
	}

	private void initView() {
		choose_text = (TextView)findViewById(R.id.choose_text);
		listView = (ListView)findViewById(R.id.listView);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				currentOil = list.get(arg2);
				choose_text.setText(currentOil.getName());
				showChooseDialog(currentOil);
			}
		});
	}

	protected void showChooseDialog(final OBDOil oil) {
		ObdDialog obdDialog = new ObdDialog(this)
		.setTitle("提示信息")
		.setMessage("确定选择   " + oil.getName())
		.setPositiveButton("重选", new ObdDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				choose_text.setText("");
				((ObdDialog) dialog).updateMessage(oil.getName());
			}
		})
		.setNegativeButton("确定", new ObdDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				choose_text.setText(oil.getName());
				Intent intent = new Intent();
				intent.putExtra("oilId", oil.getId());
				intent.putExtra("value", choose_text.getText().toString());
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
		obdDialog.show();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_right:
			Intent intent = new Intent();
			intent.putExtra("oilId", currentOil.getId());
			intent.putExtra("value", currentOil.getName());
			setResult(RESULT_OK, intent);
			finish();
			break;

		default:
			break;
		}
		super.onClick(v);
	}
}
