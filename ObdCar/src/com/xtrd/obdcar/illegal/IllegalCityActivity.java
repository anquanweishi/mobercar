package com.xtrd.obdcar.illegal;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.http.PreferencesCookieStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.adapter.ViolationProvinceAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.entity.ObdLocation;
import com.xtrd.obdcar.entity.ViolationCity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.NetUtils;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.utils.log.LogUtils;
import com.xtrd.obdcar.view.ObdDialog;
import com.xtrd.obdcar.view.SideBar;
import com.xtrd.obdcar.view.SideBar.OnTouchingLetterChangedListener;
import com.xtrd.obdcar.view.fragment.ViolationCityFragment;
import com.xtrd.obdcar.view.slidmenu.SlidingMenu;

public class IllegalCityActivity extends BaseActivity {
	protected static final String TAG = "IllegalCityActivity";
	private TextView choose_text;
	private ListView listView;

	private ArrayList<ViolationCity> list = new ArrayList<ViolationCity>();
	private SideBar mSideBar;
	private TextView mInfo;
	private ViolationProvinceAdapter adapter;
	private LinearLayout mainlayout;
	private SlidingMenu mSlidingMenu;
	private View contentView;
	private ViolationCityFragment mFrag;

	public IllegalCityActivity() {
		layout_id = R.layout.activity_illegal_city;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg,R.string.title_address, 0,0);

		initView();
		regListener();
		if(!NetUtils.IsNetworkAvailable()) {
			Utils.showNetTips(this);
			return;
		}
		
		getViolationCity();

	}

	private void initView() {
		mainlayout = (LinearLayout)findViewById(R.id.mainlayout);
		choose_text = (TextView)findViewById(R.id.choose_text);
		
		mSlidingMenu = new SlidingMenu(this);
		mainlayout.addView(mSlidingMenu);

		initSlideMenu();
	}

	private void initSlideMenu() {
		View menu = getLayoutInflater().inflate(R.layout.layout_plate_left, null);
		contentView = getLayoutInflater().inflate(R.layout.layout_car_model_content, null);
		RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		contentView.setLayoutParams(params);
		
		listView = (ListView)contentView.findViewById(R.id.listView);
		mSideBar = (SideBar) contentView.findViewById(R.id.customer_sidebar);
		mInfo = (TextView) contentView.findViewById(R.id.show_customer_info_tv);
		mSideBar.setTextView(mInfo);

		//======自定义组件中加入两个布局=======================
		mSlidingMenu.setMenu(menu);
		mSlidingMenu.setContent(contentView);
		mFrag = new ViolationCityFragment();
		mFrag.setSlideMenu(mSlidingMenu,this);
		mFrag.initView(menu,this);
	}

	private void regListener() {
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				ViolationCity location = list.get(arg2);
				mFrag.setData(choose_text,location);
				if(mSlidingMenu.isDisplay()) {
					mSlidingMenu.showMenu();
				}
			} 
		});
		
		//设置右侧触摸监听
		mSideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				//该字母首次出现的位置
				if(adapter!=null) {
					int position = adapter.getPositionForSection(s.charAt(0));
					if(position != -1){
						listView.setSelection(position);
					}
				}
			}
		});
	}
	
	protected void showChooseDialog(final ObdLocation location) {
		ObdDialog obdDialog = new ObdDialog(this)
		.setTitle("提示信息")
		.setMessage("确定选择   " + location.getDisplayName())
		.setPositiveButton("重选", new ObdDialog.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				choose_text.setText("");
				((ObdDialog) dialog).updateMessage(location.getDisplayName());
			}
		})
		.setNegativeButton("确定", new ObdDialog.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				choose_text.setText(location.getDisplayName());
				Intent intent = new Intent();
				intent.putExtra("areaCode", location.getAreaCode());
				intent.putExtra("value", choose_text.getText().toString());
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
		obdDialog.show();
		
	}

	private void getViolationCity() {
		FinalHttp fh = new FinalHttp();
		PreferencesCookieStore store = new PreferencesCookieStore(this);
		fh.configCookieStore(store);
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.Province, "");
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Violation_City_Url),params, new AjaxCallBack<String>(){
			private int status = 0;

			@Override
			public void onStart() {
				showLoading();
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				dismissLoading();
				LogUtils.e(TAG, t);
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						if("<".equals(String.valueOf(t.charAt(0)))) {
							showLoginDialog();
						}else {
							JSONObject json = new JSONObject(t);
							if(json.has("status")) {
								status  = json.getInt("status");
								if(1==status) {
									JSONArray jsonArray = json.getJSONArray("result");
									if(jsonArray!=null&&jsonArray.length()>0) {
										list.clear();
										ViolationCity loc = null;
										for(int i=0;i<jsonArray.length();i++) {
											loc = new ViolationCity();
											loc.parser((JSONObject)jsonArray.opt(i));
											list.add(loc);
										}
										//排序
										Collections.sort(list, new Comparator<ViolationCity>() {
											
											@Override
											public int compare(ViolationCity lhs, ViolationCity rhs) {
												return lhs.getSortLetters().compareTo(rhs.getSortLetters());
											}
										});
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
				dismissLoading();
				updateUI();
				super.onFailure(t, errorNo, strMsg);
			}

		});

	}

	protected void updateUI() {
		if(list==null||list.size()==0) {
			Utils.showToast(this, getResources().getString(R.string.tips_no_data));
		}else {
			if(adapter==null) {
				adapter = new ViolationProvinceAdapter(this,list);
				listView.setAdapter(adapter);
			}else {
				adapter.notifyDataSetChanged();
			}
		}
	}

	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		default:
			break;
		}
		super.onClick(v);
	}
}
