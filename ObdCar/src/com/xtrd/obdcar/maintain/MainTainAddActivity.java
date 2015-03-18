package com.xtrd.obdcar.maintain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.TimeUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.view.DateDialog;
import com.xtrd.obdcar.view.MMAlert;

public class MainTainAddActivity extends BaseActivity {
	private static final int ID_RANGE = 0;
	private static final int ID_COST = 1;
	private static final int ID_ITEM = 2;
	private static final int ID_REMARK = 3;
	private LinearLayout layout_maintain_range;
	private LinearLayout layout_maintain_time;
	private LinearLayout layout_maintain_cost;
	private LinearLayout layout_maintain_item;
	private LinearLayout layout_maintain_type;
	private LinearLayout layout_maintain_remark;


	private String range,currentTime,cost,items,type,remark;



	public MainTainAddActivity() {
		layout_id = R.layout.activity_maintain_add;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg, R.string.title_maintain_add, R.string.btn_finish,0);
		initView();
		regClick();
	}

	private void regClick() {
		layout_maintain_range.setOnClickListener(this);
		layout_maintain_time.setOnClickListener(this);
		layout_maintain_cost.setOnClickListener(this);
		layout_maintain_item.setOnClickListener(this);
		layout_maintain_type.setOnClickListener(this);
		layout_maintain_remark.setOnClickListener(this);
	}

	private void initView() {
		layout_maintain_range = (LinearLayout)findViewById(R.id.layout_maintain_range);
		layout_maintain_time = (LinearLayout)findViewById(R.id.layout_maintain_time);
		layout_maintain_cost = (LinearLayout)findViewById(R.id.layout_maintain_cost);
		layout_maintain_item = (LinearLayout)findViewById(R.id.layout_maintain_item);
		layout_maintain_type = (LinearLayout)findViewById(R.id.layout_maintain_type);
		layout_maintain_remark = (LinearLayout)findViewById(R.id.layout_maintain_remark);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_right:
			addMainTainRecoder();
			break;
		case R.id.layout_maintain_range:
			intent = new Intent(this,CorrectLegendActivity.class);
			intent.putExtra("title", "里程校准");
			intent.putExtra("unit", "公里");
			startActivityForResult(intent, ID_RANGE);
			break;
		case R.id.layout_maintain_time:
			showDatePicker();
			break;
		case R.id.layout_maintain_cost:
			intent = new Intent(this,CorrectLegendActivity.class);
			intent.putExtra("title", "保养费用");
			intent.putExtra("unit", "元");
			startActivityForResult(intent, ID_COST);
			break;
		case R.id.layout_maintain_item:
			intent = new Intent(this,MainTainCaseActivity.class);
			intent.putExtra("items", items);
			startActivityForResult(intent, ID_ITEM);
			break;
		case R.id.layout_maintain_type:
			showTypeChoose();
			break;
		case R.id.layout_maintain_remark:
			intent = new Intent(this,CorrectLegendActivity.class);
			intent.putExtra("title", "备注");
			intent.putExtra("type", true);
			startActivityForResult(intent, ID_REMARK);
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
			case ID_RANGE:
				if(data!=null) {
					range = data.getStringExtra("input");
					((TextView)layout_maintain_range.getChildAt(1)).setText(range+"公里");
				}
				break;
			case ID_COST:
				if(data!=null) {
					cost = data.getStringExtra("input");
					((TextView)layout_maintain_cost.getChildAt(1)).setText(cost+"元");
				}
				break;
			case ID_ITEM:
				if(data!=null) {
					items = data.getStringExtra("items");
					String value = data.getStringExtra("value");
					((TextView)layout_maintain_item.getChildAt(1)).setText(value);
				}
				break;
			case ID_REMARK:
				if(data!=null) {
					remark = data.getStringExtra("input");
					((TextView)layout_maintain_remark.getChildAt(1)).setText(remark);
				}
				break;

			default:
				break;
			}
		}
	}


	private void showTypeChoose() {
		final String[] datas = new String[]{"常规保养","首次保养","二次保养","三次保养"};
		MMAlert.showAlert(this, "保养类型", datas, null, new MMAlert.OnAlertSelectId() {

			@Override
			public void onClick(int whichButton) {
				if(whichButton<datas.length) {
					type = whichButton+"";
					((TextView)layout_maintain_type.getChildAt(1)).setText(datas[whichButton]);
				}
			}
		});
	}

	private void showDatePicker() {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date parse = format.parse(TimeUtils.getCurrentTime());
			new DateDialog(this,parse)
			.setPositiveButton(new DateDialog.OnClickListener() {

				@Override
				public void onClick(String time) {
					if(TimeUtils.compare(TimeUtils.getCurrentTime(), time, "yyyy-MM-dd")) {
						Utils.showToast(MainTainAddActivity.this, getResources().getString(R.string.tips_maintain_add));
						return;
					}
					currentTime = time;
					((TextView)layout_maintain_time.getChildAt(1)).setText(time);
				}
			}).show();
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	private void addMainTainRecoder() {
		if(StringUtils.isNullOrEmpty(range)) {
			Utils.showToast(this, "请输入里程");
			return;
		}
		if(StringUtils.isNullOrEmpty(cost)) {
			Utils.showToast(this, "请输入费用");
			return;
		}
		if(StringUtils.isNullOrEmpty(range)) {
			Utils.showToast(this, "请输入里程");
			return;
		}
		if(StringUtils.isNullOrEmpty(currentTime)) {
			Utils.showToast(this, "请选择保养时间");
			return;
		}
		if(StringUtils.isNullOrEmpty(items)) {
			Utils.showToast(this, "请选择保养项");
			return;
		}
		if(StringUtils.isNullOrEmpty(type)) {
			Utils.showToast(this, "请选择保养类型");
			return;
		}
		
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.MERCHANTID, "");
		params.put(ParamsKey.VEHICLEID, SettingLoader.getVehicleId(this));
		params.put(ParamsKey.Active_Distance,range);
		params.put(ParamsKey.Price, cost);
		params.put(ParamsKey.LastTime, currentTime+" 00:00:00");
		params.put(ParamsKey.MainTain_Items, items);
		params.put(ParamsKey.TYPE, type);
		params.put(ParamsKey.Remark, remark);
		NetRequest.requestUrl(this,
				ApiConfig.getRequestUrl(ApiConfig.Maintain_Add_Recoders_Url), params,
				new NetRequest.NetCallBack() {

			@Override
			public void sucCallback(String str) {
				try {
					if (!StringUtils.isNullOrEmpty(str)) {
						JSONObject json = new JSONObject(str);
						if (json.has("status")) {
							int status = json.getInt("status");
							if (1 == status) {
								Utils.showToast(MainTainAddActivity.this, "添加成功");
								finish();
							} else {
								if(json.has("message")) {
									String msg = json.getString("message");
									Utils.showToast(MainTainAddActivity.this, msg);
								}
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void failCallback(int errorNo, String strMsg) {
				Utils.showToast(MainTainAddActivity.this, "添加失败");
			}
		});
	}

}
