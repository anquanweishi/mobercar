package com.xtrd.obdcar.maintain;



import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.adapter.MainTainRecoderAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.entity.Recoder;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;

public class MaintainRecoderActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{

	protected static final String TAG = "MaintainRecoderActivity";
	private SwipeRefreshLayout swipeLayout;
	private ListView listView;

	private ArrayList<Recoder> list = new ArrayList<Recoder>();
	private boolean refresh = false;
	private TextView tips_text;
	private boolean hasMore = false;//是否有更多
	private MainTainRecoderAdapter adapter;



	public MaintainRecoderActivity() {
		layout_id = R.layout.activity_maintain_recoder;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg,R.string.title_reservation_recoder, 0,0);

		initView();

		startRefresh();
	}

	private void startRefresh() {
		try {
			Method refresh = null;
			Method[] methods = swipeLayout.getClass().getDeclaredMethods();
			for (Method method : methods) {
				if ("startRefresh".equals(method.getName())) {
					refresh = method;
				}
			}
			if (refresh != null) {
				refresh.setAccessible(true);
				refresh.invoke(swipeLayout);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}



	private void initView() {

		
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorScheme(R.color.holo_blue_bright, R.color.holo_green_light,
				R.color.holo_orange_light, R.color.holo_red_light);
		tips_text = (TextView)findViewById(R.id.tips_text);
		listView = (ListView)findViewById(R.id.listView);
		listView.setDivider(new ColorDrawable(getResources().getColor(R.color.interval)));
		listView.setDividerHeight(Utils.dipToPixels(this, 1));
		listView.setSelector(android.R.color.transparent);
		
		
		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				refresh = false;
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() == view.getCount() - 1) {
						if(hasMore) {
							getMyReservation();
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
		default:
			break;
		}
	}

	@Override
	public void onRefresh() {
		refresh = true;
		getMyReservation();
	}
	private void getMyReservation() {
		AjaxParams params = new AjaxParams();
		if(refresh) {
			if(list!=null&&list.size()>0) {
				params.put(ParamsKey.MaxTime, list.get(0).getLastTime());
			}
		}else {
			if(list!=null&&list.size()>0) {
				params.put(ParamsKey.MinTime, list.get(list.size()-1).getLastTime());
			}
		}
		NetRequest.requestUrl(this,
				ApiConfig.getRequestUrl(ApiConfig.Maintain_Recoders_Url), params,
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
										Recoder item = null;
										for(int i=0;i<array.length();i++) {
											item = new Recoder();
											item.parser(array.optJSONObject(i));
											list.add(item);
										}
										hasMore = true;
									}else {
										hasMore = false;
									}
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
				swipeLayout.setRefreshing(false);
			}
		});
	}


	protected void updateUI() {
		swipeLayout.setRefreshing(false);
		if(list!=null&&list.size()>0) {
			tips_text.setVisibility(View.GONE);
			if(listView.getAdapter()==null) {
				adapter = new MainTainRecoderAdapter(this, list);
				listView.setAdapter(adapter);
			}else {
				adapter.notifyDataSetChanged();
			}
		}else {
			tips_text.setVisibility(View.VISIBLE);
			tips_text.setText(getResources().getString(R.string.tips_no_recoder));
		}
	}
}
