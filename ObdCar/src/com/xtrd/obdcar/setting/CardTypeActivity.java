package com.xtrd.obdcar.setting;


import java.util.ArrayList;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.http.PreferencesCookieStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.entity.CardType;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;

public class CardTypeActivity extends BaseActivity {
	private ListView listView;
	private ArrayList<CardType> list = null;

	public CardTypeActivity() {
		layout_id = R.layout.activity_card_type;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg, R.string.title_identity_type, 0, 0);
		initView();
		getCardType();
	}

	private void initView() {
		listView = (ListView)findViewById(R.id.listView);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				intent.putExtra("code", list.get(position).getCode());
				intent.putExtra("value", list.get(position).getValue());
				setResult(RESULT_OK, intent);
				finish();
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
			
			break;

		default:
			break;
		}
	}
	
	private void updateUI() {
		if(listView.getAdapter()==null) {
			ListAdapter adapter = new ListAdapter();
			listView.setAdapter(adapter);
		}else {
			((ListAdapter)listView.getAdapter()).notifyDataSetChanged();
		}
	}

	
	final class ListAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return list!=null?list.size():0;
		}

		@Override
		public CardType getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(CardTypeActivity.this).inflate(R.layout.layout_textview, null);
				viewHolder.text_title = (TextView) convertView.findViewById(R.id.layout_textview);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.text_title.setText(getItem(position).getValue());
			return convertView;
		}
		
		class ViewHolder {
			public TextView text_title;
		}
	}
	
	private void getCardType() {
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.ID_KEY, ParamsKey.ID_TYPE);
		fh.post(ApiConfig.getRequestUrl(ApiConfig.IdCard_Url),params, new AjaxCallBack<String>(){

			@Override
			public void onStart() {
				super.onStart();
				showLoading();
			}
			
			@Override
			public void onSuccess(String t) {
				dismissLoading();
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						if("<".equals(String.valueOf(t.charAt(0)))) {
							showLoginDialog();
						}else {
							JSONObject json = new JSONObject(t);
							if(json.has("status")) {
								int status = json.getInt("status");
								if(1==status) {
									if(json.has("result")) {
										JSONArray array = json.getJSONArray("result");
										if(array!=null&&array.length()>0) {
											if(list==null) {
												list = new ArrayList<CardType>();
											}
											CardType card = null;
											for(int i=0;i<array.length();i++) {
												card = new CardType();
												card.parser(array.optJSONObject(i));
												list.add(card);
											}
										}
									}
								}
							}
							updateUI();
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
				dismissLoading();
				Utils.showToast(CardTypeActivity.this, R.string.data_load_fail);
			}
		});
		
	}


}
