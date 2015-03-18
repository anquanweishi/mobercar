package com.xtrd.obdcar.oil;

import java.util.ArrayList;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.adapter.FriendReportAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.entity.GasComment;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;

public class FriendOilReportActivity extends BaseActivity {
	private TextView text_plate;
	private TextView text_branch;
	private TextView text_time;
	private ListView listView;

	private ArrayList<GasComment> comments = new ArrayList<GasComment>();
	protected boolean hasMore = true;
	private boolean refresh = true;
	private FriendReportAdapter adapter;


	public FriendOilReportActivity() {
		layout_id = R.layout.activity_freind_report;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg, R.string.title_friend_oil_price_report, 0,0);
		initView();
		getComment();
	}

	private void initView() {
		listView = (ListView)findViewById(R.id.listView);
		View view = View.inflate(this, R.layout.layout_friend_report, null);
		text_plate = (TextView)view.findViewById(R.id.text_plate);
		text_branch = (TextView)view.findViewById(R.id.text_branch);
		text_time = (TextView)view.findViewById(R.id.text_time);
		listView.addHeaderView(view);
		adapter = new FriendReportAdapter(this, comments);
		listView.setAdapter(adapter);
		updateCarInfo();
		
		listView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() == view.getCount() - 1) {
						if(hasMore) {
							getComment();
						}else {
							Utils.showToast(FriendOilReportActivity.this, R.string.has_no_more_data);
						}
					}
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	private void updateCarInfo() {
		Intent intent = getIntent();
		text_plate.setText(intent.getStringExtra("title"));
		text_branch.setText(intent.getStringExtra("address"));
		text_time.setText(intent.getStringExtra("distance"));
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


	private void getComment() {
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.ID,getIntent().getIntExtra(ParamsKey.ID, 0)+"");
		if(refresh ) {
			if(comments!=null&&comments.size()>0) {
				params.put(ParamsKey.MaxTime,comments.get(0).getCreateTime());
			}
		}else {
			if(comments!=null&&comments.size()>0) {
				params.put(ParamsKey.MinTime,comments.get(comments.size()-1).getCreateTime());
			}
		}
		NetRequest.requestUrl(this, ApiConfig.getRequestUrl(ApiConfig.GasStation_Comment_Url), params, new NetRequest.NetCallBack() {

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
										GasComment comment = null;
										for(int i=0;i<array.length();i++) {
											comment = new GasComment();
											comment.parser(array.optJSONObject(i));
											comments.add(comment);
										}
										hasMore = true;
									}else {
										hasMore  = false;
									}
								}else {
									hasMore = false;
								}
							}else {
								hasMore = false;
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
			}
		});
	}

	protected void updateUI() {
		if(comments!=null&&comments.size()>0) {
			adapter.notifyDataSetChanged();
		}else {
			
		}
	}
}
