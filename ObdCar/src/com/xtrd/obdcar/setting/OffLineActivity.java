package com.xtrd.obdcar.setting;

import java.util.ArrayList;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.xtrd.obdcar.BaseActivity;
import com.xtrd.obdcar.adapter.HeaderListAdapter;
import com.xtrd.obdcar.adapter.OfflineAdapter;
import com.xtrd.obdcar.adapter.OfflineDownloadAdapter;
import com.xtrd.obdcar.db.SettingLoader;
import com.xtrd.obdcar.entity.OffLine;
import com.xtrd.obdcar.entity.OffLine.Flag;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.view.MyListView;

public class OffLineActivity extends BaseActivity implements MKOfflineMapListener{

	private static final String TAG = "OffLineActivity";
	private EditText edit_input;
	private ImageButton img_clear;
	private LinearLayout layout_map_list;

	private ArrayList<OffLine> headerList = new ArrayList<OffLine>();
	private MyListView headerListView;//header
	private HeaderListAdapter headerAdapter;

	private ExpandableListView expandList;
	private OfflineAdapter adapter;
	private ArrayList<OffLine> list = new ArrayList<OffLine>();

	private LinearLayout layout_download;
	private Button btn_download_status;
	private Button btn_all;
	private Button btn_download;

	private ListView listView;
	private OfflineDownloadAdapter lAdapter;
	private ArrayList<MKOLUpdateElement> localMapList = new ArrayList<MKOLUpdateElement>();

	private MKOfflineMap mOffline;



	public OffLineActivity() {
		layout_id = R.layout.activity_offline;
	}



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(0, R.drawable.btn_back_bg, R.string.set_item_offline_map, 0, 0);
		mOffline = new MKOfflineMap();
		mOffline.init(this);

