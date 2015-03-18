package com.xtrd.obdcar.vc;


import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.DashBoard;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.view.DashBoardView;

public class DashBoardActivity extends BaseActivity {

	private DashBoard dashBoard;
	private TextView text_time_bg;
	private TextView text_time;
	private TextView text_distance_bg;
	private TextView text_distance;
	private TextView text_fast_turbo;
	private TextView text_brake_count;
	private TextView text_race_count;
	private RelativeLayout layout_top;
	private RelativeLayout layout_bottom;
	private TextView text_avg_fule;
	private TextView text_fule;
	private TextView text_cost;
	
	public DashBoardActivity() {
		layout_id = R.layout.activity_dash_board;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg,R.string.btn_dash_board, 0,0);
		initView();
		//设置字体

		getDashBoardData();
	}


	private void initView() {
		text_time_bg = (TextView)findViewById(R.id.text_time_bg);
		text_time = (TextView)findViewById(R.id.text_time);
		text_distance_bg = (TextView)findViewById(R.id.text_distance_bg);
		text_distance = (TextView)findViewById(R.id.text_distance);
		Typeface typeface = Typeface.createFromAsset(getAssets(),"DS-DIGIB.TTF");
		title_text.setTypeface(typeface);
		text_time_bg.setTypeface(typeface);
		text_time.setTypeface(typeface);
		text_distance_bg.setTypeface(typeface);
		text_distance.setTypeface(typeface);
		
		text_avg_fule = (TextView)findViewById(R.id.text_avg_fule);
		text_fule = (TextView)findViewById(R.id.text_fule);
		text_cost = (TextView)findViewById(R.id.text_cost);
		

		layout_top = (RelativeLayout)findViewById(R.id.layout_top);
		layout_bottom = (RelativeLayout)findViewById(R.id.layout_bottom);

		//底部文本
		text_fast_turbo = (TextView)findViewById(R.id.text_fast_turbo);
		text_brake_count = (TextView)findViewById(R.id.text_brake_count);
		text_race_count = (TextView)findViewById(R.id.text_race_count);
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


	private void getDashBoardData() {
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, SettingLoader.getVehicleId(this));
		NetRequest.requestUrl(this,
				ApiConfig.getRequestUrl(ApiConfig.Dash_Board_Show_Url), params,
				new NetRequest.NetCallBack() {



			@Override
			public void sucCallback(String str) {
				try {
					if (!StringUtils.isNullOrEmpty(str)) {
						JSONObject json = new JSONObject(str);
						if (json.has("status")) {
							int status = json.getInt("status");
							if (1 == status) {
								if(json.has("result")) {
									json = json.getJSONObject("result");
									dashBoard = new DashBoard();
									dashBoard.parser(json);
								}
							} else {
								if(json.has("message")) {
									String msg = json.getString("message");
									Utils.showToast(DashBoardActivity.this, msg);
								}
							}
						}
						updateUI();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void failCallback(int errorNo, String strMsg) {
				Utils.showToast(DashBoardActivity.this, R.string.data_load_fail);
			}
		});
	}

	protected void updateUI() {

		if(dashBoard!=null) {
			text_time.setText(formatMinuts(dashBoard.getTime()));
			text_distance.setText(dashBoard.getDistance()+"");
			text_fast_turbo.setText(dashBoard.getAcc()+"");
			text_race_count.setText(dashBoard.getWhe()+"");
			text_brake_count.setText(dashBoard.getDec()+"");
			
			text_avg_fule.setText(dashBoard.getFuelC()+"");
			text_fule.setText(dashBoard.getFuelAmount()+"");
			text_cost.setText(dashBoard.getPrice()+"");
			
			RelativeLayout.LayoutParams oneLP = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			DashBoardView dashboad = new DashBoardView(this,R.drawable.ic_dash_one,getTempDegree(dashBoard.getTem()),dashBoard.getTem()+"","冷却液温度(°C)");
			oneLP.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			layout_top.addView(dashboad,oneLP);
			
			RelativeLayout.LayoutParams twoLP = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			twoLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			dashboad = new DashBoardView(this,R.drawable.ic_dash_two,getSpeedDegree(dashBoard.getMaxSpeed()),dashBoard.getMaxSpeed()+"","最高时速(km/h)");
			layout_top.addView(dashboad,twoLP);
			
			RelativeLayout.LayoutParams threeLP = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			threeLP.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			dashboad = new DashBoardView(this,R.drawable.ic_dash_three,getElecDegree(dashBoard.getElec()),dashBoard.getElec()+"","电瓶电压(V)");
			layout_bottom.addView(dashboad,threeLP);
			
			RelativeLayout.LayoutParams fourLP = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			fourLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			dashboad = new DashBoardView(this,R.drawable.ic_dash_four,getLocDegree(dashBoard.getLoc()),dashBoard.getLoc()+"","节气门位置(%)");
			layout_bottom.addView(dashboad,fourLP);
		}
	}

	private String formatMinuts(int time) {
		return time/60+":"+time%60;
	}

	private int getLocDegree(double loc) {
		try {
			return (int) (180*loc/100);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 15;
	}

	private int getElecDegree(double elec) {
		try {
			return (int) (180*elec/36);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 15;
	}

	private int getSpeedDegree(int maxSpeed) {
		try {
			return 180*maxSpeed/240;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 15;
	}

	private int getTempDegree(int tem) {
		try {
			return 180*tem/120;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 15;
	}

}
