package com.xtrd.obdcar.vc;


import java.util.ArrayList;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.XtrdApp;
import com.xtrd.obdcar.adapter.ChatAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.MessageOpenHelper;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.EMMessage;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.passport.LoginActivity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
/**
 * 专家问答
 * @author Administrator
 *
 */
public class ProfessorQAActivity extends BaseActivity {
	
	private Button btn_ask,btn_send;
	private EditText edit_input;
	private ListView listView;
	private ArrayList<EMMessage> list = new ArrayList<EMMessage>();
	private ChatAdapter adapter;

	public ProfessorQAActivity() {
		layout_id = R.layout.activity_progessor_qa;
	}
	
	
	public Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case XtrdApp.ID_RECEIVE_QA_MSG:
				list.add(MessageOpenHelper.getInstance(ProfessorQAActivity.this).getReceiveMsg());
				updateUI();
				break;
			default:
				break;
			}
		}

	};

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg,R.string.title_experts, 0,0);
		XtrdApp.addHandler(handler);
		initView();
		ArrayList<EMMessage> messages = MessageOpenHelper.getInstance(this).getMessages();
		list.addAll(messages);
		updateUI();
		if(!SettingLoader.hasLogin(this)) {
			Intent intent = new Intent(this,LoginActivity.class);
			startActivityForResult(intent, 1);
		}
	}

	private void initView() {
		listView = (ListView)findViewById(R.id.listView);
		edit_input = (EditText)findViewById(R.id.edit_input);
		btn_ask = (Button)findViewById(R.id.btn_ask);
		btn_send = (Button)findViewById(R.id.btn_send);
		btn_ask.setOnClickListener(this);
		btn_send.setOnClickListener(this);
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(RESULT_OK==resultCode) {
			switch (requestCode) {
			case 1:
				finish();
				break;
				
			default:
				break;
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_ask:
			
			break;
		case R.id.btn_send:
			if(SettingLoader.hasLogin(this)) {
				sendQaContent();
			}else {
				Intent intent = new Intent(this,LoginActivity.class);
				startActivity(intent);
			}
			break;

		default:
			break;
		}
	}

	private void sendQaContent() {
		final String content = edit_input.getText().toString();
		if(StringUtils.isNullOrEmpty(content)) {
			Utils.showToast(this, getResources().getString(R.string.tips_no_input));
			return;
		}
		
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.Content, content);
		NetRequest.requestUrl(this, ApiConfig.getRequestUrl(ApiConfig.QA_Url), params, new NetRequest.NetCallBack() {

			private String msg;

			@Override
			public void sucCallback(String str) {
				try {
					if(!StringUtils.isNullOrEmpty(str)) {
						JSONObject json = new JSONObject(str);
						if(json.has("status")) {
							int status = json.getInt("status");
							if(1==status) {
								Utils.showToast(ProfessorQAActivity.this, getResources().getString(R.string.tips_send_suc));
								EMMessage message = new EMMessage(SettingLoader.getBranchId(ProfessorQAActivity.this),content,System.currentTimeMillis());
								list.add(message);
								edit_input.setText("");
								MessageOpenHelper.getInstance(ProfessorQAActivity.this).insertItem(message);
								updateUI();
							}else {
								msg = json.getString("message");
								Utils.showToast(ProfessorQAActivity.this, msg);
							}
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
		if(list!=null&&list.size()>0) {
			if(adapter==null) {
				adapter = new ChatAdapter(this, list);
				listView.setAdapter(adapter);
			}else {
				adapter.notifyDataSetChanged();
			}
		}
		listView.setSelection(list.size()-1);
	}
	
	
}
