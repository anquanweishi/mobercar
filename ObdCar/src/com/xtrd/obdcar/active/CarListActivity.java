package com.xtrd.obdcar.active;

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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.adapter.CarListAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.entity.CarInfo;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.NetUtils;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;

public class CarListActivity extends BaseActivity {

	private static final int ID_ADD = 32;
	private ListView listView;
	private Button btn_add_car;

	private ArrayList<CarInfo> list = new ArrayList<CarInfo>();
	private LinearLayout layout_active_top;
	private CarListAdapter adapter;
	private TextView tips_text;

	public CarListActivity() {
		layout_id = R.layout.activity_car_list;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0,R.drawable.btn_back_bg,R.string.title_active,R.string.btn_next,0);
		initView();
		getCarList();
	}

	private void initView() {
		layout_active_top = (LinearLayout)findViewById(R.id.layout_active_top);
		tips_text = (TextView)findViewById(R.id.tips_text);
		boolean from = getIntent().getBooleanExtra("from", false);
		if(from) {
			tips_text.setVisibility(View.VISIBLE);
			layout_active_top.setVisibility(View.VISIBLE);
		}else {
			layout_active_top.setVisibility(View.GONE);
			tips_text.setVisibility(View.GONE);
		}

		View view = new View(this);
		view.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
		listView = (ListView)findViewById(R.id.listView);
		listView.addHeaderView(view);
		btn_add_car = (Button)findViewById(R.id.btn_add_car);
		btn_add_car.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		Intent intent = null;
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_right:
			CarInfo info = getSelectId();
			if(null!=info) {
				intent = new Intent(this,BoxActiveTwoActivity.class);
				intent.putExtra("vehicleId", info.getVehicleId());
				intent.putExtra("distance", info.getDriveDistance());//TODO
				startActivity(intent);
				finish();
			}else {
				Utils.showToast(this, getResources().getString(R.string.tips_choose_add_car));
			}
			break;
		case R.id.btn_add_car:
			intent = new Intent(this,AddCarActivity.class);
			intent.putExtra("from", getIntent().getBooleanExtra("from", false));//判断是否来源与激活盒子
			startActivityForResult(intent,ID_ADD);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(RESULT_OK==resultCode) {
			if(data==null) {
				return;
			}
			switch (requestCode) {
			case ID_ADD:
				getCarList();
				break;
			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}


	/**
	 * 获取被选车辆id
	 * @return
	 */
	private CarInfo getSelectId() {
		if(list!=null||list.size()>0) {
			for(CarInfo info : list) {
				if(info.isChecked()) {
					return info;
				}
			}
		}
		return null;
	}

	private void getCarList() {
		if(!NetUtils.IsNetworkAvailable()) {
			Utils.showNetTips(this);
			return;
		}

		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		fh.get(ApiConfig.getRequestUrl(ApiConfig.NO_ACTIVE_CAR_LIST_URL), new AjaxCallBack<String>(){
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
									if(json.has("result")) {
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
				Utils.showToast(CarListActivity.this, getResources().getString(R.string.data_load_fail));
				super.onFailure(t, errorNo, strMsg);
			}

		});

	}

	protected void updateUI() {
		if(listView.getAdapter()==null) {
			adapter = new CarListAdapter(this,list);
			listView.setAdapter(adapter);
		}else {
			adapter.notifyDataSetChanged();
		}
	}

}
