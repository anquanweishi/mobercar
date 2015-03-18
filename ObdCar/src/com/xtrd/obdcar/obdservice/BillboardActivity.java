package com.xtrd.obdcar.obdservice;


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
import android.widget.ListView;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.adapter.BillBoardAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.CarUser;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.view.ObdListDialog;

public class BillboardActivity extends BaseActivity {

	private TextView btn_oil_wear;
	private TextView btn_skill_drvie;
	private TextView text_num;
	private ListView listView;
	private BillBoardAdapter adapter;
	private ArrayList<CarUser> list = new ArrayList<CarUser>();
	
	private int type = 1;//0周排名 月排名
	public BillboardActivity() {
		layout_id = R.layout.activity_billboard;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg, R.string.title_billboard, 0, 0);
		initView();
		regClick();
		getList("0", type);
	}

	private void initView() {
		btn_oil_wear = (TextView)findViewById(R.id.btn_oil_wear);
		btn_skill_drvie = (TextView)findViewById(R.id.btn_skill_drvie);
		text_num = (TextView)findViewById(R.id.text_num);
		listView = (ListView)findViewById(R.id.listView);
	}

	private void regClick() {
		btn_oil_wear.setOnClickListener(this);
		btn_skill_drvie.setOnClickListener(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(BillboardActivity.this,UserDetailActivity.class);
				intent.putExtra(ParamsKey.VEHICLEID, list.get(position).getVid());
				intent.putExtra(ParamsKey.TYPE, type);
				intent.putExtra("fromBill", true);
				startActivity(intent);
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
		case R.id.btn_oil_wear:
			btn_oil_wear.setBackgroundColor(getResources().getColor(R.color.gray));
			btn_skill_drvie.setBackgroundResource(R.drawable.listview_bg);
			list.clear();
			if(adapter!=null) {
				adapter.notifyDataSetChanged();
			}
			showChooseListDialog("0");
			
			break;
		case R.id.btn_skill_drvie:
			btn_skill_drvie.setBackgroundColor(getResources().getColor(R.color.gray));
			btn_oil_wear.setBackgroundResource(R.drawable.listview_bg);
			list.clear();
			if(adapter!=null) {
				adapter.notifyDataSetChanged();
			}
			showChooseListDialog("1");
			break;

		default:
			break;
		}
	}

	private void showChooseListDialog(final String cat) {
		String[] datas = new String[]{"按周排名","按月排名"};
		new ObdListDialog(this).setList(datas)
		.setItemButton(new ObdListDialog.OnClickListener() {

			@Override
			public void onClick(String value) {
				type = "按周排名".equals(value)?0:	1;
				getList(cat,type);
			}
		})
		.show();
		
	}

	private void getList(final String cat,int type) {
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID,SettingLoader.getVehicleId(this)+"");
		params.put(ParamsKey.Cat,cat);
		params.put(ParamsKey.TYPE,type+"");
		NetRequest.requestUrl(this, ApiConfig.getRequestUrl(ApiConfig.Bill_Board_Url), params, new NetRequest.NetCallBack() {

			@Override
			public void sucCallback(String str) {
				try {
					if(!StringUtils.isNullOrEmpty(str)) {
						JSONObject json = new JSONObject(str);
						if(json.has("status")) {
							int status = json.getInt("status");
							if(1==status) {
								
								if(json.has("result")) {
									json = json.getJSONObject("result");
									if(json.has("datas")) {
										JSONArray array = json.getJSONArray("datas");
										CarUser item = null;
										for(int i=0;i<array.length();i++) {
											item = new CarUser();
											item.parser(array.optJSONObject(i));
											list.add(item);
										}
									}
									updateTop(cat,json);
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

			}
		});
	}

	protected void updateTop(String cat, JSONObject json) {
		if(!SettingLoader.hasLogin(this)) {
			((LinearLayout)text_num.getParent()).setVisibility(View.GONE);
		}else {
			((LinearLayout)text_num.getParent()).setVisibility(View.VISIBLE);
			String value = "";
			try {
				if("0".equals(cat)) {
					if(json!=null) {
						value+="您在同排量（";
						if(json.has("displacement")) {
							value+= json.getString("displacement");
						}
						if(json.getInt("sort")!=0) {
							value += "）油耗排名第：";
							if(json.has("sort")) {
								value += json.getInt("sort");
							}
							value+="位";
						}else {
							value += "）油耗排名未知";
						}
					}
				}else {
					if(json!=null) {
						if(json.getInt("sort")!=0) {
							value += "您的驾驶技能排名第：";
							if(json.has("sort")) {
								value += json.getInt("sort");
							}
							value+="位";
						}else {
							value += "您的驾驶技能排名未知";
						}
					
					}
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			text_num.setText(value);
		}
	}

	

	protected void updateUI() {
		if(list!=null&&list.size()>0) {
			if(adapter==null) {
				adapter = new BillBoardAdapter(this, list);
				listView.setAdapter(adapter);
			}else {
				adapter.notifyDataSetChanged();
			}
		}else {
			
		}
	}

}
