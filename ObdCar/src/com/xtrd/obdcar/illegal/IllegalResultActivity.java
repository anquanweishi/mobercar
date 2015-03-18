package com.xtrd.obdcar.illegal;


import java.util.ArrayList;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.http.PreferencesCookieStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.XtrdApp;
import com.xtrd.obdcar.adapter.ViolationResultAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.RuleBreakOpenHelper;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.ViolationResult;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.TimeUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.utils.log.LogUtils;

public class IllegalResultActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{
	protected static final String TAG = "IllegalResultActivity";
	private SwipeRefreshLayout swipeLayout;
	private ListView listView;
	private ArrayList<ViolationResult> list = new ArrayList<ViolationResult>();
	private TextView text_info;
	private String area;
	protected boolean refresh;
	protected boolean hasMore = true;
	private ViolationResultAdapter adapter;


	public IllegalResultActivity() {
		layout_id = R.layout.activity_vio_result;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg,R.string.title_illegal_result, 0,0);
		ArrayList<ViolationResult> data = getIntent().getParcelableArrayListExtra("list");
		list.addAll(data);
		area = getIntent().getStringExtra("area");
		initView();
		regListener();

	}

	private void initView() {
		text_info = (TextView)findViewById(R.id.text_info);
		processInfo();
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorScheme(R.color.holo_blue_bright, R.color.holo_green_light,
				R.color.holo_orange_light, R.color.holo_red_light);
		listView = (ListView)findViewById(R.id.listView);
		adapter = new ViolationResultAdapter(this, list);
		listView.setAdapter(adapter);
	}


	@Override
	public void onRefresh() {
		refresh = true;
		getViolation();
	}
	
	private void processInfo() {
		int money=0,fen=0;
		for(ViolationResult result : list) {
			money+=result.getMoney();
			fen+=result.getFen();
		}
		String str = String.format(getResources().getString(R.string.text_vio_result), area+"  ",list.size(),money,fen);
		
		SpannableString s = new SpannableString(str);
		s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.red)), str.indexOf(String.valueOf(list.size())), str.indexOf(String.valueOf(list.size()))+String.valueOf(list.size()).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.red)), str.indexOf(String.valueOf(money)), str.indexOf(String.valueOf(money))+String.valueOf(money).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.red)), str.indexOf(String.valueOf(fen)), str.indexOf(String.valueOf(fen))+String.valueOf(fen).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		text_info.setText(s);
	}

	private void regListener() {
		
		listView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() == view.getCount() - 1) {
						if(hasMore) {
							refresh = false;
							getViolation();
						}else {
							Utils.showToast(IllegalResultActivity.this, R.string.has_no_more_data);
						}
					}
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		default:
			break;
		}
		super.onClick(v);
	}
	
	private void getViolation() {
		Intent intent = getIntent();
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.CITY, intent.getStringExtra(ParamsKey.CITY));
		params.put(ParamsKey.Hphm, intent.getStringExtra(ParamsKey.Hphm));
		params.put(ParamsKey.Hpzl, intent.getStringExtra(ParamsKey.Hpzl));
		params.put(ParamsKey.Engineno, intent.getStringExtra(ParamsKey.Engineno));
		params.put(ParamsKey.Classno, intent.getStringExtra(ParamsKey.Classno));
		params.put(ParamsKey.Registno,intent.getStringExtra(ParamsKey.Registno));
		if(refresh) {
			if(list.size()>0) {
				params.put(ParamsKey.MaxTime, list.get(0).getDate());
			}
		}else {
			if(list.size()>1) {
				params.put(ParamsKey.MaxTime, list.get(list.size()-1).getDate());
			}
		}
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Violation_Query_Url) , params,new AjaxCallBack<String>() {

			private String msg;

			@Override
			public void onStart() {
				showLoading();
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				dismissLoading();
				LogUtils.e(TAG, t.toString());
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						if("<".equals(String.valueOf(t.charAt(0)))) {
							showLoginDialog();
						}else {
							JSONObject json = new JSONObject(t);
							if(json.has("status")) {
								int status = json.getInt("status");
								SettingLoader.setQueryDate(IllegalResultActivity.this,TimeUtils.getCurrentTime());
								SettingLoader.setIllegalCityAndCode(IllegalResultActivity.this,area,getIntent().getStringExtra(ParamsKey.CITY));
								if(1==status) {
									if(json.has("result")){
										JSONArray array = json.getJSONArray("result");
										if(array!=null&&array.length()>0) {
											ArrayList<ViolationResult> slist = new ArrayList<ViolationResult>();
											ViolationResult violation = null;
											for(int i=0;i<array.length();i++) {
												violation = new ViolationResult();
												violation.parser((JSONObject)array.opt(i));
												slist.add(violation);
											}
											if(refresh) {
												list.addAll(0,slist);
											}else {
												list.addAll(slist);
											}
											hasMore = true;
											RuleBreakOpenHelper.getInstance(IllegalResultActivity.this).batchInfos(SettingLoader.getVehicleId(IllegalResultActivity.this), slist,SettingLoader.getIllegalCity(IllegalResultActivity.this));
										}else {
											hasMore = false;
										}
									}else {
										hasMore = true;
									}
								}else {
									hasMore = true;
									msg = json.getString("message");
									Utils.showToast(IllegalResultActivity.this, msg);
								}
								XtrdApp.sendMsg(XtrdApp.ID_REFRESH_ILLEGAL);
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
				super.onFailure(t, errorNo, strMsg);
				swipeLayout.setRefreshing(false);
				Utils.showToast(IllegalResultActivity.this, getResources().getString(R.string.data_load_fail));
				dismissLoading();
			}

		});
	}

	protected void updateUI() {
		swipeLayout.setRefreshing(false);
		adapter.notifyDataSetChanged();
	}
}
