package com.xtrd.obdcar.vc;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.adapter.HistoryVCAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.VConditionGroup;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.TimeUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.view.DateDialog;
/**
 * 历史车况
 * @author Administrator
 *
 */
public class HistoryVCActivity extends BaseActivity {
	

	private TextView text_start_date;
	private TextView text_end_date;
	private Button btn_search;
	private ExpandableListView listView;
	private TextView tips_text;

	private ArrayList<VConditionGroup> list = new ArrayList<VConditionGroup>();
	private String start_date,end_date;
	protected boolean refresh = true;
	protected boolean hasMore;
	private HistoryVCAdapter adapter;
	
	public HistoryVCActivity() {
		layout_id = R.layout.activity_history_vc;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg,R.string.title_vc, 0,0);
		initView();
		
		getHistoryVc();
	}


	private void initView() {
		text_start_date = (TextView)findViewById(R.id.text_start_date);
		text_end_date = (TextView)findViewById(R.id.text_end_date);
		btn_search = (Button)findViewById(R.id.btn_search);
		text_start_date.setOnClickListener(this);
		text_end_date.setOnClickListener(this);
		btn_search.setOnClickListener(this);
		
		start_date = TimeUtils.getPreMonth(1,null);
		end_date = TimeUtils.getCurrentMonth();
		text_start_date.setText(start_date);
		text_end_date.setText(end_date);
		
		tips_text = (TextView)findViewById(R.id.tips_text);
		listView = (ExpandableListView)findViewById(R.id.expandablelist);
		listView.setOnGroupClickListener(new OnGroupClickListener() {
			
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				return true;
			}
		});
		listView.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Intent intent = new Intent(HistoryVCActivity.this,HistoryVCDetailActivity.class);
				intent.putExtra("name",list.get(groupPosition).getList().get(childPosition).getName());
				intent.putExtra("time",list.get(groupPosition).getTime());
				intent.putExtra("desc",list.get(groupPosition).getList().get(childPosition).getDescription());
				intent.putExtra("stand",list.get(groupPosition).getList().get(childPosition).getStand());
				startActivity(intent);
				return false;
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
		case R.id.btn_search:
			start_date = text_start_date.getText().toString();
			end_date = text_end_date.getText().toString();
			if(TimeUtils.compare(start_date,end_date,"yyyy-MM")) {
				list.clear();
				getHistoryVc();
			}else {
				Utils.showToast(HistoryVCActivity.this, "起始日期应早于结束日期");
			}
			
			break;
		case R.id.text_start_date:
			start_date = text_start_date.getText().toString();
			showDateDialog(true,start_date);
			break;
		case R.id.text_end_date:
			end_date = text_end_date.getText().toString();
			showDateDialog(false,end_date);
			break;
		default:
			break;
		}
	}
	
	private void showDateDialog(final boolean start, String date) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
			Date parse = format.parse(date);
			new DateDialog(this,false,parse)
			.setPositiveButton(new DateDialog.OnClickListener() {

				@Override
				public void onClick(String date) {
					if(start) {
						start_date = date;
						text_start_date.setText(date);
					}else {
						if(TimeUtils.compare(start_date,date,"yyyy-MM")) {
							end_date = date;
							text_end_date.setText(date);
						}else {
							Utils.showToast(HistoryVCActivity.this, "起始日期应早于结束日期");
						}
					}
				}
			}).show();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	private void getHistoryVc() {
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, SettingLoader.getVehicleId(this));
		params.put(ParamsKey.StartTime,  TimeUtils.getFirstDatebyMonth(start_date)+" 00:00:00");
		params.put(ParamsKey.EndTime, TimeUtils.getLastDatebyMonth(end_date)+" 23:59:59");
		NetRequest.requestUrl(this, ApiConfig.getRequestUrl(ApiConfig.Fault_Stats_Url), params,new NetRequest.NetCallBack() {
			
			private String msg;

			@Override
			public void sucCallback(String str) {
				try {
					JSONObject json = new JSONObject(str);
					if(json.has("status")) {
						int status = json.getInt("status");
						if(1==status) {
							if(refresh ) {
								list.clear();
							}
							if(json.has("result")) {
								JSONArray array = json.getJSONArray("result");
								if(array!=null&&array.length()>0) {
									VConditionGroup group = null;
									for(int i=0;i<array.length();i++) {
										group = new VConditionGroup();
										group.parser((JSONObject)array.opt(i));
										list.add(group);
									}
									hasMore = true;
								}
							}else {
								hasMore = false;
							}
						}else {
							msg = json.getString("message");
							Utils.showToast(HistoryVCActivity.this, msg);
						}
					}
					updateUI();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void failCallback(int errorNo, String strMsg) {
				Utils.showToast(HistoryVCActivity.this,getResources().getString(R.string.data_load_fail));
			}
		});
		
	}

	protected void updateUI() {
		if(list==null||list.size()==0) {
			listView.setVisibility(View.GONE);
			tips_text.setText(getResources().getString(R.string.tips_no_data));
			tips_text.setVisibility(View.VISIBLE);
		}else {
			listView.setVisibility(View.VISIBLE);
			tips_text.setVisibility(View.GONE);
			if(adapter==null) {
				adapter = new HistoryVCAdapter(this, list);
				listView.setAdapter(adapter);
			}else {
				adapter.notifyDataSetChanged();
			}
			
			for(int i=0;i<list.size();i++) {
				listView.expandGroup(i);
			}
			
		}
		
	}
}
