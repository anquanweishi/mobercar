package com.xtrd.obdcar.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.xtrd.obdcar.setting.OffLineActivity;
import com.xtrd.obdcar.setting.ViewMapActivity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.Utils;
import com.xtrd.obdcar.utils.log.LogUtils;
import com.xtrd.obdcar.view.ObdDialog;

public class OfflineDownloadAdapter extends BaseAdapter {
	private static final String TAG = "OfflineDownloadAdapter";
	private Context context;
	private ArrayList<MKOLUpdateElement> list;
	private MKOfflineMap offLine;

	public OfflineDownloadAdapter(Context context,ArrayList<MKOLUpdateElement> list,MKOfflineMap offLine) {
		this.context = context;
		this.list = list;
		this.offLine = offLine;
	}

	@Override
	public int getCount() {
		return list!=null?list.size():0;
	}

	@Override
	public MKOLUpdateElement getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final MKOLUpdateElement record = list.get(position);
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_offline_item, null);
			viewHolder.layout_offline_item = (LinearLayout) convertView.findViewById(R.id.layout_offline_item);
			viewHolder.text_title = (TextView) convertView.findViewById(R.id.text_title);
			viewHolder.text_update = (TextView) convertView.findViewById(R.id.text_update);
			viewHolder.text_size = (TextView) convertView.findViewById(R.id.text_size);
			viewHolder.text_status = (TextView) convertView.findViewById(R.id.text_status);
			viewHolder.btn_download = (ImageButton) convertView.findViewById(R.id.btn_download);
			viewHolder.btn_look = (Button) convertView.findViewById(R.id.btn_look);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		updateUI(viewHolder,record);
		OnClickListener click = new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.layout_offline_item:
					break;
				case R.id.btn_download:
					startDownload(record);
					break;
				case R.id.btn_look:
					Intent intent = new Intent(context,ViewMapActivity.class);
					intent.putExtra("x", record.geoPt.longitude);
					intent.putExtra("y", record.geoPt.latitude);
					intent.putExtra("title", record.cityName);
					context.startActivity(intent);
					break;

				default:
					break;
				}
			}
		};
		viewHolder.layout_offline_item.setOnClickListener(click);
		viewHolder.btn_download.setOnClickListener(click);
		viewHolder.btn_look.setOnClickListener(click);
		viewHolder.layout_offline_item.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				showLoginDialog(record);
				return false;
			}
		});


		return convertView;
	}

	private void startDownload(MKOLUpdateElement record) {
		if(record.update){
			offLine.remove(record.cityID);
			record.update = false;
		}

		if(MKOLUpdateElement.DOWNLOADING!=record.status) {
			boolean start = offLine.start(record.cityID);
			if(start) {
				Utils.showToast(context, context.getResources().getString(R.string.tips_start_download));
				LogUtils.e(TAG, "city id " + record.cityID+" " + record.cityName);
				OfflineDownloadAdapter.this.notifyDataSetChanged();
			}
		}else {
			Utils.showToast(context,"该城市正在下载中...");
		}
	}

	public void showLoginDialog(final MKOLUpdateElement record) {
		ObdDialog dialog = new ObdDialog(context).setTitle("温馨提示")
				.setMessage("您确认删除该离线地图吗？")
				.setPositiveButton(context.getResources().getString(R.string.btn_cancel), new ObdDialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				})
				.setNegativeButton(context.getResources().getString(R.string.btn_confirm), new ObdDialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						offLine.remove(record.cityID);
						list.remove(record);
						OfflineDownloadAdapter.this.notifyDataSetChanged();
						((OffLineActivity)context).onrefresh();
					}
				});
		if(!dialog.isShowing()) {
			dialog.show();
		}
	}
	private void updateUI(ViewHolder viewHolder, MKOLUpdateElement info) {
		viewHolder.text_title.setText(info.cityName);
		viewHolder.text_size.setText(Utils.formatDataSize(info.size));
		viewHolder.btn_look.setVisibility(View.GONE);
		if (100==info.ratio) {
			viewHolder.btn_download.setVisibility(View.GONE);
			viewHolder.btn_look.setVisibility(View.VISIBLE);
			viewHolder.text_status.setText("已下载");
		} else if(0==info.ratio){
			viewHolder.btn_download.setVisibility(View.VISIBLE);
			viewHolder.btn_look.setVisibility(View.GONE);
			viewHolder.text_status.setText("未下载");
		}else {
			viewHolder.btn_download.setVisibility(View.VISIBLE);
			viewHolder.btn_look.setVisibility(View.GONE);
			String progressMsg = "";
			// 根据当前状态，设置显示
			switch (info.status){
			case MKOLUpdateElement.SUSPENDED:
				progressMsg += "暂停下载";
				break;
			case MKOLUpdateElement.WAITING:
				progressMsg += "等待下载";
				viewHolder.btn_download.setVisibility(View.GONE);
				break;
			case MKOLUpdateElement.DOWNLOADING:
				viewHolder.btn_download.setVisibility(View.GONE);
				if(100!=info.ratio) {
					progressMsg += "正在下载"+info.ratio+"%";
				}else {
					progressMsg += "已下载";
				}
				break;
			case MKOLUpdateElement.FINISHED:
				progressMsg += "已下载";
				viewHolder.btn_download.setVisibility(View.GONE);
				viewHolder.btn_look.setVisibility(View.VISIBLE);
				break;
			case MKOLUpdateElement.UNDEFINED:
				progressMsg += "未下载";
				viewHolder.btn_download.setVisibility(View.VISIBLE);
				break;
			default:
				viewHolder.btn_download.setVisibility(View.VISIBLE);
				break;
			}
			viewHolder.text_status.setText(progressMsg);
		}
		
		if(info.update) {
			viewHolder.text_update.setVisibility(View.VISIBLE);
			viewHolder.text_update.setText("有新版本！");
			viewHolder.btn_download.setVisibility(View.VISIBLE);
		}else{
			viewHolder.text_update.setVisibility(View.GONE);
		}

		
	}

	final static class ViewHolder {

		public LinearLayout layout_offline_item;
		public TextView text_title;
		public TextView text_size;
		public TextView text_update;
		public TextView text_status;
		public Button btn_look;
		public ImageButton btn_download;

	}

}
