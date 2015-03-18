package com.xtrd.obdcar.view;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xtrd.obdcar.active.CarListActivity;
import com.xtrd.obdcar.entity.BarItem;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;
/**
 * 首页导航处理
 * @author Administrator
 *
 */
public class Bars extends LinearLayout {

	private ArrayList<BarItem> list;
	private int linenum = 2,column=3;
	private Context context;

	public Bars(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public Bars(Context context,ArrayList<BarItem> list) {
		super(context);
		this.context = context;
		this.list = list;
		initView();
	}
	
	public void updateData(ArrayList<BarItem> initData) {
		this.list = initData;
		removeAllViews();
		initView();
	}

	private void initView() {
		if(list==null||list.size()==0) {
			return;
		}
		LinearLayout.LayoutParams parent = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		setLayoutParams(parent);
		setOrientation(LinearLayout.VERTICAL);
		
		
		LinearLayout line = null;
		for(int i=0;i<linenum;i++) {
			line = new LinearLayout(context);
			addView(line,parent);
			for(int j=0;j<column;j++) {
				if(i==0) {
					if(j==2) {
						break;
					}
				}
				LinearLayout view = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.layout_bar_item, null);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
				params.setMargins(0, Utils.dipToPixels(context, 10), 0, Utils.dipToPixels(context, 10));
				params.weight = 1;
				line.addView(view,params);
				updateViewText(context,view,list.get(i*linenum+j));
				if(j<list.size()-1) {
					addLine(line,context);
				}
			}
			addHorLine(context);
		}
	}

	

	private void updateViewText(final Context context,LinearLayout view,final BarItem barItem) {
		ImageView img_icon = (ImageView) view.findViewById(R.id.img_icon);
		TextView text_title = (TextView) view.findViewById(R.id.text_title);
		TextView text_desc = (TextView) view.findViewById(R.id.text_desc);
		img_icon.setImageResource(barItem.getImg());
		text_title.setText(barItem.getTitle());
		if(!StringUtils.isNullOrEmpty(barItem.getDesc())) {
			text_desc.setVisibility(View.VISIBLE);
			text_desc.setText(barItem.getDesc());
		}else {
			text_desc.setVisibility(View.GONE);
		}
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(barItem.getDest()!=null) {
					Intent intent = new Intent(context,barItem.getDest());
					intent.putExtra("title", barItem.getTitle());
					if(CarListActivity.class.equals(barItem.getDest())) {
						intent.putExtra("from", true);
					}
					context.startActivity(intent);
				}
			}
		});
		
	}
	
	private void addHorLine(Context context) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,Utils.dipToPixels(context, 1));
		View view = new View(context);
		view.setBackgroundColor(context.getResources().getColor(R.color.home_bar_interval_color));
		addView(view,params);
	}

	/**
	 * 添加分割线
	 * @param line 
	 */
	private void addLine(LinearLayout line, Context context) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Utils.dipToPixels(context,1),LayoutParams.MATCH_PARENT);
		View view = new View(context);
		view.setBackgroundColor(context.getResources().getColor(R.color.home_bar_interval_color));
		line.addView(view,params);
	}

	
}
