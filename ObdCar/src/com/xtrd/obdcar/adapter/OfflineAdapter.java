package com.xtrd.obdcar.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.xtrd.obdcar.entity.OffLine;
import com.xtrd.obdcar.entity.OffLine.Flag;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.Utils;

public class OfflineAdapter extends BaseExpandableListAdapter {

	private List<OffLine> mList = null;
	private Context context = null;
	private MKOfflineMap mOffline;

	public OfflineAdapter(Context con, ArrayList<OffLine> list, MKOfflineMap mOffline) {
		this.mList = list;
		this.context = con;
		this.mOffline = mOffline;
	}

	@Override
	public int getGroupCount() {
		return mList != null ? mList.size() : 0;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return mList.get(groupPosition).getList().size();
	}

	@Override
	public OffLine getGroup(int groupPosition) {
		return mList.get(groupPosition);
	}

	@Override
	public OffLine getChild(int groupPosition, int childPosition) {
		return mList.get(groupPosition).getList().get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		final OffLine group = mList.get(groupPosition);
		View view = null;
		ViewHolder holder = null;
		if (convertView == null) {
			view = View.inflate(context, R.layout.layout_offline_group_item, null);
			holder = new ViewHolder();
			holder.layout_textview = (TextView) view.findViewById(R.id.layout_textview);
			holder.layout_group_item = (LinearLayout)view.findViewById(R.id.layout_group_item);
			holder.text_title = (TextView) view.findViewById(R.id.text_title);
			holder.text_size = (TextView) view.findViewById(R.id.text_size);
			holder.text_status = (TextView) view.findViewById(R.id.text_status);
			holder.btn_download = (ImageButton) view.findViewById(R.id.btn_download);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		
		if(group.getList()==null||group.getList().size()==0) {
			holder.layout_textview.setVisibility(View.GONE);
			holder.layout_group_item.setVisibility(View.VISIBLE);
			holder.text_title.setText(group.cityName);
			holder.text_size.setText(Utils.formatDataSize(group.size));
			if(100==group.getProgress()) {
				holder.btn_download.setVisibility(View.GONE);
				holder.text_status.setText("已下载");
			}else {
				switch (group.getFlag()) {
				case DOWNLOADING:
					holder.text_status.setText("正在下载"+group.getProgress()+"%");
					holder.btn_download.setVisibility(View.GONE);
					break;
				case NO_STATUS:
					if(0==group.getProgress()) {
						holder.text_status.setText("未下载");
					}else {
						holder.text_status.setText("已下载"+group.getProgress());
					}
					holder.btn_download.setVisibility(View.VISIBLE);
					break;

				default:
					break;
				}
				
			}
			holder.btn_download.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					startDownload(group);
				}
			});
		}else {
			holder.layout_textview.setVisibility(View.VISIBLE);
			holder.layout_group_item.setVisibility(View.GONE);
			holder.layout_textview.setText(group.cityName);
			holder.layout_textview.setTextColor(context.getResources().getColor(R.color.top_bar_color));
			holder.layout_textview.setBackgroundColor(context.getResources().getColor(R.color.white));
			if(isExpanded) {
				holder.layout_textview.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_up, 0);
			}else {
				holder.layout_textview.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.arrow_down, 0);
			}
		}
		return view;
	}

	final static class ViewHolder {
		public TextView layout_textview;
		public LinearLayout layout_group_item;
		public TextView text_title;
		public TextView text_size;
		public TextView text_status;
		public ImageButton btn_download;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		final OffLine record = mList.get(groupPosition).getList().get(childPosition);
		ChildViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ChildViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_offline_item, null);
			viewHolder.text_title = (TextView) convertView.findViewById(R.id.text_title);
			viewHolder.text_size = (TextView) convertView.findViewById(R.id.text_size);
			viewHolder.text_status = (TextView) convertView.findViewById(R.id.text_status);
			viewHolder.btn_download = (ImageButton) convertView.findViewById(R.id.btn_download);
			viewHolder.btn_look = (Button) convertView.findViewById(R.id.btn_look);
			viewHolder.btn_look.setVisibility(View.GONE);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ChildViewHolder) convertView.getTag();
		}

		updateUI(viewHolder,record);
		OnClickListener click = new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btn_download:
					startDownload(record);
					break;

				default:
					break;
				}
			}


		};
		viewHolder.btn_download.setOnClickListener(click);

		return convertView;
	}

	private void startDownload(MKOLSearchRecord record) {
		boolean start = mOffline.start(record.cityID);
		if(start) {
			Utils.showToast(context, context.getResources().getString(R.string.tips_start_download));
			OfflineAdapter.this.notifyDataSetChanged();
		}
	}

	private void updateUI(ChildViewHolder viewHolder, OffLine record) {
		viewHolder.text_title.setText(record.cityName);
		viewHolder.text_size.setText(Utils.formatDataSize(record.size));
		int progress = record.getProgress();
		String progressMsg = "";
		// 根据进度情况，设置显示
		if (progress == 0){
			progressMsg = "未下载";
			viewHolder.btn_download.setVisibility(View.VISIBLE);
		} else if (progress == 100){
			record.setFlag(Flag.NO_STATUS);
			progressMsg = "已下载";
			viewHolder.btn_download.setVisibility(View.GONE);
		} else{
			viewHolder.btn_download.setVisibility(View.VISIBLE);
			progressMsg = progress + "%";
		}
		// 根据当前状态，设置显示
		switch (record.getFlag())
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


	final static class ChildViewHolder {

		public Button btn_look;
		public TextView text_title;
		public TextView text_size;
		public TextView text_status;
		public ImageButton btn_download;

	}
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
