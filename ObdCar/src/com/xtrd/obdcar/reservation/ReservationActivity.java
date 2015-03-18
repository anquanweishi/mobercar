package com.xtrd.obdcar.reservation;

import java.util.ArrayList;
import java.util.Date;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.adapter.MainTainCheckdapter;
import com.xtrd.obdcar.adapter.ReservationMainTainAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.MainTainChildItem;
import com.xtrd.obdcar.entity.Reservation;
import com.xtrd.obdcar.maintain.CorrectLegendActivity;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.setting.MyReservationActivity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.TimeUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.view.DateDialog;
import com.xtrd.obdcar.view.MyGridView;
import com.xtrd.obdcar.view.MyListView;

public class ReservationActivity extends BaseActivity {

	private TextView text_name;
	private LinearLayout layout_distance;
	private TextView text_distance;
	private LinearLayout layout_time;
	private TextView text_time;
	private TextView text_price;
	private LinearLayout layout_choose_items;
	private MyGridView gridView;
	private MyListView listView;

	private static final int ID_CORRECT = 0;
	private ArrayList<MainTainChildItem> maintains = new ArrayList<MainTainChildItem>();

	private Reservation item = null;
	private ReservationMainTainAdapter adapter;
	private MainTainCheckdapter selectAdapter;
	private String selecttime = TimeUtils.getCurrentTime("yyyy-MM-dd HH:mm");
	private int merchantId;
	private String merchantName;
	private double priceValue=0;


	public ReservationActivity() {
		layout_id = R.layout.activity_reservation;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg,
				R.string.title_reservation, R.string.btn_apply,0);
		merchantId = getIntent().getIntExtra(ParamsKey.MERCHANTID,0);
		merchantName = getIntent().getStringExtra("merchantName");
		initView();
		regClick();
		text_name.setText(merchantName);
		text_time.setText(String.format(getResources().getString(R.string.text_order_time), selecttime));

