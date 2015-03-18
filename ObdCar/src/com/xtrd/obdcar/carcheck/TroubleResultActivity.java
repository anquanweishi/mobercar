package com.xtrd.obdcar.carcheck;

import java.util.ArrayList;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.http.PreferencesCookieStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.entity.CarTrouble;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.utils.log.LogUtils;

public class TroubleResultActivity extends BaseActivity {
	protected static final String TAG = "TroubleResultActivity";
	private ArrayList<CarTrouble> list,tempList;
	private ListView listView;
	private int position;

	public TroubleResultActivity() {
		layout_id = R.layout.activity_trouble;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg, R.string.title_trouble, R.string.btn_see_all,0);
		boolean from = getIntent().getBooleanExtra("from", false);
		if(from) {
			getTrouble();
		}else {
			list = getIntent().getParcelableArrayListExtra("list");
			position = getIntent().getIntExtra("position", 0);
			if(tempList==null) {
				tempList = new ArrayList<CarTrouble>();
			}
			tempList.addAll(list);
			if(list!=null) {
				for(int i=0;i<list.size();i++) {
					if(i!=position) {
						list.remove(i);
					}
				}
			}
		}
		
		initView();
	}

	private void initView() {
		listView = (ListView)findViewById(R.id.listView);
		ListAdapter adapter = new ListAdapter();
		listView.setAdapter(adapter);
	}
	
	
	protected void updateUI() {
		if(listView.getAdapter()==null) {
			ListAdapter adapter = new ListAdapter();
			listView.setAdapter(adapter);
		}else {
			((ListAdapter)listView.getAdapter()).notifyDataSetChanged();
		}
	}
	
	/**
	 * 获取车辆故障
	 */
	private void getTrouble() {
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, getIntent().getIntExtra("vehicleId", 0)+"");
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Car_Fault_URL) ,params, new AjaxCallBack<String>() {

			private String msg;

			@Override
			public void onStart() {
				super.onStart();
				showLoading();
			}

			@Override
			public void onSuccess(String t) {
				LogUtils.e(TAG, "info "+t.toString());
				dismissLoading();
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						JSONObject json = new JSONObject(t);
						if("<".equals(String.valueOf(t.charAt(0)))) {
							showLoginDialog();
						}else {
							if(json.has("status")) {
								int status = json.getInt("status");
								if(1==status) {
									if(list==null) {
										list = new ArrayList<CarTrouble>();
									}else {
										list.clear();
									}
									if(json.has("result")) {
										JSONArray array = json.getJSONArray("result");
										if(array!=null&&array.length()>0) {
											CarTrouble health = null;
											for(int i=0;i<array.length();i++) {
												health = new CarTrouble();
												health.parser(array.optJSONObject(i));
												list.add(health);
											}
										}
									}
									updateUI();
								}else {
									msg = json.getString("message");
									Utils.showToast(TroubleResultActivity.this, msg);
								}
							}
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
				Utils.showToast(TroubleResultActivity.this, getResources().getString(R.string.data_load_fail));
				dismissLoading();
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
			if(tempList!=null&&tempList.size()>0) {
				list.clear();
				list.addAll(tempList);
				tempList.clear();
				tempList = null;
			}
			if(listView.getAdapter()==null) {
				ListAdapter adapter = new ListAdapter();
				listView.setAdapter(adapter);
			}else {
				((ListAdapter)listView.getAdapter()).notifyDataSetChanged();
			}
			break;

		default:
			break;
		}
	}
	
	final class ListAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return list!=null?list.size():0;
		}

		@Override
		public CarTrouble getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder view = null;
			if (convertView == null) {
				view = new ViewHolder();
				convertView = LayoutInflater.from(TroubleResultActivity.this).inflate(R.layout.layout_trouble_item, null);
				view.view = (View) convertView.findViewById(R.id.view);
				view.text_title = (TextView)convertView.findViewById(R.id.text_title);
				view.text_time = (TextView)convertView.findViewById(R.id.text_time);
				view.text_desc = (TextView)convertView.findViewById(R.id.text_desc);
				convertView.setTag(view);
			} else {
				view = (ViewHolder) convertView.getTag();
			}
			CarTrouble item = getItem(position);
			updateUI(view,item);

			return convertView;
		}
		private void updateUI(ViewHolder view, CarTrouble item) {
			view.text_title.setText(item.getDetail().getName());
			view.text_time.setText(item.getCreateTime());
			view.text_desc.setText(item.getDetail().getDescription());
		}
		class ViewHolder {
			View view;
			TextView text_title;
			TextView text_time;
			TextView text_desc;
		}
	}

}
