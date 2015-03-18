package com.xtrd.obdcar.maintain;

import java.util.ArrayList;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.adapter.MaintainItemAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.MainTainChildItem;
import com.xtrd.obdcar.entity.MainTainItem;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.view.InputDialog;
/**
 * 保养项 可修改
 * @author Administrator
 *
 */
public class MainTainItemActivity extends BaseActivity {

	private ExpandableListView expandlist;
	private MaintainItemAdapter adapter;
	private ArrayList<MainTainItem> list = new ArrayList<MainTainItem>();

	public MainTainItemActivity() {
		layout_id = R.layout.activity_maintain_item;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg, R.string.title_maintain_item, 0, 0);
		initView();
		
		getItems();
	}


	private void initView() {
		expandlist = (ExpandableListView)findViewById(R.id.expandlist);
		expandlist.setGroupIndicator(null);
		
		expandlist.setOnGroupClickListener(new OnGroupClickListener() {
			
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				return false;
			}
		});
		
		expandlist.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				//弹出修改框
				showInputDialog(list.get(groupPosition).getList().get(childPosition));
				return false;
			}
		});
	}
	
	protected void showInputDialog(final MainTainChildItem item) {
		new InputDialog(this)
		.setTitle(item.getName()+"保养周期")
		.setMessage("设置"+item.getName()+"保养的周期公里数")
		.setUnit("公里")
		.setInputType(InputType.TYPE_CLASS_NUMBER)
		.setPositiveButton("立即设置", new InputDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
			@Override
			public void onClick(DialogInterface dialog, int which, String input) {
				updateMaintain(input,item);
			}
		})
		.setNegativeButton("我知道了", new InputDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}

			@Override
			public void onClick(DialogInterface dialog, int which, String input) {

			}
		})
		.show();
	}

	/**
	 * 修改保养周期
	 */
	protected void updateMaintain(final String distance,final MainTainChildItem item) {
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, SettingLoader.getVehicleId(this));
		params.put(ParamsKey.Active_Distance, distance);
		params.put(ParamsKey.ItemId, item.getId()+"");
		params.put(ParamsKey.TYPE, "1");
		NetRequest.requestUrl(this, ApiConfig.getRequestUrl(ApiConfig.Maintain_Zhouqi_Set_Url), params, new NetRequest.NetCallBack() {

			@Override
			public void sucCallback(String str) {
				try {
					if(!StringUtils.isNullOrEmpty(str)) {
						JSONObject json = new JSONObject(str);
						if(json.has("status")) {
							int status = json.getInt("status");
							if(1==status) {
								Utils.showToast(MainTainItemActivity.this, "设置成功");
								item.setRange(distance);
								adapter.notifyDataSetChanged();
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void failCallback(int errorNo, String strMsg) {
				// TODO Auto-generated method stub

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
	
	private void getItems() {
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, SettingLoader.getVehicleId(this));
		NetRequest.requestUrl(this,
				ApiConfig.getRequestUrl(ApiConfig.Maintain_Item_Url), params,
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
										MainTainItem item = null;
										for(int i=0;i<array.length();i++) {
											item = new MainTainItem();
											item.parser(array.optJSONObject(i));
											list.add(item);
										}
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
			}
		});
	}

	
	protected void updateUI() {
		if(adapter==null) {
			adapter = new MaintainItemAdapter(this,list);
			expandlist.setAdapter(adapter);
		}else {
			adapter.notifyDataSetChanged();
		}
		for(int i=0;i<list.size();i++) {
			expandlist.expandGroup(i);
		}
	}

}
