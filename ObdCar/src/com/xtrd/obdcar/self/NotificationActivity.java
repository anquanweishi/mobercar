package com.xtrd.obdcar.self;

import java.util.ArrayList;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.adapter.NotifyAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.entity.NotifyMessage;
import com.xtrd.obdcar.entity.NotifyType;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.view.ObdListDialog;

public class NotificationActivity extends BaseActivity {
	private LinearLayout layout_select;
	private ListView listView;
	private TextView tips_text;
	
	private ArrayList<NotifyType> types = new ArrayList<NotifyType>();
	private ArrayList<NotifyMessage> list = new ArrayList<NotifyMessage>();
	private NotifyAdapter adapter;
	protected boolean hasMore = true;
	private int currentType = 0;
	protected boolean refresh = true;

	public NotificationActivity() {
		layout_id = R.layout.activity_my_notify;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg, R.string.title_notification, 0, 0);
		initView();

		getNotify(0);
	}


	private void initView() {
		layout_select = (LinearLayout)findViewById(R.id.layout_select);
		layout_select.setOnClickListener(this);
		listView = (ListView)findViewById(R.id.listView);
		tips_text = (TextView)findViewById(R.id.tips_text);
		
		listView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() == view.getCount() - 1) {
						if(hasMore) {
							getNotify(currentType);
						}else {
							Utils.showToast(NotificationActivity.this, R.string.has_no_more_data);
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
		case R.id.layout_select:
			showChooseDialog();
			break;

		default:
			break;
		}
	}

	private void showChooseDialog() {
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.ID, "");
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
										types.add(new NotifyType("全部消息"));
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
		String[] datas = new String[types.size()];
		for(int i=0;i<types.size();i++) {
			datas[i] = types.get(i).getName();
		}
		new ObdListDialog(this)
		.setList(datas)
		.setItemButton(new ObdListDialog.OnClickListener() {

			@Override
			public void onClick(String value) {
				((TextView)layout_select.getChildAt(0)).setText(value);
				refresh = true;
				list.clear();
				if(adapter!=null) {
					adapter.notifyDataSetChanged();
				}
				int typeId = getTypeId(value);
				getNotify(typeId==0?0:typeId);
			}
		})
		.show();
	}
	
	private int getTypeId(String name) {
		for(NotifyType item : types) {
			if(name.equals(item.getName())) {
				return item.getId();
			}
		}
		return 0;
	}

	private void getNotify(int condition) {
		AjaxParams params = new AjaxParams();
		if(0!=condition) {
			params.put(ParamsKey.TYPE, condition+"");
		}
		if(refresh) {
			if(list!=null&&list.size()>0) {
				params.put(ParamsKey.MaxTime, list.get(0).getTime());
			}
		}else {
			if(list!=null&&list.size()>0) {
				params.put(ParamsKey.MinTime, list.get(list.size()-1).getTime());
			}
		}
		NetRequest.requestUrl(this, ApiConfig.getRequestUrl(ApiConfig.Notify_Url) ,params,new NetRequest.NetCallBack() {

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
										JSONArray array = json.getJSONArray("result");
										if(array!=null&&array.length()>0) {
											NotifyMessage message = null;
											for(int i=0;i<array.length();i++) {
												message = new NotifyMessage();
												message.parser(array.optJSONObject(i));
												list.add(message);
											}
										}
									}
								}
							}
							updateUI();
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

	protected void updateUI() {
		if(list==null||list.size()==0) {
			tips_text.setVisibility(View.VISIBLE);
			tips_text.setText(getResources().getString(R.string.tips_no_data));
			listView.setVisibility(View.GONE);
		}else {
			listView.setVisibility(View.VISIBLE);
			tips_text.setVisibility(View.GONE);
			if(adapter==null) {
				adapter = new NotifyAdapter(this, list);
				listView.setAdapter(adapter);
			}else {
				adapter.notifyDataSetChanged();
			}
		}
	}

}
