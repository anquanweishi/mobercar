package com.xtrd.obdcar.vc;


import java.util.ArrayList;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.adapter.TroubleCodeAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.entity.VCondition;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;

public class TroubleCodeActivity extends BaseActivity {

	protected static final String TAG = "TroubleCodeActivity";
	private TextView tips_text;
	private EditText edit_input;
	private Button btn_search;
	private ListView listView;
	private ArrayList<VCondition> list = new ArrayList<VCondition>();
	private TroubleCodeAdapter adapter;
	private ImageView btn_clear;

	public TroubleCodeActivity() {
		layout_id = R.layout.activity_trouble_code;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg,R.string.title_trouble_code, 0, 0);
		initView();
		if(getIntent().getStringExtra(ParamsKey.Code)!=null) {
			edit_input.setText(getIntent().getStringExtra(ParamsKey.Code));
		}
	}

	private void initView() {
		tips_text = (TextView)findViewById(R.id.tips_text);
		edit_input = (EditText)findViewById(R.id.edit_input);
		btn_search = (Button)findViewById(R.id.btn_search);
		listView = (ListView)findViewById(R.id.listView);
		btn_search.setOnClickListener(this);
		
		btn_clear = (ImageView)findViewById(R.id.btn_clear);
		btn_clear.setOnClickListener(this);

		edit_input.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if(s.length()>0) {
					btn_clear.setVisibility(View.VISIBLE);
				}else {
					btn_clear.setVisibility(View.INVISIBLE);
				}
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				VCondition item = list.get(position);
				Intent intent = new Intent(TroubleCodeActivity.this,TroubleCodeDetailActivity.class);
				intent.putExtra("code",item.getCode());
				intent.putExtra("name", item.getName());
				intent.putExtra("desc", item.getDescription());
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
		case R.id.btn_search:
			searchTrouble();
			break;
		case R.id.btn_clear:
			edit_input.setText("");
			break;
		default:
			break;
		}
	}

	

	private void searchTrouble() {
		String code = edit_input.getText().toString();
		if(StringUtils.isNullOrEmpty(code)) {
			Utils.showToast(this, "请输入故障码");
			return;
		}
		
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.Code, code);
		NetRequest.requestUrl(this, ApiConfig.getRequestUrl(ApiConfig.Fault_Search_Url), params,new NetRequest.NetCallBack() {
			
			private String msg;

			@Override
			public void sucCallback(String str) {
				try {
					JSONObject json = new JSONObject(str);
					if(json.has("status")) {
						int status = json.getInt("status");
						if(1==status) {
							list.clear();
							if(json.has("result")) {
								JSONArray array = json.getJSONArray("result");
								if(array!=null&&array.length()>0) {
									VCondition item = null;
									for(int i=0;i<array.length();i++) {
										item = new VCondition();
										item.parser((JSONObject)array.opt(i));
										list.add(item);
									}
								}
							}
						}else {
							msg = json.getString("message");
							Utils.showToast(TroubleCodeActivity.this, msg);
						}
					}
					updateUI();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void failCallback(int errorNo, String strMsg) {
				Utils.showToast(TroubleCodeActivity.this,getResources().getString(R.string.data_load_fail));
			}
		});
	}

	protected void updateUI() {
		if(list==null||list.size()==0) {
			tips_text.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
			tips_text.setText(getResources().getString(R.string.tips_no_data));
		}else {
			tips_text.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
			if(listView.getAdapter()==null) {
				adapter = new TroubleCodeAdapter(this, list);
				listView.setAdapter(adapter);
			}else {
				adapter.notifyDataSetChanged();
			}
		}
	}

}
