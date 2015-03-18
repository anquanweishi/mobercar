package com.xtrd.obdcar.adapter;


import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.xtrd.obdcar.entity.CarBranch;
import com.xtrd.obdcar.tumi.R;
/**
 * 客户列表适配器
 *
 */
public class BranchAdapter extends BaseAdapter  implements SectionIndexer{

	private List<CarBranch> mList = null;
	private Context mContext = null;
	public BranchAdapter(Context con,List<CarBranch> list){
		this.mList = list;
		this.mContext = con;
	}


	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final CarBranch info = mList.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.model_list_item, null);
			viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.text_group);
			viewHolder.tvLetter = (TextView) convertView.findViewById(R.id.text_title);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		//根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);


		//如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if(position == getPositionForSection(section)){
			//					viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvTitle.setVisibility(View.VISIBLE);
			//					viewHolder.tvLetter.setText(mContent.getSortLetters());
			viewHolder.tvTitle.setText(info.getSortLetters());//设置首字母
		}else{
			viewHolder.tvTitle.setVisibility(View.GONE);
		}

		viewHolder.tvLetter.setText(this.mList.get(position).getBranch());

		return convertView;
	}
	final static class ViewHolder {
		TextView tvLetter;
		TextView tvTitle;
		ImageView mHeader;
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	@Override
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = mList.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	@Override
	public int getSectionForPosition(int position) {
		if(mList.get(position).getSortLetters()!=null) {
			return mList.get(position).getSortLetters().charAt(0);
		}else {
			return "#".charAt(0);
		}
	}
	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String  sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}
}