		getReservations();
	}


	private void initView() {
		text_name = (TextView)findViewById(R.id.text_name);
		layout_distance = (LinearLayout)findViewById(R.id.layout_distance);
		text_distance = (TextView)findViewById(R.id.text_distance);
		layout_time = (LinearLayout)findViewById(R.id.layout_time);
		text_price = (TextView)findViewById(R.id.text_price);
		text_time = (TextView)findViewById(R.id.text_time);
		layout_choose_items = (LinearLayout)findViewById(R.id.layout_choose_items);
		gridView = (MyGridView)findViewById(R.id.gridView);
		listView = (MyListView)findViewById(R.id.listView);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_right:
			orderMerchant();
			break;
		case R.id.layout_time:
			showDatePicker();
			break;
		case R.id.layout_distance:
			Intent intent = new Intent(this,CorrectLegendActivity.class);
			intent.putExtra("title", "里程校准");
			intent.putExtra("unit", "公里");
			startActivityForResult(intent, ID_CORRECT);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(RESULT_OK==resultCode) {
			switch (requestCode) {
			case ID_CORRECT:
				if(data!=null) {
					String input =	data.getStringExtra("input");
					text_distance.setText(String.format(getResources().getString(R.string.text_current_distance),input));
				}
				break;

			default:
				break;
			}
		}
	}
	private void showDatePicker() {
		new DateDialog(this,new Date(System.currentTimeMillis()),true)
		.setPositiveButton(new DateDialog.OnClickListener() {

			@Override
			public void onClick(String time) {
				if(TimeUtils.compare(TimeUtils.getCurrentTime("yyyy-MM-dd HH:mm"),time,"yyyy-MM-dd HH:mm")) {
					selecttime = time;
					text_time.setText(String.format(getResources().getString(R.string.text_order_time), selecttime));
				}else {
					Utils.showToast(ReservationActivity.this, "请设置正确的预约时间");
				}
			}
		}).show();
	}

	private void regClick() {
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(item!=null) {
					item.getList().get(position).setChecked(!item.getList().get(position).isChecked());
					if(adapter!=null) {
						adapter.notifyDataSetChanged();
					}
					if(item.getList().get(position).isChecked()) {
						priceValue+=item.getList().get(position).getPrice();
						maintains.add(item.getList().get(position));
					}else {
						for(int i=maintains.size()-1;i>=0;i--) {
							if(maintains.get(i).getId()==item.getList().get(position).getId()) {
								priceValue-=maintains.get(i).getPrice();
								maintains.remove(i);
							}
						}
					}
					updateSelectItems();
				}
			}
		});
		layout_distance.setOnClickListener(this);
		layout_time.setOnClickListener(this);

	}

	/**
	 * 更新已选择的项目
	 */
	protected void updateSelectItems() {
		if(maintains!=null&&maintains.size()>0) {
			layout_choose_items.setVisibility(View.VISIBLE);
			selectAdapter = new MainTainCheckdapter(this, maintains,new MainTainCheckdapter.CallBack() {

				@Override
				public void callback(MainTainChildItem info) {
					if(item!=null) {
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

		text_price.setText(priceValue+"元");
	}

	protected void updateMaintain(int id) {
		if(item!=null&&item.getList()!=null) {
			for(MainTainChildItem info : item.getList()) {
				if(id==info.getId()) {
					info.setChecked(false);
					priceValue-=info.getPrice();
					break;
				}
			}
		}
		text_price.setText(priceValue+"元");
	}

	private void getReservations() {
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, SettingLoader.getVehicleId(this));
		params.put(ParamsKey.MERCHANTID, merchantId+"");
		NetRequest.requestUrl(this, ApiConfig.getRequestUrl(ApiConfig.Order_Url), params, new NetRequest.NetCallBack() {

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
									item = new Reservation();
									item.parser(json);
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
		if(item!=null) {
			text_distance.setText(String.format(getResources().getString(R.string.text_current_distance), item.getDistance()+""));
			adapter = new ReservationMainTainAdapter(this, item.getList());
			listView.setAdapter(adapter);
		}
	}

	/**
	 * 立即预约
	 */
	private void orderMerchant() {
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, SettingLoader.getVehicleId(this));
		params.put(ParamsKey.MERCHANTID, merchantId+"");
		params.put(ParamsKey.MainTain_Items, getSelectItems());
		params.put(ParamsKey.Time, selecttime+":00");
		params.put(ParamsKey.Price, getPrice());
		NetRequest.requestUrl(this, ApiConfig.getRequestUrl(ApiConfig.Take_Order_Url), params, new NetRequest.NetCallBack() {

			@Override
			public void sucCallback(String str) {
				try {
					if(!StringUtils.isNullOrEmpty(str)) {
						JSONObject json = new JSONObject(str);
						if(json.has("status")) {
							int status = json.getInt("status");
							if(1==status) {
								Utils.showToast(ReservationActivity.this, getResources().getString(R.string.tips_order_suc));
								Intent intent = new Intent(ReservationActivity.this,MyReservationActivity.class);
								startActivity(intent);
								finish();
							}else{
								Utils.showToast(ReservationActivity	.this, getResources().getString(R.string.tips_order_fail));
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void failCallback(int errorNo, String strMsg) {
				Utils.showToast(ReservationActivity.this, getResources().getString(R.string.tips_order_fail));
			}
		});
	}

	private String getSelectItems() {
		String items = "";
		if(item!=null&&item.getList()!=null) {
			for(MainTainChildItem info : item.getList()) {
				if(info.isChecked()) {
					items+=info.getId()+",";
				}
			}
		}
		return items;
	}

	/**
	 * 获取总价
	 * @return
	 */
	private String getPrice() {
		int price = 0;
		if(item!=null&&item.getList()!=null) {
			for(MainTainChildItem info : item.getList()) {
				price+=info.getPrice();
			}
		}
		return price+"";
	}


}
