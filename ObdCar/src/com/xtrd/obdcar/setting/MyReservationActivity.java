package com.xtrd.obdcar.setting;


import java.util.ArrayList;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.adapter.MyReservationAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.entity.Reservation;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.NetUtils;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;

public class MyReservationActivity extends BaseActivity {

	protected static final String TAG = "MyReservationActivity";
	private TextView text_fen,text_order,text_suc_order;
	private ListView listView;

	private ArrayList<Reservation> list = new ArrayList<Reservation>();
	private TextView tips_text;
	private boolean hasMore = false;//是否有更多
	private MyReservationAdapter adapter;



	public MyReservationActivity() {
		layout_id = R.layout.activity_my_reservation;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg,R.string.title_my_reservation, 0,0);

		initView();

		if(!NetUtils.IsNetworkAvailable()) {
			Utils.showNetTips(this);
			return;
		}
		getMyReservation();
	}



	private void initView() {

		View view = LayoutInflater.from(this).inflate(R.layout.layout_my_reservation_top, null);
		text_fen = (TextView)view.findViewById(R.id.text_fen);
		text_order = (TextView)view.findViewById(R.id.text_order);
		text_suc_order = (TextView)view.findViewById(R.id.text_suc_order);

		tips_text = (TextView)findViewById(R.id.tips_text);
		listView = (ListView)findViewById(R.id.listView);
		listView.setDivider(new ColorDrawable(getResources().getColor(R.color.interval)));
		listView.setDividerHeight(Utils.dipToPixels(this, 1));
		listView.setSelector(android.R.color.transparent);
		listView.addHeaderView(view);
		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() == view.getCount() - 1) {
						if(hasMore) {
							getMyReservation();
						}else {
							Utils.showToast(MyReservationActivity.this, R.string.tips_no_data);
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
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_right:

			break;
		default:
			break;
		}
	}

	private void getMyReservation() {
		AjaxParams params = new AjaxParams();
		if(hasMore) {
			if(list!=null&&list.size()>0) {
				params.put(ParamsKey.MinTime, list.get(list.size()-1).getTime());
			}
		}
		NetRequest.requestUrl(this,
				ApiConfig.getRequestUrl(ApiConfig.My_Order_Url), params,
				new NetRequest.NetCallBack() {

			@Override
			public void sucCallback(String str) {
				try {
					if (!StringUtils.isNullOrEmpty(str)) {
						JSONObject json = new JSONObject(str);
						if (json.has("status")) {
							int status = json.getInt("status");
							if (1 == status) {
								if (json.has("result")) {
									JSONArray array = json.getJSONArray("result");
									if(array!=null&&array.length()>0) {
										Reservation item = null;
										for(int i=0;i<array.length();i++) {
											item = new Reservation();
											item.parser(array.optJSONObject(i));
											list.add(item);
										}
										hasMore = true;
									}else {
										hasMore = false;
									}
								}else {
									hasMore = false;
								}

								if(json.has("datas")) {
									updateTop(json.getJSONObject("datas"));
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


	protected void updateTop(JSONObject json) {
		try {
			text_fen.setText(String.format(getResources().getString(R.string.text_my_reservation_score), json.getInt("score")));
			text_order.setText(String.format(getResources().getString(R.string.text_my_reservation_order), json.getInt("sum")));
			text_suc_order.setText(String.format(getResources().getString(R.string.text_my_reservation_rate), json.getString("rate")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	protected void updateUI() {
		if(list!=null&&list.size()>0) {
			tips_text.setVisibility(View.GONE);
			if(listView.getAdapter()==null) {
				adapter = new MyReservationAdapter(this, list);
				listView.setAdapter(adapter);
			}else {
				adapter.notifyDataSetChanged();
			}
		}else {
			tips_text.setVisibility(View.VISIBLE);
			tips_text.setText(getResources().getString(R.string.tips_no_data));
		}
	}
}
