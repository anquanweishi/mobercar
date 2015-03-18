package com.xtrd.obdcar.illegal;


import java.util.ArrayList;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.PreferencesCookieStore;

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
import com.xtrd.obdcar.adapter.ViolationPlateAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.entity.PlateEntity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.NetUtils;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.utils.log.LogUtils;
import com.xtrd.obdcar.view.ObdDialog;

public class IllegalPlateActivity extends BaseActivity {
	protected static final String TAG = "IllegalPlateActivity";
	private ListView listView;
	private TextView choose_text;
	private ArrayList<PlateEntity> list = new ArrayList<PlateEntity>();

	public IllegalPlateActivity() {
		layout_id = R.layout.activity_illegal_plate;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg, R.string.title_violation_plate, 0, 0);
		initView();
		
		if(!NetUtils.IsNetworkAvailable()) {
			Utils.showNetTips(this);
			return;
		}
		getViolationPlate();
	}

	private void initView() {
		choose_text = (TextView)findViewById(R.id.choose_text);
		listView = (ListView)findViewById(R.id.listView);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				choose_text.setText(list.get(position).getCar());
				showChooseDialog(list.get(position));
			}
		});
	}
	
	protected void showChooseDialog(final PlateEntity plateEntity) {
		ObdDialog obdDialog = new ObdDialog(this)
		.setTitle("提示信息")
		.setMessage("确定选择   " + plateEntity.getCar())
		.setPositiveButton("重选", new ObdDialog.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				choose_text.setText("");
				((ObdDialog) dialog).updateMessage("");
			}
		})
		.setNegativeButton("确定", new ObdDialog.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent();
				intent.putExtra("plateType", plateEntity.getId());
				intent.putExtra("value", choose_text.getText().toString());
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
		obdDialog.show();
		
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;

		default:
			break;
		}
	}

	private void getViolationPlate() {
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		//		params.put(ParamsKey.PageSize, "30");
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Violation_Plate_Url),new AjaxCallBack<String>() {

			private String msg;

			@Override
			public void onStart() {
				showLoading();
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				LogUtils.e(TAG, t.toString());
				dismissLoading();
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						if("<".equals(String.valueOf(t.charAt(0)))) {
							showLoginDialog();
						}else {
							JSONObject json = new JSONObject(t);
							if(json.has("status")) {
								int status = json.getInt("status");
								if(1==status) {
									JSONArray array = json.getJSONArray("result");
									if(array!=null&&array.length()>0) {
										PlateEntity plate = null;
										for(int i=0;i<array.length();i++) {
											plate = new PlateEntity();
											plate.parser((JSONObject)array.opt(i));
											list.add(plate);
										}
									}
								}else {
									msg = json.getString("message");
									Utils.showToast(IllegalPlateActivity.this, msg);
								}
								updateUI();
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
				updateUI();
				dismissLoading();
			}

		});
	}

	protected void updateUI() {
		if(list==null||list.size()==0) {
			Utils.showToast(this, getResources().getString(R.string.tips_no_data));
		}else {
			if(listView.getAdapter()==null) {
				ViolationPlateAdapter adapter = new ViolationPlateAdapter(this, list);
				listView.setAdapter(adapter);
			}else {
				((ViolationPlateAdapter)listView.getAdapter()).notifyDataSetChanged();
			}
		}
	}

}
