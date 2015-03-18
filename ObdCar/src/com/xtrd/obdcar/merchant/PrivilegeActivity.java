package com.xtrd.obdcar.merchant;


import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.entity.Privilege;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.TimeUtils;

public class PrivilegeActivity extends BaseActivity {
	
	private View img_icon;
	private TextView text_shop_name;
	private TextView text_time;
	private TextView text_people;
	private TextView text_desc;
	protected Privilege item;


	public PrivilegeActivity() {
		layout_id = R.layout.activity_privilege;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg,R.string.text_vip_privilege,0,0);
		
		initView();
		int id = getIntent().getIntExtra("id", 0);
		getPrivilege(id+"");
	}
	
	

	private void initView() {
		img_icon = (View)findViewById(R.id.img_icon);
		text_shop_name = (TextView)findViewById(R.id.text_shop_name);
		text_time = (TextView)findViewById(R.id.text_time);
		text_people = (TextView)findViewById(R.id.text_people);
		text_desc = (TextView)findViewById(R.id.text_desc);
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
	
	private void getPrivilege(String id) {
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.ID, id);
		NetRequest.requestUrl(this, ApiConfig.getRequestUrl(ApiConfig.Discount_Url), params, new NetRequest.NetCallBack() {

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
									item = new Privilege();
									item.parser(json);
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
			}
		}); 	
	}

	protected void updateUI() {
		if(item!=null) {
			if(!StringUtils.isNullOrEmpty(item.getImgUrl())) {
				((LinearLayout)img_icon.getParent()).setVisibility(View.VISIBLE);
				FinalBitmap fb = FinalBitmap.create(this);
				fb.display(img_icon, item.getImgUrl());
			}else {
				((LinearLayout)img_icon.getParent()).setVisibility(View.GONE);
			}
			text_shop_name.setText(item.getMerchantName());
			text_desc.setText(item.getDescription());
			text_time.setText("促销时段："+TimeUtils.getDateByTime(item.getStartTime())+"至"+TimeUtils.getDateByTime(item.getEndTime()));
			text_people.setText("活动对象："+item.getOwner());
		}
	}
}
