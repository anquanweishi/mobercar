package com.xtrd.obdcar.adapter;

import java.util.ArrayList;

import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.entity.NotifyType;
import com.xtrd.obdcar.net.NetRequest;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;

public class NotifyTypeAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<NotifyType> list;

	public NotifyTypeAdapter(Context context,ArrayList<NotifyType> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list!=null?list.size():0;
	}

	@Override
	public NotifyType getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final NotifyType info = getItem(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.layout_notify_type_item, null);
			viewHolder.text_title = (TextView) convertView.findViewById(R.id.text_title);
			viewHolder.text_status = (TextView) convertView.findViewById(R.id.text_status);
			viewHolder.btn_toggle = (ToggleButton) convertView.findViewById(R.id.btn_toggle);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.text_title.setText(info.getName());
		if(1==info.getType()) {
			viewHolder.btn_toggle.setChecked(false);
			viewHolder.text_status.setText("不屏蔽");
		}else {
			viewHolder.btn_toggle.setChecked(true);
			viewHolder.text_status.setText("屏蔽");
		}
		viewHolder.btn_toggle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				info.setType(1==info.getType()?0:1);
				setNotify(info);
			}
		});
		
		return convertView;
	}

	private void setNotify(final NotifyType info) {
		AjaxParams params = new AjaxParams();
		params.put(ParamsKey.ID, info.getId()+"");
		params.put(ParamsKey.TYPE,info.getType()+"");
		NetRequest.requestUrl(context,ApiConfig.getRequestUrl(ApiConfig.Notify_Set_Url), params,
				new NetRequest.NetCallBack() {

			@Override
			public void sucCallback(String str) {
				try {
					if (!StringUtils.isNullOrEmpty(str)) {
						JSONObject json = new JSONObject(str);
						if (json.has("status")) {
							int status = json.getInt("status");
							if (1 == status) {
								Utils.showToast(context, "设置成功");
								NotifyTypeAdapter.this.notifyDataSetChanged();
							} else {
								String msg = json.getString("message");
								Utils.showToast(context, msg);
							}
						}
					}
				} catch (JSONException e) {
					Utils.showToast(context, "设置失败");
					e.printStackTrace();
				}
			}

			@Override
			public void failCallback(int errorNo, String strMsg) {
				Utils.showToast(context, "设置失败");
			}
		});
	}
	
	final static class ViewHolder {
		public TextView text_title;
		public TextView text_status;
		public ToggleButton btn_toggle;
	}

}
