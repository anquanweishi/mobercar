package com.xtrd.obdcar.view;



import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout.LayoutParams;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.Utils;

public class PlateChooseDialog extends Dialog{
	private Context context;
	private GridView gridView;
	private CallBack callback;
	private ListAdapter adapter;
	
	public PlateChooseDialog(Context context,CallBack callback) {
		super(context);
		this.context=context;
		this.callback = callback;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		ViewGroup viewGroup = (ViewGroup) getLayoutInflater().inflate(R.layout.dialog_plate_choose, null);
		gridView = (GridView)viewGroup.findViewById(R.id.gridView1);
		LayoutParams layoutParams = new LayoutParams(Utils.getScreenWidth(context), LayoutParams.WRAP_CONTENT);
		setContentView(viewGroup,layoutParams);
		
		updateUI();

		
		Window window = getWindow();
		window.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP);
		window.setBackgroundDrawableResource(android.R.color.transparent);

		WindowManager.LayoutParams manager=window.getAttributes();
		manager.x=0;
		manager.y=Utils.getScreenHeight(context)+Utils.dipToPixels(context, 10);
		Animation animation=AnimationUtils.loadAnimation(context, R.anim.push_down_in);
		viewGroup.setAnimation(animation);
		window.setAttributes(manager);
		
	}
    
	private void updateUI() {
		adapter = new ListAdapter();
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(callback!=null) {
					callback.callback(adapter.getItem(position));
				}
				dismiss();
			}
		});
		
	}
	
	final class ListAdapter extends BaseAdapter {
		private String[] arr;
		public ListAdapter() {
			arr = context.getResources().getStringArray(R.array.car_plate);
		}

		@Override
		public int getCount() {
			return arr.length;
		}

		@Override
		public String getItem(int position) {
			return arr[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.layout_textview, null);
				viewHolder.text_title = (TextView) convertView.findViewById(R.id.layout_textview);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.text_title.setGravity(Gravity.CENTER);
			viewHolder.text_title.setPadding(0, 0, 0, 0);
			viewHolder.text_title.setText(arr[position]);
			return convertView;
		}
		class ViewHolder {
			public TextView text_title;
		}
	}

	public interface CallBack{
		void callback(String value);
	}
}
