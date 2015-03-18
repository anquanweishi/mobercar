package com.xtrd.obdcar.nearby;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.LocationDecoder;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;

/**
 * @author Administrator
 * 
 */
public class NearShopDetailActivity extends BaseActivity {

	private TextView text_name;
	private TextView text_location;
	private TextView text_distance;
	private LinearLayout layout_phone;
	private TextView text_phone;
	private LinearLayout layout_nearby;
	private ImageButton img_nav;

	private String name,address,phone;
	private double latitude,longitude;

	public NearShopDetailActivity() {
		layout_id = R.layout.activity_near_shop_detail;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		name = intent.getStringExtra("title");
		initTitle(0, R.drawable.btn_back_bg, name, R.string.btn_favorite,0);
		if(!SettingLoader.hasLogin(this)) {
			btn_right.setVisibility(View.GONE);
		}else {
			if(getIntent().getBooleanExtra("needHideFav", false)) {
				btn_right.setVisibility(View.GONE);
			}else {
				btn_right.setVisibility(View.VISIBLE);
			}
		}

		address = intent.getStringExtra("address");
		phone = intent.getStringExtra("phone");
		latitude = intent.getDoubleExtra("latitude", 0);
		longitude = intent.getDoubleExtra("longitude", 0);

		initView();
		regClick();
		updateUI();
		checkFav();
	}


	private void checkFav() {
		AjaxParams params = new AjaxParams();
		params.put("uids", getIntent().getStringExtra(ParamsKey.PoiId));
		NetRequest.requestUrl(this,
				ApiConfig.getRequestUrl(ApiConfig.POI_Exist_Url), params,
				new NetRequest.NetCallBack(){

			@Override
			public void sucCallback(String str) {
				try {
					if(!StringUtils.isNullOrEmpty(str)) {
						JSONObject json = new JSONObject(str);
						if(json!=null) {
							if(json.has("status")) {
								int status = json.getInt("status");
								if(1==status) {
									if(json.has("result")) {
										if(1==json.getInt("result")) {
											btn_right.setVisibility(View.GONE);
										}else {
											btn_right.setVisibility(View.VISIBLE);
										}
									}
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

			}});
	}

	private void initView() {
		text_name = (TextView) findViewById(R.id.text_name);
		text_location = (TextView) findViewById(R.id.text_location);
		img_nav = (ImageButton)findViewById(R.id.img_nav);
		text_distance = (TextView) findViewById(R.id.text_distance);
		layout_phone = (LinearLayout) findViewById(R.id.layout_phone);
		text_phone = (TextView) findViewById(R.id.text_phone);
		layout_nearby = (LinearLayout) findViewById(R.id.layout_nearby);
	}

	private void regClick() {
		layout_phone.setOnClickListener(this);
		layout_nearby.setOnClickListener(this);
		img_nav.setOnClickListener(this);
	}

	private void updateUI() {
		text_name.setText(name);
		text_location.setText(address);
		text_distance.setText(LocationDecoder.getDistance(SettingLoader.getCarLatLng(this), new LatLng(latitude,longitude)));
		if(StringUtils.isNullOrEmpty(phone)) {
			layout_phone.setVisibility(View.GONE);
		}else {
			layout_phone.setVisibility(View.VISIBLE);
			text_phone.setText(phone);
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
			favShop();
			break;
		case R.id.img_nav:
			Intent map = new Intent(this,PoiLineActivity.class);
			map.putExtra("endLat",latitude);
			map.putExtra("endLng", longitude);
			map.putExtra("endTitle", name);
			map.putExtra("convert", false);
			startActivity(map);
			break;
		case R.id.layout_phone:
			String phones = getIntent().getStringExtra("phone");
			if(!StringUtils.isNullOrEmpty(phones)) {
				String[] split = phones.split(",");
				if(split.length>0) {
					Utils.showPhoneListTips(this, split);
				}else {
					Utils.showPhoneTips(this, phones);
				}
			}
			break;
		case R.id.layout_nearby:
			if(getIntent().getBooleanExtra("needHideFav", false)) {
				Intent intent = new Intent(this,NearCategoryActivity.class);
				startActivity(intent);
			}else {
				Intent intent = new Intent();
				intent.putExtra("finish", true);
				intent.putExtra("latitude",latitude);
				intent.putExtra("longitude",longitude);
				setResult(RESULT_OK, intent);
				finish();
			}
			break;

		default:
			break;
		}
	}

	private void favShop() {
		Intent intent = getIntent();
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.LATITUDE, intent.getDoubleExtra("latitude", 0)+"");
		params.put(ParamsKey.LONGITUDE, intent.getDoubleExtra("longitude", 0)+"");
		params.put(ParamsKey.Reg_Name, intent.getStringExtra("title"));
		params.put(ParamsKey.Owner_Address, intent.getStringExtra("address"));
		params.put(ParamsKey.Phone, intent.getStringExtra("phone"));
		params.put(ParamsKey.ID_KEY, intent.getIntExtra("id",0)+"");
		params.put(ParamsKey.PoiId, getIntent().getStringExtra(ParamsKey.PoiId));
		NetRequest.requestUrl(this, ApiConfig.getRequestUrl(ApiConfig.Fav_Shop_Url),params, new NetRequest.NetCallBack() {

			@Override
			public void sucCallback(String str) {
				try {
					if(!StringUtils.isNullOrEmpty(str)) {
						JSONObject json = new JSONObject(str);
						if(json.has("status")) {
							int status = json.getInt("status");
							if(1==status) {
								Utils.showToast(NearShopDetailActivity.this, "收藏成功");
								btn_right.setVisibility(View.GONE);
							}else {
								Utils.showToast(NearShopDetailActivity.this, "收藏失败");
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void failCallback(int errorNo, String strMsg) {
				Utils.showToast(NearShopDetailActivity.this, "收藏失败");
			}
		});

	}
}
