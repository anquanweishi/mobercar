package com.xtrd.obdcar.view;


import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.xtrd.obdcar.entity.GasStation;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.Utils;

public class GasStationSpinner extends Dialog{
	private Context context;
	private ListView listView;
	private ArrayList<GasStation> list ;
	private CallBack callBack;
	private ListAdapter adapter;
	
	public GasStationSpinner(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}


	public GasStationSpinner(Context context, int theme) {
		super(context, theme);
	}


	public GasStationSpinner(Context context) {
		super(context);
	}

	public GasStationSpinner(Context context, ArrayList<GasStation> list, CallBack callBack) {
		super(context);
		this.context = context;
		this.callBack = callBack;
		this.list = list;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		LayoutInflater listInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup layout = (LinearLayout) listInflater.inflate(R.layout.layout_popup, null);
		LayoutParams layoutParams = new LayoutParams(
				Utils.getScreenWidth(context)-Utils.dipToPixels(context, 80), LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(0, 0, 0, Utils.dipToPixels(context, 10));
		setContentView(layout, layoutParams);
		
		listView = (ListView)layout.findViewById(R.id.listView);
		adapter = new ListAdapter(context,list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(callBack!=null) {
					callBack.callback(list.get(position));
				}
				dismiss();
			}
		});
		
		Window window = getWindow();
//		window.setGravity(Gravity.LEFT | Gravity.TOP);
		window.setGravity(Gravity.CENTER);
		window.setBackgroundDrawableResource(android.R.color.transparent);

//		WindowManager.LayoutParams manager = window.getAttributes();
//		manager.x = 0;
//		manager.y = Utils.getScreenHeight(context)
//				+ Utils.dipToPixels(context, 10);
//		Animation animation = AnimationUtils.loadAnimation(context,
//				R.anim.push_down_in);
//		viewGroup.setAnimation(animation);
//		window.setAttributes(manager);

	}
	

	public interface CallBack {
		void callback(GasStation gasStation);
	}

	public class ListAdapter extends BaseAdapter {

		private Context context;
		private ArrayList<GasStation> list;
		public ListAdapter(Context context,ArrayList<GasStation> list) {
			this.context = context;
			this.list = list;
		}

		@Override
		public int getCount() {
			return list!=null?list.size():0;
		}

		@Override
		public GasStation getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			final GasStation info = getItem(position);
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.layout_car_select, null);
				viewHolder.img_icon = (ImageView) convertView.findViewById(R.id.img_icon);
				viewHolder.img_icon.setVisibility(View.GONE);
				viewHolder.text_plate = (TextView) convertView.findViewById(R.id.text_plate);
				viewHolder.text_branch = (TextView) convertView.findViewById(R.id.text_branch);
				viewHolder.checkBox = (ImageView) convertView.findViewById(R.id.checkBox);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.text_plate.setText(info.getName());
			viewHolder.text_branch.setText(info.getAddress());
			if(info.isChecked()) {
				viewHolder.checkBox.setImageResource(R.drawable.ic_checked);
			}else {
				viewHolder.checkBox.setImageResource(R.color.transparent);
			}

			return convertView;
		}

		final class ViewHolder {
			public ImageView img_icon;
			public TextView text_plate;
			public TextView text_branch;
			public ImageView checkBox;
		}
	}
}