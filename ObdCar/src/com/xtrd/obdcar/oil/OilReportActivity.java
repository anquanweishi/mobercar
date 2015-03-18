package com.xtrd.obdcar.oil;

import java.util.ArrayList;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.XtrdApp;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.GasStation;
import com.xtrd.obdcar.entity.OilPrice;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.passport.LoginActivity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.view.GasStationSpinner;

public class OilReportActivity extends BaseActivity {
	private ImageView img_icon;
	private TextView text_plate;
	private TextView text_branch;
	private TextView text_time;
	private TextView text_name;
	private TextView text_address;
	private LinearLayout layout_price;
	private ImageButton btn_arrow;
	private EditText edit_input;

	private ArrayList<GasStation> list = new ArrayList<GasStation>();
	private GasStationSpinner popup;
	private String currentStationId;

	private GasStation currentStation;

	public OilReportActivity() {
		layout_id = R.layout.activity_oil_price_report;
	}
	public Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case XtrdApp.ID_CARINFO_GET:
				updateCarInfo();
				break;
			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg, R.string.title_oil_price_report, R.string.btn_commit,0);
		XtrdApp.addHandler(handler);
		initView();
		updateCarInfo();
		if(!SettingLoader.hasLogin(this)) {
			Intent intent = new Intent(this,LoginActivity.class);
			startActivityForResult(intent, 1);
			return;
		}
		if(getIntent().getBooleanExtra("displayOne", false)) {
			currentStation = getIntent().getParcelableExtra("gasStation");
			btn_arrow.setVisibility(View.GONE);
			updateUI();
			updatePrices();
		}else {
			btn_arrow.setVisibility(View.VISIBLE);
			getDisplayData();
		}
	}

	private void initView() {
		img_icon = (ImageView)findViewById(R.id.img_icon);
		text_plate = (TextView)findViewById(R.id.text_plate);
		text_branch = (TextView)findViewById(R.id.text_branch);
		text_time = (TextView)findViewById(R.id.text_time);
		text_name = (TextView)findViewById(R.id.text_name);
		text_address = (TextView)findViewById(R.id.text_address);
		layout_price = (LinearLayout)findViewById(R.id.layout_price);
		btn_arrow = (ImageButton)findViewById(R.id.btn_arrow);
		btn_arrow.setOnClickListener(this);
		edit_input = (EditText)findViewById(R.id.edit_input);
	}

	private void updateCarInfo() {
		FinalBitmap fb = FinalBitmap.create(this);
		fb.display(img_icon, ApiConfig.getRequestUrl(ApiConfig.Car_Logo_Url)+"?"+ParamsKey.Car_Branch+"="+SettingLoader.getBranchId(this));
		text_plate.setText(SettingLoader.getCarPlate(this));
		text_branch.setText(SettingLoader.getBranchName(this));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1:
			if(SettingLoader.hasLogin(this)) {
				getDisplayData();
			}else {
				finish();
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_right:
			commitPrice();
			break;
		case R.id.btn_arrow:
			if(popup==null) {
				popup = new GasStationSpinner(this,list,new GasStationSpinner.CallBack() {
					@Override
					public void callback(GasStation gasStation) {
						for(GasStation item : list) {
							if(item.getStationId()==gasStation.getStationId()) {
								item.setChecked(true);
							}else {
								item.setChecked(false);
							}
						}
						currentStation = gasStation;
						updateUI();
						updatePrices();
					}
				});
			}
			if(!popup.isShowing()) {
				popup.show();
			}
			break;

		default:
			break;
		}
	}


	private void getDisplayData() {
		AjaxParams params = new AjaxParams();
		if(SettingLoader.getCarLatLng(this)!=null) {
			params.put(ParamsKey.LATITUDE,SettingLoader.getCarLatLng(this).latitude+"");
			params.put(ParamsKey.LONGITUDE,SettingLoader.getCarLatLng(this).longitude+"");
		}
		NetRequest.requestUrl(this, ApiConfig.getRequestUrl(ApiConfig.Oil_Price_Report_Url), params, new NetRequest.NetCallBack() {

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
										GasStation item = null;
										for(int i=0;i<array.length();i++) {
											item = new GasStation();
											item.parser(array.optJSONObject(i));
											list.add(item);
										}
									}
								}
							}
							if(list.size()>0) {
								currentStation = getCurrentStation();
							}
							updateUI();
							updatePrices();
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


	protected GasStation getCurrentStation() {
		for(GasStation item : list) {
			if(getIntent().getIntExtra(ParamsKey.ID, 0)==item.getStationId()) {
				return item;
			}
		}
		return list.get(0);
	}

	protected void updateUI() {
		if(currentStation!=null) {
			text_time.setText(currentStation.getTime());
			text_name.setText(currentStation.getName());
			text_address.setText(currentStation.getAddress());
			currentStationId = currentStation.getStationId()+"";
		}
	}

	private void updatePrices() {
		if(currentStation!=null&&currentStation.getPrices()!=null&&currentStation.getPrices().size()>0) {
			View view = null;
			LinearLayout.LayoutParams params =  new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
			params.setMargins(0, 0, 0, Utils.dipToPixels(this, 10));
			OilPrice price = null;
			layout_price.removeAllViews();
			for(int i=0;i<currentStation.getPrices().size();i++) {
				price = currentStation.getPrices().get(i);
				view = LayoutInflater.from(this).inflate(R.layout.layout_oil_price_for_report, null);
				((TextView)view.findViewById(R.id.text_oil_type)).setText(price.getName()+"#");
				((EditText)view.findViewById(R.id.edit_price_input)).setSelection(0);
				layout_price.addView(view,params);
			}
		}
	}

	/**
	 * 获取名称与价格
	 * @return
	 */
	private String[] getNamesAndPrices() {
		String names = "",prices="";
		if(layout_price.getChildCount()>0) {
			View child = null;
			String price = "";
			for(int i=0;i<layout_price.getChildCount();i++) {
				child= layout_price.getChildAt(i);
				price = ((EditText)child.findViewById(R.id.edit_price_input)).getText().toString();
				if(!StringUtils.isNullOrEmpty(price)&&Double.parseDouble(price)>0) {
					names += currentStation.getPrices().get(i).getName()+",";
					prices+= String.format("%.2f", Double.parseDouble(price))+",";
				}
			}
		}
		if(StringUtils.isNullOrEmpty(prices)||StringUtils.isNullOrEmpty(names)) {
			return null;
		}
		if(!StringUtils.isNullOrEmpty(names)&&!StringUtils.isNullOrEmpty(prices)) {
			return new String[]{names.substring(0, names.lastIndexOf(",")),prices.substring(0, prices.lastIndexOf(","))};
		}else {
			return new String[]{names,prices};
		}
	}


	/**
	 * 上报油价
	 */
	private void commitPrice() {
		String input = edit_input.getText().toString();
		if(StringUtils.isNullOrEmpty(input)) {
			Utils.showToast(this, "请发表点评");
			return;
		}

		String[] namesAndPrices = getNamesAndPrices();
		if(namesAndPrices==null) {
			Utils.showToast(this, "至少上报一种油品价格！");
			return;
		}
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.ID,currentStationId);
		params.put(ParamsKey.EVALUATE_CONTENT,input);
		params.put(ParamsKey.Names,namesAndPrices[0]);
		params.put(ParamsKey.Prices,namesAndPrices[1]);
		NetRequest.requestUrl(this, ApiConfig.getRequestUrl(ApiConfig.Oil_Price_Report_Add_Url), params, new NetRequest.NetCallBack() {

			@Override
			public void sucCallback(String str) {
				try {
					if(!StringUtils.isNullOrEmpty(str)) {
						JSONObject json = new JSONObject(str);
						if(json.has("status")) {
							int status = json.getInt("status");
							if(1==status) {
								Utils.showToast(OilReportActivity.this, "上报成功");
								setResult(RESULT_OK);
								finish();
							}else {
								if(json.has("message")) {
									String msg = json.getString("message");
									Utils.showToast(OilReportActivity.this, msg);
								}
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void failCallback(int errorNo, String strMsg) {
				Utils.showToast(OilReportActivity.this, "上报失败");
			}
		});
	}

}