		initHeaderList();
		list.addAll(toOffLines(mOffline.getOfflineCityList()));
		initView();
		updateUI();
		regClick();
	}


	private void initHeaderList() {
		headerList.clear();
		OffLine item = new OffLine();
		item.cityName = "当前城市";
		item.setType(1);
		headerList.add(item);
		headerList.add(getCurrentCity());
		item = new OffLine();
		item.cityName = "热门城市";
		item.setType(1);
		headerList.add(item);
		headerList.addAll(toHotList(mOffline.getHotCityList()));
		item = new OffLine();
		item.cityName = "全国";
		item.setType(1);
		headerList.add(item);
	}

	private ArrayList<OffLine> toHotList(ArrayList<MKOLSearchRecord> list) {
		ArrayList<OffLine> datas = new ArrayList<OffLine>();
		OffLine item = null;
		ArrayList<MKOLUpdateElement> allUpdateInfo = mOffline.getAllUpdateInfo();
		if(allUpdateInfo!=null&&allUpdateInfo.size()>0) {
			for(MKOLSearchRecord record : list) {
				item = new OffLine(record.cityName,record.cityID,record.size,record.cityType);
				if(record.childCities!=null&&record.childCities.size()>0) {
					for(MKOLSearchRecord cord : record.childCities) {
						for (MKOLUpdateElement ele : allUpdateInfo){
							if (ele.cityID == cord.cityID){
								item.addToMap(new OffLine(cord.cityName,cord.cityID,cord.size,record.cityType,ele.ratio));
							}
						}
					}
				}else {
					for (MKOLUpdateElement ele : allUpdateInfo){
						if (ele.cityID == record.cityID){
							item.setProgress(ele.ratio);
							item.addToMap(new OffLine(record.cityName,record.cityID,record.size,record.cityType,ele.ratio));
						}
					}
				}
				datas.add(item);
			}
		}else {
			for(MKOLSearchRecord record : list) {
				item = new OffLine(record.cityName,record.cityID,record.size,record.cityType);
				if(record.childCities!=null&&record.childCities.size()>0) {
					for(MKOLSearchRecord cord : record.childCities) {
						item.addToMap(new OffLine(cord.cityName,cord.cityID,cord.size,record.cityType));
					}
				}
				datas.add(item);
			}
		}

		return datas;
	}

	private ArrayList<OffLine> toOffLines(ArrayList<MKOLSearchRecord> list) {
		ArrayList<OffLine> datas = new ArrayList<OffLine>();
		OffLine item = null;
		ArrayList<MKOLUpdateElement> allUpdateInfo = mOffline.getAllUpdateInfo();
		if(allUpdateInfo!=null&&allUpdateInfo.size()>0) {
			for(MKOLSearchRecord record : list) {
				//				if(1==record.cityType) {//省份
				item = new OffLine(record.cityName,record.cityID,record.size,record.cityType);
				if(record.childCities!=null&&record.childCities.size()>0) {
					for(MKOLSearchRecord cord : record.childCities) {
						for (MKOLUpdateElement ele : allUpdateInfo){
							if (ele.cityID == cord.cityID){
								item.addToMap(new OffLine(cord.cityName,cord.cityID,cord.size,record.cityType,ele.ratio));
							}else {
								item.addToMap(new OffLine(cord.cityName,cord.cityID,cord.size,record.cityType));
							}
						}
					}
				}else {
					for (MKOLUpdateElement ele : allUpdateInfo){
						if (ele.cityID == item.cityID){
							item.setProgress(ele.ratio);
						}
					}
				}
				datas.add(item);
				//				}else if(0==record.cityType) {
				//					item = new OffLine(record.cityName,record.cityID,record.size);
				//					datas.add(item);
				//				}

			}
		}else {
			for(MKOLSearchRecord record : list) {
				//				if(1==record.cityType) {//省份
				item = new OffLine(record.cityName,record.cityID,record.size,record.cityType);
				if(record.childCities!=null&&record.childCities.size()>0) {
					for(MKOLSearchRecord cord : record.childCities) {
						item.addToMap(new OffLine(cord.cityName,cord.cityID,cord.size,record.cityType));
					}
				}
				datas.add(item);
				//				}else if(0==record.cityType) {
				//					item = new OffLine(record.cityName,record.cityID,record.size);
				//					datas.add(item);
				//				}

			}
		}

		return datas;
	}

	private OffLine getCurrentCity() {
		String city = SettingLoader.getCurrentCity(this);
		ArrayList<MKOLSearchRecord> records = mOffline.searchCity(city);
		if (records == null || records.size() != 1) {
			return null;
		}
		ArrayList<MKOLUpdateElement> allUpdateInfo = mOffline.getAllUpdateInfo();
		if(allUpdateInfo!=null&&allUpdateInfo.size()>0) {
			for (MKOLUpdateElement ele : allUpdateInfo){
				for(MKOLSearchRecord recoder : records) {
					if(ele.cityName.equals(city)&&city.equals(recoder.cityName)) {
						return new OffLine(recoder.cityName,recoder.cityID, recoder.size,recoder.cityType,ele.ratio);
					}else {
						return new OffLine(recoder.cityName,recoder.cityID, recoder.size,recoder.cityType);
					}
				}
			}
		}else {
			for(MKOLSearchRecord recoder : records) {
				if(city.equals(recoder.cityName)) {
					return new OffLine(recoder.cityName,recoder.cityID, recoder.size,recoder.cityType);
				}
			}
		}


		return null;
	}

	private void initView() {
		layout_map_list = (LinearLayout)findViewById(R.id.layout_map_list);
		edit_input = (EditText)findViewById(R.id.edit_input);
		img_clear = (ImageButton)findViewById(R.id.img_clear);
		img_clear.setOnClickListener(this);

		View inflate = LinearLayout.inflate(this,R.layout.offlinemap_header, null);
		headerListView = (MyListView) inflate.findViewById(R.id.headerListView);

		expandList = (ExpandableListView)findViewById(R.id.expandlist);
		expandList.addHeaderView(inflate);

		layout_download = (LinearLayout)findViewById(R.id.layout_download);
		btn_download_status = (Button)findViewById(R.id.btn_download_status);
		listView = (ListView)findViewById(R.id.listView);
		btn_all = (Button)findViewById(R.id.btn_all);
		btn_download = (Button)findViewById(R.id.btn_download);
	}


	private void updateUI() {
		if(headerAdapter==null) {
			headerAdapter = new HeaderListAdapter(this, headerList,mOffline);
			headerListView.setAdapter(headerAdapter);
		}else {
			headerAdapter.notifyDataSetChanged();
		}

		if(adapter==null) {
			adapter = new OfflineAdapter(this, list,mOffline);
			expandList.setAdapter(adapter);
		}else {
			adapter.notifyDataSetChanged();
		}
	}

	private void regClick() {
		btn_download.setOnClickListener(this);
		btn_all.setOnClickListener(this);
		btn_download_status.setOnClickListener(this);
		expandList.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				return false;
			}
		});

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
				updateSelectedView(s.toString());
			}
		});
	}

	protected void updateSelectedView(String input) {
		if(!StringUtils.isNullOrEmpty(input)) {
			headerListView.setVisibility(View.GONE);
			for(int i=list.size()-1;i>=0;i--) {
				if(!list.get(i).cityName.contains(input)){
					list.remove(i);
				}
			}
		}else {
			headerListView.setVisibility(View.VISIBLE);
			list.clear();
			list.addAll(toOffLines(mOffline.getOfflineCityList()));
			updateUI();
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_all:
			layout_map_list.setVisibility(View.VISIBLE);
			layout_download.setVisibility(View.GONE);
			btn_all.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
			btn_download.setBackgroundColor(getResources().getColor(R.color.transparent));
			break;
		case R.id.btn_download:
			layout_map_list.setVisibility(View.GONE);
			layout_download.setVisibility(View.VISIBLE);
			btn_download.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
			btn_all.setBackgroundColor(getResources().getColor(R.color.transparent));
			displayDownloaded();
			break;

		case R.id.btn_download_status:

			break;
		case R.id.img_clear:
			edit_input.setText("");
			break;

		default:
			break;
		}
	}



	private void displayDownloaded() {
		localMapList.clear();
		if(mOffline.getAllUpdateInfo()!=null&&mOffline.getAllUpdateInfo().size()>0) {
			localMapList.addAll(mOffline.getAllUpdateInfo());
		}
		if(lAdapter==null) {
			lAdapter = new OfflineDownloadAdapter(this, localMapList,mOffline);
			listView.setAdapter(lAdapter);
		}else {
			lAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onGetOfflineMapState(int type, int state) {
		switch (type) {
		case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: {
			MKOLUpdateElement update = mOffline.getUpdateInfo(state);
			// 处理下载进度更新提示
			if (update != null) {
				if(headerList!=null&&headerList.size()>0) {
					for(OffLine line : headerList) {
						if(state == line.cityID) {
							line.setProgress(update.ratio);
							line.setFlag(Flag.DOWNLOADING);
						}
					}
				}

				if(list!=null&&list.size()>0) {
					for (OffLine bean : list){
						if(1==bean.cityType) {
							for(OffLine line : bean.getList()) {
								if (line.cityID == state){
									line.setProgress(update.ratio);
									line.setFlag(Flag.DOWNLOADING);
								}
							}
						}else {
							if (bean.cityID == state){
								bean.setProgress(update.ratio);
								bean.setFlag(Flag.DOWNLOADING);
							}
						}
					}
				}

				if(localMapList!=null&&localMapList.size()>0) {
					for(MKOLUpdateElement element : localMapList) {
						if(element.cityID == state) {
							element.ratio = update.ratio;
							element.size = update.size;
							element.status = update.status;
						}
					}
				}

				headerAdapter.notifyDataSetChanged();
				adapter.notifyDataSetChanged();

				if(lAdapter!=null) {
					lAdapter.notifyDataSetChanged();
				}
			}
		}
		break;
		case MKOfflineMap.TYPE_NEW_OFFLINE:
			// 有新离线地图安装
			Log.d("OfflineDemo", String.format("add offlinemap num:%d", state));
			break;
		case MKOfflineMap.TYPE_VER_UPDATE:
			// 版本更新提示
			// MKOLUpdateElement e = mOffline.getUpdateInfo(state);

			break;
		}
	}

	/**
	 * 删除一个条目 进行刷新
	 */
	public void onrefresh() {
		initHeaderList();
		list.addAll(toOffLines(mOffline.getOfflineCityList()));
		updateUI();
	}
}
