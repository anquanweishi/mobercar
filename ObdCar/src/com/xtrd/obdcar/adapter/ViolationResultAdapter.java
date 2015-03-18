package com.xtrd.obdcar.adapter;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xtrd.obdcar.entity.ViolationResult;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.TimeUtils;

public class ViolationResultAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<ViolationResult> list;

	public ViolationResultAdapter(Context context,ArrayList<ViolationResult> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list!=null?list.size():0;
	}

	@Override
	public ViolationResult getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViolationResult result = list.get(position);
		ViewHolder view = null;
		if (convertView == null) {
			view = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_violation_result, null);
			view.view = (View) convertView.findViewById(R.id.view);
			view.text_hour = (TextView) convertView.findViewById(R.id.text_hour);
			view.text_day = (TextView) convertView.findViewById(R.id.text_day);
			view.text_year = (TextView) convertView.findViewById(R.id.text_year);
			view.text_info1 = (TextView) convertView.findViewById(R.id.text_info1);
			view.text_info2 = (TextView) convertView.findViewById(R.id.text_info2);
			view.text_location = (TextView) convertView.findViewById(R.id.text_location);
			view.text_intro = (TextView) convertView.findViewById(R.id.text_intro);
			convertView.setTag(view);
		} else {
			view = (ViewHolder) convertView.getTag();
		}
		
		updateUI(view,result);

		return convertView;
	}
	private void updateUI(ViewHolder view, ViolationResult result) {
		Date date = TimeUtils.getTimeByStr(result.getDate());
		if(date!=null) {
			view.text_hour.setText(TimeUtils.formatDate2Hour(date));
			view.text_day.setText(TimeUtils.formatDate2Day(date));
			view.text_year.setText(TimeUtils.formatDate2Year(date));
		}
		processColor(view.text_info1, context.getResources().getString(R.string.text_vio_info1), result.getMoney()+"");
		processColor(view.text_info2, context.getResources().getString(R.string.text_vio_info2), result.getFen()+"");
		view.text_location.setText(result.getArea());
		view.text_intro.setText(result.getAct());
	}
	
	private void processColor(TextView view,String res, String data) {
		String str = String.format(res, data);
		SpannableString s = new SpannableString(str);
		s.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.red)), str.indexOf(data), str.indexOf(data)+data.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		view.setText(s);
	}
	
	class ViewHolder {
		View view;
		TextView text_hour;
		TextView text_day;
		TextView text_year;
		TextView text_location;
		TextView text_info1;
		TextView text_info2;
		TextView text_intro;
	}


}
