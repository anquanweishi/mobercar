package com.xtrd.obdcar.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtrd.obdcar.entity.MComment;
import com.xtrd.obdcar.tumi.R;

public class CommentAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<MComment> list;

	public CommentAdapter(Context context,ArrayList<MComment> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list!=null?list.size():0;
	}

	@Override
	public MComment getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final MComment info = list.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_m_comment, null);
			viewHolder.layout_comment_item = (LinearLayout) convertView.findViewById(R.id.layout_comment_item);
			viewHolder.img_comment = (ImageView) convertView.findViewById(R.id.img_comment);
			viewHolder.text_title = (TextView) convertView.findViewById(R.id.text_title);
			viewHolder.text_desc = (TextView) convertView.findViewById(R.id.text_desc);
			viewHolder.text_time = (TextView) convertView.findViewById(R.id.text_time);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
	
		updateUI(viewHolder,info);
		return convertView;
	}
	
	private void updateUI(ViewHolder viewHolder, MComment info) {
		if(info!=null) {
			if(1==info.getStatus()) {
				viewHolder.text_title.setText("推荐");
				viewHolder.img_comment.setImageResource(R.drawable.ic_comment_up);
			}else {
				viewHolder.text_title.setText("吐槽");
				viewHolder.img_comment.setImageResource(R.drawable.ic_comment_down);
			}
			viewHolder.text_desc.setText(info.getDesc());
			viewHolder.text_time.setText(info.getCreateTime());
		}
	}
	
	final static class ViewHolder {

		public LinearLayout layout_comment_item;
		public ImageView img_comment;
		public TextView text_title;
		public TextView text_desc;
		public TextView text_time;

	}

}
