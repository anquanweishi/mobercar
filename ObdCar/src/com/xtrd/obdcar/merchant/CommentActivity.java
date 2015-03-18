package com.xtrd.obdcar.merchant;

import java.util.ArrayList;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;

import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.adapter.CommentAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.entity.MComment;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;

public class CommentActivity extends BaseActivity {

	private ListView listView;
	private ArrayList<MComment> list = new ArrayList<MComment>();
	private CommentAdapter adapter;
	private TextView tips_text;
	protected boolean hasMore = true;

	public CommentActivity() {
		layout_id = R.layout.activity_comment;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg,R.string.title_comment, 0,0);
		initView();
		getComments();
	}

	

	private void initView() {
		tips_text = (TextView)findViewById(R.id.tips_text);
		listView = (ListView) findViewById(R.id.listView);
		listView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() == view.getCount() - 1) {
						if(hasMore ) {
							getComments();
						}else {
							Utils.showToast(CommentActivity.this, R.string.has_no_more_data);
						}
					}
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
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

	private void getComments() {
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.MERCHANTID, getIntent().getIntExtra(ParamsKey.MERCHANTID, 0)+"");
		if(list.size()>0) {
			params.put(ParamsKey.MinTime, list.get(list.size()-1).getCreateTime());
		}
		NetRequest.requestUrl(this,ApiConfig.getRequestUrl(ApiConfig.Comment_Url), params,
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
										MComment comment = null;
										for(int i=0;i<array.length();i++) {
											comment = new MComment();
											comment.parser(array.optJSONObject(i));
											list.add(comment);
										}
										hasMore = true;
									}else {
										hasMore = false;
									}
								}else {
									hasMore = false;
								}
							} else {
								hasMore = false;
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
		if (list != null && list.size() > 0) {
			tips_text.setVisibility(View.GONE);
			if (listView.getAdapter() == null) {
				adapter = new CommentAdapter(this, list);
				listView.setAdapter(adapter);
			} else {
				adapter.notifyDataSetChanged();
			}
		} else {
			tips_text.setVisibility(View.VISIBLE);
			tips_text.setText(getResources().getString(
					R.string.tips_no_comment));
		}
		
	}
}
