package com.xtrd.obdcar.setting;

import java.util.ArrayList;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.PreferencesCookieStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.adapter.DefaultCarAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.entity.CarInfo;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.NetUtils;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;

public class CarDetailActivity extends BaseActivity {
	
	private ListView listView;
	private ArrayList<CarInfo> list = new ArrayList<CarInfo>();

	public CarDetailActivity() {
		layout_id = R.layout.activity_car_detail;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg, R.string.set_item_car_detail, 0, 0);
		initView();
		getDefaultCar();
	}

	private void initView() {
		listView = (ListView)findViewById(R.id.listView);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				CarInfo info = list.get(position);
				Intent intent = new Intent(CarDetailActivity.this,DetailActivity.class);
				intent.putExtra("vehicleId", info.getVehicleId());
				intent.putExtra("plateNumber", info.getPlateNumber());
				intent.putExtra("branch", info.getBranch());
				intent.putExtra("series", info.getSeries());
				startActivityForResult(intent,1);
			}
		});
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK) {
			if(data!=null) {
				//判断是否刷新列表
				boolean refresh = data.getBooleanExtra("infoupdate", false);
				if(refresh) {
					list.clear();
					getDefaultCar();
				}
			}
		}
	}
	
	
	private void getDefaultCar() {
		if(!NetUtils.IsNetworkAvailable()) {
			Utils.showNetTips(this);
			return;
		}
		
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		fh.get(ApiConfig.getRequestUrl(ApiConfig.Car_List_Url), new AjaxCallBack<String>(){
			private int status = 0;

			@Override
			public void onStart() {
				showLoading();
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				dismissLoading();
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						if("<".equals(String.valueOf(t.charAt(0)))) {
							showLoginDialog();
						}else {
							JSONObject json = new JSONObject(t);
							if(json.has("status")) {
								status  = json.getInt("status");
								if(1==status) {
									JSONArray jsonArray = json.getJSONArray("result");
									if(jsonArray!=null&&jsonArray.length()>0) {
										CarInfo car = null;
										for(int i=0;i<jsonArray.length();i++) {
											car = new CarInfo();
											car.parser(jsonArray.optJSONObject(i));
											list.add(car);
										}
									}
								}
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
				Utils.showToast(CarDetailActivity.this, getResources().getString(R.string.data_load_fail));
				super.onFailure(t, errorNo, strMsg);
			}

		});

	}

	protected void updateUI() {
		if(listView.getAdapter()==null) {
			DefaultCarAdapter adapter = new DefaultCarAdapter(this,list);
			adapter.setFrom(true);
			listView.setAdapter(adapter);
		}else {
			((DefaultCarAdapter)listView.getAdapter()).setFrom(true);
			((DefaultCarAdapter)listView.getAdapter()).notifyDataSetChanged();
		}
	}

}
