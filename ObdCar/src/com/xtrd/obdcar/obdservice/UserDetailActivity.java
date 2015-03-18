package com.xtrd.obdcar.obdservice;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.CarOpenHelper;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.CarUser;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.ImageUtils;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.log.LogUtils;

public class UserDetailActivity extends BaseActivity {
	
	protected static final String TAG = "UserDetailActivity";
	private TextView text_range_display;
	
	private ImageView img_icon;
	private TextView text_phone;
	private TextView text_location;
	private TextView text_plate;
	private TextView text_num;
	private TextView text_range;
	private TextView text_box_range;
	private TextView text_history_week;
	private TextView text_avg_oil;
	private TextView text_amount_range;
	private TextView text_sort_week;
	private TextView text_avg_oil_sort;
	private TextView text_skill_sort;
	
	private CarUser user = new CarUser();
	private int type;

	public UserDetailActivity() {
		layout_id = R.layout.activity_user_detail;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg, R.string.title_user, 0, 0);
		type = getIntent().getIntExtra("type", 0);
		initView();
		if(getIntent().getBooleanExtra("fromBill", false)) {
			getUserDetail();
		}else {
			if(SettingLoader.hasLogin(this)) {
				getUserDetail();
			}else {
				user.setBranchId(SettingLoader.getBranchId(this));
				user.setNick(SettingLoader.getCarPlate(this));
				user.setCity(CarOpenHelper.getInstance(this).getCityByPlate(SettingLoader.getCarPlate(this)));
				updateUI();
			}
		}
	}

	private void initView() {
		img_icon = (ImageView)findViewById(R.id.img_icon);
		text_phone = (TextView)findViewById(R.id.text_phone);
		text_location = (TextView)findViewById(R.id.text_location);
		text_plate = (TextView)findViewById(R.id.text_plate);
		text_num = (TextView)findViewById(R.id.text_num);
		text_range = (TextView)findViewById(R.id.text_range);
		text_box_range = (TextView)findViewById(R.id.text_box_range);
		text_history_week = (TextView)findViewById(R.id.text_history_week);
		text_avg_oil = (TextView)findViewById(R.id.text_avg_oil);
		text_range_display = (TextView)findViewById(R.id.text_range_display);
		text_amount_range = (TextView)findViewById(R.id.text_amount_range);
		text_sort_week = (TextView)findViewById(R.id.text_sort_week);
		text_avg_oil_sort = (TextView)findViewById(R.id.text_avg_oil_sort);
		text_skill_sort = (TextView)findViewById(R.id.text_skill_sort);
		
		if(0==type) {
			text_range_display.setText(getResources().getString(R.string.text_amount_range));
			text_history_week.setText(getResources().getString(R.string.text_week_history));
			text_sort_week.setText(getResources().getString(R.string.text_week_sort));
		}else {
			text_range_display.setText(getResources().getString(R.string.text_month_amount_range));
			text_history_week.setText(getResources().getString(R.string.text_month_history));
			text_sort_week.setText(getResources().getString(R.string.text_month_sort));
		}
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

	private void getUserDetail() {
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, getIntent().getStringExtra(ParamsKey.VEHICLEID));
		params.put(ParamsKey.TYPE, type+"");
		NetRequest.requestUrl(this,  ApiConfig.getRequestUrl(ApiConfig.User_Detail_Url),params, new NetRequest.NetCallBack() {
			
			@Override
			public void sucCallback(String str) {
				try {
					if(!StringUtils.isNullOrEmpty(str)) {
						JSONObject json = new JSONObject(str);
						if(json.has("status")) {
							int status = json.getInt("status");
							if(1==status) {
								if(json.has("result")) {
									json = json.getJSONObject("result");
									user.parser(json);
								}
							}
						}
					}

					updateUI();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void failCallback(int errorNo, String strMsg) {
				LogUtils.e(TAG, "str " + strMsg);
			}
		});
		
	}

	protected void updateUI() {
		ImageUtils.displayBranchImg(this, img_icon, user.getBranchId());
		text_phone.setText(user.getNick());
		text_location.setText(user.getCity());
		text_plate.setText(user.getBra()+"  "+user.getSer());
		text_num.setText("");
		text_range.setText(user.getDistance()+"公里");
		text_box_range.setText(user.getMy()+"公里");
		text_avg_oil.setText(user.getFuelAvg()+"升/百公里");
		text_amount_range.setText(user.getSumDis()+"公里");
		text_avg_oil_sort.setText("第"+user.getFsort()+"位");
		text_skill_sort.setText("第"+user.getDsort()+"位");
	}
}
