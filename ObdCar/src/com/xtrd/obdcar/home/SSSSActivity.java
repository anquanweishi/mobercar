package com.xtrd.obdcar.home;

import java.util.ArrayList;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.http.PreferencesCookieStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.adapter.HotLineAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.entity.HotLine;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.NetUtils;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.utils.log.LogUtils;

public class SSSSActivity extends BaseActivity {

	protected static final String TAG = "InsuranceActivity";
	private ListView listView;
	private int currentPage = 0;
	private ArrayList<HotLine> list = new ArrayList<HotLine>();

	public SSSSActivity() {
		layout_id = R.layout.activity_4s;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg,R.string.title_4s, 0,0);
		
		listView = (ListView)findViewById(R.id.listView);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				HotLine hotLine = list.get(position);
				if(!StringUtils.isNullOrEmpty(hotLine.getTelephone())) {
					String[] split = hotLine.getTelephone().split(",");
					if(split.length>0) {
						Utils.showPhoneListTips(SSSSActivity.this, split);
					}else {
						Utils.showPhoneTips(SSSSActivity.this, hotLine.getTelephone());
					}
				}
			}
		});
		
		if(!NetUtils.IsNetworkAvailable()) {
			Utils.showNetTips(this);
			return;
		}
		
		get4S();
	}

	private void get4S() {
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.PageIndex, currentPage +"");
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Car_4S_URL) ,params, new AjaxCallBack<String>() {

			private String msg;

			@Override
			public void onStart() {
				showLoading();
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				LogUtils.e(TAG, "info "+t.toString());
				currentPage++;
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						if("<".equals(String.valueOf(t.charAt(0)))) {
							showLoginDialog();
						}else {
							JSONObject json = new JSONObject(t);
							if(json.has("status")) {
								int status = json.getInt("status");
								if(1==status) {
									json = json.getJSONObject("result");
									if(json.has("pageIndex")) {
										json.getInt("pageIndex");
									}
									if(json.has("data")) {
										JSONArray array = json.getJSONArray("data");
										if(array!=null&&array.length()>0) {
											HotLine hotLine = null;
											for(int i=0;i<array.length();i++) {
												hotLine = new HotLine();
												hotLine.parser((JSONObject)array.opt(i));
												list.add(hotLine);
											}
										}
									}
								}else {
									msg = json.getString("message");
									Utils.showToast(SSSSActivity.this, msg);
								}
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				updateUI();
				super.onSuccess(t);
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				updateUI();
				super.onFailure(t, errorNo, strMsg);
			}

		});
	}

	private void updateUI() {
		dismissLoading();
		if(list==null||list.size()==0) {
			Utils.showToast(this, getResources().getString(R.string.tips_no_data));
		}else {
			HotLineAdapter adapter = new HotLineAdapter(this, list);
			listView.setAdapter(adapter);
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

}
