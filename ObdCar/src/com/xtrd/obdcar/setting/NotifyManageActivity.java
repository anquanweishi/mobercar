package com.xtrd.obdcar.setting;

import java.util.ArrayList;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.adapter.NotifyTypeAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.entity.NotifyType;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;

public class NotifyManageActivity extends BaseActivity {
	
	private ListView listView;
	private ArrayList<NotifyType> types = new ArrayList<NotifyType>();
	private NotifyTypeAdapter adapter;

	public NotifyManageActivity() {
		layout_id = R.layout.activity_notify_manage;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg, R.string.set_item_notify_manage, 0, 0);
		initView();
		getNotifys();
	}

	

	private void initView() {
		listView = (ListView)findViewById(R.id.listView);
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

	private void getNotifys() {
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.ID, "");
		params.put(ParamsKey.Notify_Set, "1");
		NetRequest.requestUrl(this, ApiConfig.getRequestUrl(ApiConfig.Notify_Type_Url) ,params,new NetRequest.NetCallBack() {

			@Override
			public void sucCallback(String str) {
				try {
					if(!StringUtils.isNullOrEmpty(str)) {
						JSONObject json = new JSONObject(str);
						if(json!=null) {
							if(json.has("status")) {
								int status = json.getInt("status");
								if (1 == status) {
									if(json.has("result")) {
										types.clear();
										JSONArray array = json.getJSONArray("result");
										if(array!=null&&array.length()>0) {
											NotifyType type = null;
											for(int i=0;i<array.length();i++) {
												type = new NotifyType();
												type.parser(array.optJSONObject(i));
												types.add(type);
											}
										}
									}
								}
							}
							updateTypes();
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

	protected void updateTypes() {
		if(types!=null&&types.size()>0) {
			adapter = new NotifyTypeAdapter(this, types);
			listView.setAdapter(adapter);
		}
		
	}
}
