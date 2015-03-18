package com.xtrd.obdcar.view.fragment;

import java.util.ArrayList;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xtrd.obdcar.adapter.SeriesAdapter;
import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.db.BranchOpenHelper;
import com.xtrd.obdcar.entity.CarBranch;
import com.xtrd.obdcar.entity.CarModel;
import com.xtrd.obdcar.entity.CarSeries;
import com.xtrd.obdcar.passport.ChooseBranchActivity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.log.LogUtils;
import com.xtrd.obdcar.view.ObdDialog;
import com.xtrd.obdcar.view.slidmenu.SlidingMenu;

public class LeftFragment {

	protected static final String TAG = "LeftFragment";
	private SlidingMenu mSlidingMenu;
	private FrameLayout layout_left;
	private ExpandableListView expandlist;
	private ChooseBranchActivity activity;
	private ArrayList<CarSeries> list = new ArrayList<CarSeries>();
	private SeriesAdapter adapter;
	private CarBranch branch;
	private TextView choose_text;

	public void setSlideMenu(SlidingMenu mSlidingMenu, ChooseBranchActivity activity) {
		this.mSlidingMenu = mSlidingMenu;
		this.activity = activity;
	}

	public void initView(View menu, Context context) {
		layout_left = (FrameLayout)menu.findViewById(R.id.layout_left);
		expandlist = (ExpandableListView)menu.findViewById(R.id.expandlist);
		expandlist.setGroupIndicator(null);
		adapter = new SeriesAdapter(activity,list);
		expandlist.setAdapter(adapter);

		expandlist.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				CarSeries carSeries = list.get(groupPosition);
				if(0==carSeries.getHasChildren()) {
					showChooseDialog(carSeries, null);
				}else {
					choose_text.setText(branch.getBranch()+" " +carSeries.getName());
					ArrayList<CarModel> models = BranchOpenHelper.getInstance(activity).getLocalModel(carSeries.getSeriesId());
					if(models!=null&&models.size()>0) {
						carSeries.getList().clear();
						carSeries.getList().addAll(models);
						updateUI();
					}else {
						getModel(carSeries);
					}
				}
				return false;
			}
		});

		expandlist.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				CarModel model = list.get(groupPosition).getList().get(childPosition);
				choose_text.setText(branch.getBranch()+" " + list.get(groupPosition).getName() +" " +model.getName());
				showChooseDialog(list.get(groupPosition),model);
				return false;
			}
		});
	}



	protected void showChooseDialog(final CarSeries series, final CarModel model) {
		ObdDialog obdDialog = new ObdDialog(activity)
		.setTitle("提示信息")
		.setMessage("确定选择   " + branch.getBranch()+ " " + series.getName() + " " + (model!=null?model.getName():""))
		.setPositiveButton("重选", new ObdDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				choose_text.setText(branch.getBranch()+" " +series.getName());
			}
		})
		.setNegativeButton("确定", new ObdDialog.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				choose_text.setText(branch.getBranch()+" " +series.getName() + " " +(model!=null?model.getName():""));
				Intent intent = new Intent();
				intent.putExtra("modelId", (model!=null?model.getModelId():series.getSeriesId()));
				intent.putExtra("value", choose_text.getText().toString());
				intent.putExtra("support",(model!=null?model.getSupport():0));
				activity.setResult(Activity.RESULT_OK, intent);
				activity.finish();
			}
		});
		obdDialog.show();

	}

	public void setData(TextView choose_text, CarBranch branch) {
		this.choose_text = choose_text;
		choose_text.setText(branch.getBranch());
		this.branch = branch;
		ArrayList<CarSeries> series = BranchOpenHelper.getInstance(activity).getLocalSeries(branch.getBranchId());
		if(series!=null&&series.size()>0) {
			list.clear();
			list.addAll(series);
			updateUI();
		}else {
			getSeriesById(branch.getBranchId());
		}

	}

	private void getSeriesById(int branchId) {
		FinalHttp fh = new FinalHttp();
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.Car_Branch, branchId+"");
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Car_Series_Url),params, new AjaxCallBack<String>(){
			private int status = 0;

			@Override
			public void onStart() {
				activity.showLoading();
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				activity.dismissLoading();
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						LogUtils.e(TAG, "t " +t);
						JSONObject json = new JSONObject(t);
						if(json.has("status")) {
							status  = json.getInt("status");
							if(1==status) {
								JSONArray jsonArray = json.getJSONArray("result");
								if(jsonArray!=null&&jsonArray.length()>0) {
									list.clear();
									CarSeries series = null;
									for(int i=0;i<jsonArray.length();i++) {
										series = new CarSeries();
										series.parser((JSONObject)jsonArray.opt(i));
										list.add(series);
									}
								}
								new AsyncTask<Void, Integer, Void>(){

									@Override
									protected Void doInBackground(
											Void... params) {
										BranchOpenHelper.getInstance(activity).batchSeriesInfos(list);
										return null;
									}}.execute();
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
				LogUtils.e(TAG,"errorNo " + errorNo + "strMsg " + strMsg);
				activity.dismissLoading();
				super.onFailure(t, errorNo, strMsg);
			}

		});
	}


	protected void updateUI() {
		if(adapter==null) {
			adapter = new SeriesAdapter(activity,list);
			expandlist.setAdapter(adapter);
		}else {
			adapter.notifyDataSetChanged();
		}

	}


	protected void getModel(final CarSeries carSeries) {
		FinalHttp fh = new FinalHttp();
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.Car_Series, carSeries.getSeriesId()+"");
		fh.post(ApiConfig.getRequestUrl(ApiConfig.Car_Model_Url),params, new AjaxCallBack<String>(){
			private int status = 0;

			@Override
			public void onStart() {
				activity.showLoading();
				super.onStart();
			}

			@Override
			public void onSuccess(String t) {
				activity.dismissLoading();
				try {
					if(!StringUtils.isNullOrEmpty(t)) {
						LogUtils.e(TAG, "t "+t);
						JSONObject json = new JSONObject(t);
						if(json.has("status")) {
							status  = json.getInt("status");
							if(1==status) {
								JSONArray jsonArray = json.getJSONArray("result");
								if(jsonArray!=null&&jsonArray.length()>0) {
									CarModel model = null;
									carSeries.getList().clear();
									for(int i=0;i<jsonArray.length();i++) {
										model = new CarModel();
										model.parser((JSONObject)jsonArray.opt(i));
										carSeries.getList().add(model);
									}
								}
								new AsyncTask<Void, Integer, Void>(){

									@Override
									protected Void doInBackground(
											Void... params) {
										BranchOpenHelper.getInstance(activity).batchModelInfos(carSeries.getList());
										return null;
									}}.execute();
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
				activity.dismissLoading();
				super.onFailure(t, errorNo, strMsg);
			}

		});
	}

}
