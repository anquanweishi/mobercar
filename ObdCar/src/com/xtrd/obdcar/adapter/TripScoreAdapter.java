package com.xtrd.obdcar.adapter;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult.AddressComponent;
import com.xtrd.obdcar.entity.Trip;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.LocationDecoder;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.TimeUtils;

public class TripScoreAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Trip> list;

	public TripScoreAdapter(Context context,ArrayList<Trip> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list!=null?list.size():0;
	}

	@Override
	public Trip getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder view = null;
		if (convertView == null) {
			view = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_trip_score_item, null);
			view.view = (View) convertView.findViewById(R.id.view);
			view.text_start_hour = (TextView) convertView.findViewById(R.id.text_start_hour);
			view.text_start_day = (TextView) convertView.findViewById(R.id.text_start_day);
			view.text_start_year = (TextView) convertView.findViewById(R.id.text_start_year);
			view.text_end_hour = (TextView) convertView.findViewById(R.id.text_end_hour);
			view.text_end_day = (TextView) convertView.findViewById(R.id.text_end_day);
			view.text_end_year = (TextView) convertView.findViewById(R.id.text_end_year);
			view.text_start_location = (TextView) convertView.findViewById(R.id.text_start_location);
			view.text_end_location = (TextView) convertView.findViewById(R.id.text_end_location);
			view.text_score = (TextView) convertView.findViewById(R.id.text_score);
			view.ratingbar = (RatingBar) convertView.findViewById(R.id.ratingbar);
			convertView.setTag(view);
		} else {
			view = (ViewHolder) convertView.getTag();
		}

		final Trip trip = list.get(position);
		updateUI(view,trip);
		

		return convertView;
	}


	private void updateUI(final ViewHolder view, final Trip trip) {
		if(trip.getGpsEndTime()!=null) {
			Date date = TimeUtils.getTimeByStr(trip.getGpsEndTime());
			if(date!=null) {
				view.text_end_hour.setText(TimeUtils.formatDate2Hour(date));
				view.text_end_day.setText(TimeUtils.formatDate2Day(date));
				view.text_end_year.setText(TimeUtils.formatDate2Year(date));
			}else {
				view.text_end_hour.setText("");
				view.text_end_day.setText("");
				view.text_end_year.setText("");
			}
		}else {
			view.text_end_hour.setText("");
			view.text_end_day.setText("");
			view.text_end_year.setText("");
		}
		if(trip.getGpsStartTime()!=null) {
			Date start = TimeUtils.getTimeByStr(trip.getGpsStartTime());
			if(start!=null) {
				view.text_start_hour.setText(TimeUtils.formatDate2Hour(start));
				view.text_start_day.setText(TimeUtils.formatDate2Day(start));
				view.text_start_year.setText(TimeUtils.formatDate2Year(start));
			}else {
				view.text_start_hour.setText("");
				view.text_start_day.setText("");
				view.text_start_year.setText("");
			}
		}else {
			view.text_start_hour.setText("");
			view.text_start_day.setText("");
			view.text_start_year.setText("");
		}
		view.text_start_location.setText("");
		view.text_end_location.setText("");
		if(StringUtils.isNullOrEmpty(trip.getSlocation())) {
			if(trip.getStartGps()!=null) {
				LocationDecoder.decodeLocation(trip.getStartGps().getLatitude(), trip.getStartGps().getLongitude(),true,
						new LocationDecoder.LocationCallBack() {
					@Override
					public void callback(String str, AddressComponent addressComponent) {
						view.text_start_location.setText(str);
						trip.setSlocation(str);
					}
				});
			}
		}else {
			view.text_start_location.setText(trip.getSlocation());
		}
		
		if(StringUtils.isNullOrEmpty(trip.getElocation())) {
			if(trip.getEndGps()!=null) {
				LocationDecoder.decodeLocation(trip.getEndGps().getLatitude(), trip.getEndGps().getLongitude(),true,
						new LocationDecoder.LocationCallBack() {
					@Override
					public void callback(String str, AddressComponent addressComponent) {
						view.text_end_location.setText(str);
						trip.setElocation(str);
					}
				});
			}

		}else {
			view.text_end_location.setText(trip.getElocation());
		}
		view.text_score.setText(String.format(context.getResources().getString(R.string.text_drive_score), (int)trip.getDrivingScore()));
		view.ratingbar.setRating((float)(5*trip.getDrivingScore()/100));
	}



	class ViewHolder {
		View view;
		TextView text_start_hour;
		TextView text_start_day;
		TextView text_start_year;
		TextView text_end_hour;
		TextView text_end_day;
		TextView text_end_year;
		TextView text_start_location;
		TextView text_end_location;
		TextView text_score;
		RatingBar ratingbar;
	}


}
