package com.xtrd.obdcar.active;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.XtrdApp;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.Config;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.view.ObdDialog;

public class BoxActiveTwoActivity extends BaseActivity {

	protected static final String TAG = "BoxActiveTwoActivity";
	private static final int ID_QRSCAN = 1;
	private ImageView active_tow_img;
	private View active_tow_view,active_three_view;
	private TextView active_tow_text;
	private TextView btn_qrcode,btn_manual_input;
	private LinearLayout layout_input,layout_qrcode;
	private EditText edit_snum;
	private EditText edit_active;
	private ImageView img_qrcode;
	private TextView btn_buy;

	public BoxActiveTwoActivity() {
		layout_id = R.layout.activity_active_two;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initTitle(0,R.drawable.btn_back_bg,R.string.title_active,R.string.btn_finish,0);
		initStatusView();
		initView();
		regClick();
	}

	private void regClick() {
		btn_qrcode.setOnClickListener(this);
		btn_manual_input.setOnClickListener(this);
		btn_buy.setOnClickListener(this);
		img_qrcode.setOnClickListener(this);
		edit_active.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if(4==s.length()||9==s.length()||14==s.length()) {//TODO 处理删除
					edit_active.setText(s+"-");
					edit_active.setSelection(edit_active.getText().length());
				}
			}
		});

		edit_active.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER
						&& KeyEvent.ACTION_DOWN == event.getAction()) {
					String snum = edit_snum.getText().toString();
					String activeCode = edit_active.getText().toString();
					activeBox(snum,activeCode);
					return true;
				}

				return false;
			}
		});
	}

	private void initStatusView() {
		active_tow_img = (ImageView)findViewById(R.id.active_tow_img);
		active_tow_view = (View)findViewById(R.id.active_tow_view);
		active_three_view = (View)findViewById(R.id.active_three_view);
		active_tow_text = (TextView)findViewById(R.id.active_tow_text);
		active_tow_img.setImageResource(R.drawable.ic_active_solid);
		active_tow_view.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
		active_three_view.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
		active_tow_text.setTextColor(getResources().getColor(R.color.top_bar_color));
	}

	private void initView() {
		btn_qrcode = (TextView)findViewById(R.id.btn_qrcode);
		btn_manual_input = (TextView)findViewById(R.id.btn_manual_input);

		//手动输入
		layout_input = (LinearLayout)findViewById(R.id.layout_input);
		edit_snum = (EditText)findViewById(R.id.edit_snum);
		edit_active = (EditText)findViewById(R.id.edit_active);
		btn_buy = (TextView)findViewById(R.id.btn_buy);
		//二维码
		layout_qrcode = (LinearLayout)findViewById(R.id.layout_qrcode);
		img_qrcode = (ImageView)findViewById(R.id.img_qrcode);
	}

	@Override
	public void onBackPressed() {
		showActiveDialog();
	}
	
	public void showActiveDialog() {
		ObdDialog dialog = new ObdDialog(this).setTitle("温馨提示")
				.setMessage("您确定放弃已输入数据？")
				.setPositiveButton(getResources().getString(R.string.btn_cancel), new ObdDialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					
					}
				})
				.setNegativeButton("确定", new ObdDialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
		if(!dialog.isShowing()) {
			dialog.show();
		}
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_left:
			onBackPressed();
			break;
		case R.id.btn_right:
			String snum = edit_snum.getText().toString();
			String activeCode = edit_active.getText().toString();
			activeBox(snum,activeCode);
			break;
		case R.id.btn_qrcode:
			btn_manual_input.setBackgroundColor(getResources().getColor(R.color.btn_qr_color));
			btn_qrcode.setBackgroundColor(getResources().getColor(R.color.transparent));
			layout_input.setVisibility(View.GONE);
			layout_qrcode.setVisibility(View.VISIBLE);
			break;
		case R.id.btn_manual_input:
			btn_qrcode.setBackgroundColor(getResources().getColor(R.color.btn_qr_color));
			btn_manual_input.setBackgroundColor(getResources().getColor(R.color.transparent));
			layout_qrcode.setVisibility(View.GONE);
			layout_input.setVisibility(View.VISIBLE);
			break;
		case R.id.img_qrcode:
			Intent intent = new Intent(this,QrScanActivity.class);
			startActivityForResult(intent,ID_QRSCAN);
			break;
		case R.id.btn_buy:
			Utils.showPhoneTips(this, Config.Hot_Line);
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
			case ID_QRSCAN:
				if(data!=null) {
					String result = data.getStringExtra("result");
					if(!StringUtils.isNullOrEmpty(result)) {
						String[] split = result.split(",");
						if(split.length==2) {
							activeBox(split[0],split[1]);
						}
					}
				}
				break;

			default:
				break;
			}
		}
	}

	/**
	 * 盒子激活
	 */
	private void activeBox(String snum,String activeCode) {
		if(StringUtils.isNullOrEmpty(snum)) {
			Utils.showToast(this, getResources().getString(R.string.text_snum_null));
			return;
		}
		if(StringUtils.isNullOrEmpty(activeCode)) {
			Utils.showToast(this, getResources().getString(R.string.text_active_null));
			return;
		}

		if(!Utils.isVilidActive(activeCode)||activeCode.length()!=19) {
			Utils.showToast(this, getResources().getString(R.string.text_active_invalid));
			return;
		}

		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.VEHICLEID, getIntent().getIntExtra("vehicleId",0)+"");
		params.put(ParamsKey.Active_SN, snum);
		params.put(ParamsKey.Active_Code, activeCode);
		params.put(ParamsKey.Active_Distance, getIntent().getStringExtra("distance"));
		NetRequest.requestUrl(this, ApiConfig.getRequestUrl(ApiConfig.Active_Bind_Box_Url), params, new NetRequest.NetCallBack() {

			@Override
			public void sucCallback(String str) {
				try {
					if(!StringUtils.isNullOrEmpty(str)) {
						JSONObject json = new JSONObject(str);
						if(json.has("status")) {
							int status = json.getInt("status");
							if(1==status) {
								Utils.showToast(BoxActiveTwoActivity.this, getResources().getString(R.string.tips_active_suc));
								XtrdApp.sendMsg(XtrdApp.ID_BIND_CAR);
								finish();
							}else{
								Utils.showToast(BoxActiveTwoActivity.this, getResources().getString(R.string.tips_active_fail));
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void failCallback(int errorNo, String strMsg) {
				Utils.showToast(BoxActiveTwoActivity.this, getResources().getString(R.string.tips_active_fail));
			}
		});
	}
}
