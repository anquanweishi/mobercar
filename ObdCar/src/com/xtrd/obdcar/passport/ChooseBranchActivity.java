package com.xtrd.obdcar.passport;

import java.util.ArrayList;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import com.xtrd.obdcar.adapter.BranchAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.db.BranchOpenHelper;
import com.xtrd.obdcar.entity.CarBranch;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.NetUtils;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.utils.log.LogUtils;
import com.xtrd.obdcar.view.SideBar;
import com.xtrd.obdcar.view.SideBar.OnTouchingLetterChangedListener;
import com.xtrd.obdcar.view.fragment.LeftFragment;
import com.xtrd.obdcar.view.slidmenu.SlidingMenu;

public class ChooseBranchActivity extends BaseActivity {

	protected static final String TAG = "ChooseBranchActivity";
	private TextView choose_text;
	private ListView listView;

	private ArrayList<CarBranch> list = new ArrayList<CarBranch>();
	private SideBar mSideBar;
	private TextView mInfo;
	private BranchAdapter adapter;
	private LinearLayout mainlayout;
	private SlidingMenu mSlidingMenu;
	private View contentView;
	private LeftFragment mFrag;

	public ChooseBranchActivity() {
		layout_id = R.layout.activity_branch;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		boolean from = getIntent().getBooleanExtra("from", false);
		if(from) {
			initTitle(0, R.drawable.btn_back_bg,
					R.string.title_car_model, 0,0);

		}else {
			initTitle(0, R.drawable.btn_back_bg,
					R.string.title_car_model, 0,0);
		}
		initView();
		regListener();
		if(!NetUtils.IsNetworkAvailable()) {
			Utils.showNetTips(this);
			return;
		}
		ArrayList<CarBranch> branchs = BranchOpenHelper.getInstance(this).getLocalBranchs();
		if(branchs!=null&&branchs.size()>0) {
			list.addAll(branchs);
			updateUI();
		}else {
			getBranch();
		}

	}

	private void initView() {
		mainlayout = (LinearLayout)findViewById(R.id.mainlayout);
		choose_text = (TextView)findViewById(R.id.choose_text);
		
		mSlidingMenu = new SlidingMenu(this);
		mainlayout.addView(mSlidingMenu);

		initSlideMenu();
	}

	private void initSlideMenu() {
		View menu = getLayoutInflater().inflate(R.layout.layout_slide_left, null);
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
		mFrag = new LeftFragment();
		mFrag.setSlideMenu(mSlidingMenu,this);
		mFrag.initView(menu,this);
	}

	private void regListener() {
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				mFrag.setData(choose_text,list.get(arg2));
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
	

	private void getBranch() {
		FinalHttp fh = new FinalHttp();
		fh.get(ApiConfig.getRequestUrl(ApiConfig.Car_Branch_Url), new AjaxCallBack<String>(){
			private int status = 0;

			@Override
			public void onStart() {
				showLoading();
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				dismissLoading();
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						JSONObject json = new JSONObject(t);
						LogUtils.e(TAG, "t " + t);
						if(json.has("status")) {
							status  = json.getInt("status");
							if(1==status) {
								JSONArray jsonArray = json.getJSONArray("result");
								if(jsonArray!=null&&jsonArray.length()>0) {
									list.clear();
									CarBranch branch = null;
									for(int i=0;i<jsonArray.length();i++) {
										branch = new CarBranch();
										branch.parser((JSONObject)jsonArray.opt(i));
										list.add(branch);
									}
								}
								BranchOpenHelper.getInstance(ChooseBranchActivity.this).batchInfos(list);
							}
						}
						updateUI();
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
				adapter = new BranchAdapter(this,list);
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
