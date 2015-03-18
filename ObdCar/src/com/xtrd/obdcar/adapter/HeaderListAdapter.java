package com.xtrd.obdcar.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.xtrd.obdcar.entity.OffLine;
import com.xtrd.obdcar.entity.OffLine.Flag;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.Utils;

public class HeaderListAdapter extends BaseAdapter {

	private List<OffLine> list;
	private Context context;
	private MKOfflineMap mOffline;

	public HeaderListAdapter(Context context, List<OffLine> list, MKOfflineMap mOffline) {
		this.context = context;
		this.list = list;
		this.mOffline = mOffline;
	}

	@Override
	public int getCount() {
		return list != null ? list.size() : 0;
	}

	@Override
	public OffLine getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final OffLine item = list.get(position);

		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = (LinearLayout) LinearLayout.inflate(context,R.layout.layout_offline_header_item, null);
			viewHolder.layout_textview = (TextView) convertView.findViewById(R.id.layout_textview);
			viewHolder.layout_offline_item = (LinearLayout)convertView.findViewById(R.id.layout_offline_item);
			viewHolder.text_title = (TextView) convertView.findViewById(R.id.text_title);
			viewHolder.text_size = (TextView) convertView.findViewById(R.id.text_size);
			viewHolder.text_status = (TextView) convertView.findViewById(R.id.text_status);
			viewHolder.btn_download = (ImageButton) convertView.findViewById(R.id.btn_download);
			viewHolder.btn_look = (Button) convertView.findViewById(R.id.btn_look);
			viewHolder.btn_look.setVisibility(View.GONE);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		OnClickListener click = new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btn_download:
					startDownload(item);
					break;

				default:
					break;
				}
			}


		};
		viewHolder.btn_download.setOnClickListener(click);
		
		updateUI(viewHolder,item);

		return convertView;
	}

	protected void startDownload(OffLine item) {
		if(OffLine.Flag.DOWNLOADING==item.getFlag()) {
			Utils.showToast(context, "该城市正在下载");
		}else {
			boolean start = mOffline.start(item.cityID);
			if(start) {
				Utils.showToast(context, context.getResources().getString(R.string.tips_start_download));
				HeaderListAdapter.this.notifyDataSetChanged();
			}
		}
	}

	private void updateUI(ViewHolder viewHolder, OffLine item) {
		if(item!=null) {
			if(1==item.getType()) {
				viewHolder.layout_textview.setVisibility(View.VISIBLE);
				viewHolder.layout_textview.setText(item.cityName);
				viewHolder.layout_offline_item.setVisibility(View.GONE);
			}else {
				viewHolder.layout_textview.setVisibility(View.GONE);
				viewHolder.layout_offline_item.setVisibility(View.VISIBLE);
				viewHolder.text_title.setText(item.cityName);
				viewHolder.text_size.setText(Utils.formatDataSize(item.size));
				int progress = item.getProgress();
				String progressMsg = "";
				// 根据进度情况，设置显示
				if (progress == 0){
					progressMsg = "未下载";
					viewHolder.btn_download.setVisibility(View.VISIBLE);
				} else if (progress == 100){
					item.setFlag(Flag.NO_STATUS);
					progressMsg = "已下载";
					viewHolder.btn_download.setVisibility(View.GONE);
				} else{
					viewHolder.btn_download.setVisibility(View.VISIBLE);
					progressMsg = progress + "%";
				}
				// 根据当前状态，设置显示
				switch (item.getFlag())
				{
				case PAUSE:
					progressMsg += "【等待下载】";
					viewHolder.btn_download.setVisibility(View.VISIBLE);
					break;
				case DOWNLOADING:
					progressMsg += "【正在下载】";
					viewHolder.btn_download.setVisibility(View.GONE);
					break;
				default:
					break;
				}
				viewHolder.text_status.setText(progressMsg);
			}
		}
	}

	class ViewHolder {

		public TextView layout_textview;
		public LinearLayout layout_offline_item;
		public Button btn_look;
		public TextView text_title;
		public TextView text_size;
		public TextView text_status;
		public ImageButton btn_download;

	}

}
