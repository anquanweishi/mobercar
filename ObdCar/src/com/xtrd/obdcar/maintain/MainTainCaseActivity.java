package com.xtrd.obdcar.maintain;

import java.util.ArrayList;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.adapter.MainTainCheckdapter;
import com.xtrd.obdcar.adapter.ReservationMainTainAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.MainTainChildItem;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.view.MyGridView;
import com.xtrd.obdcar.view.MyListView;
/**
 * 保养项 可修改
 * @author Administrator
 *
 */
public class MainTainCaseActivity extends BaseActivity {

	private LinearLayout layout_choose_items;
	private MyGridView gridView;
	private MyListView listView;

	private ReservationMainTainAdapter adapter;
	private MainTainCheckdapter selectAdapter;


	private ArrayList<MainTainChildItem> selItems = new ArrayList<MainTainChildItem>();

	private ArrayList<MainTainChildItem> maintains = new ArrayList<MainTainChildItem>();

	public MainTainCaseActivity() {
		layout_id = R.layout.activity_maintain_case;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg, R.string.title_maintain_item, R.string.btn_reset, 0);
		initView();
		regClick();
		getMainTainItems();
	}


	private void initView() {
		layout_choose_items = (LinearLayout)findViewById(R.id.layout_choose_items);
		gridView = (MyGridView)findViewById(R.id.gridView);
		listView = (MyListView)findViewById(R.id.listView);
	}

	private void regClick() {
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				maintains.get(position).setChecked(!maintains.get(position).isChecked());
				if(adapter!=null) {
					adapter.notifyDataSetChanged();
				}
				if(maintains.get(position).isChecked()) {
					selItems.add(maintains.get(position));
				}else {
					for(int i=selItems.size()-1;i>=0;i--) {
						if(selItems.get(i).getId()==maintains.get(position).getId()) {
							selItems.remove(i);
						}
					}
				}
				updateSelectItems();
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
			Intent intent = new Intent();
			intent.putExtra("items", getSelectItems());
			intent.putExtra("value", getSelectValue());
			setResult(RESULT_OK, intent);
			finish();
			break;

		default:
			break;
		}
	}


	/**
	 * 更新已选择的项目
	 */
	protected void updateSelectItems() {
		if(selItems!=null&&selItems.size()>0) {
			layout_choose_items.setVisibility(View.VISIBLE);
			selectAdapter = new MainTainCheckdapter(this, selItems,new MainTainCheckdapter.CallBack() {
				
				@Override
				public void callback(MainTainChildItem info) {
					if(maintains!=null) {
						updateMaintain(info.getId());
					}
					if(selectAdapter!=null) {
						selectAdapter.notifyDataSetChanged();
					}
					if(adapter!=null) {
						adapter.notifyDataSetChanged();
					}
				}
			});
			gridView.setAdapter(selectAdapter);

		}else {
			layout_choose_items.setVisibility(View.GONE);
		}

	}

	protected void updateMaintain(int id) {
		for(MainTainChildItem info : maintains) {
			if(id==info.getId()) {
				info.setChecked(false);
			}
		}
	}

	private void getMainTainItems() {
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, SettingLoader.getVehicleId(this));
		NetRequest.requestUrl(this, ApiConfig.getRequestUrl(ApiConfig.MainTain_Items_Url), params, new NetRequest.NetCallBack() {

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
										MainTainChildItem item = null;
										for(int i=0;i<array.length();i++) {
											item = new MainTainChildItem();
											item.parser(array.optJSONObject(i));
											maintains.add(item);
										}
									}
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
		if(!StringUtils.isNullOrEmpty(getIntent().getStringExtra("items"))) {
			updateHasSelectItems(getIntent().getStringExtra("items"));
		}
		if(adapter==null) {
			adapter = new ReservationMainTainAdapter(this, maintains);
			listView.setAdapter(adapter);
		}else {
			adapter.notifyDataSetChanged();
		}
		
	}
	
	private void updateHasSelectItems(String selects) {
		if(!StringUtils.isNullOrEmpty(selects)) {
			for(MainTainChildItem info : maintains) {
				if(selects.contains(info.getId()+"")) {
					info.setChecked(true);
					selItems.add(info);
				}
			}
		}
		
		updateSelectItems();
		
	}

	private String getSelectItems() {
		String items = "";
		if(maintains!=null&&maintains!=null) {
			for(MainTainChildItem info : maintains) {
				if(info.isChecked()) {
					items+=info.getId()+",";
				}
			}
		}
		return items;
	}
	private String getSelectValue() {
		String value = "";
		if(maintains!=null&&maintains!=null) {
			for(MainTainChildItem info : maintains) {
				if(info.isChecked()) {
					value+=info.getName()+",";
				}
			}
		}
		if(value.length()>0) {
			return value.substring(0,value.lastIndexOf(","));
		}else {
			return value;
		}
	}
}
