package com.xtrd.obdcar.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.utils.StringUtils;
import com.xtrd.obdcar.utils.Utils;

public class ObdListDialog extends Dialog {

	private Context context;
	private TextView text_title;
	private ListView listView;
	private String title;
	private String[] data;
	private OnClickListener onclick;
	
	public ObdListDialog(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		ViewGroup viewGroup = (ViewGroup) getLayoutInflater().inflate(
				R.layout.obd_list_dialog, null);
		text_title = (TextView)viewGroup.findViewById(R.id.text_title);
		listView = (ListView)viewGroup.findViewById(R.id.listView);
		LayoutParams layoutParams = new LayoutParams(
				Utils.getScreenWidth(context)-Utils.dipToPixels(context, 80), LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(0, 0, 0, Utils.dipToPixels(context, 10));
		setContentView(viewGroup, layoutParams);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.layout_textview,data);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(onclick!=null) {
					onclick.onClick(data[position]);
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
	
	public ObdListDialog setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public ObdListDialog setItemButton(OnClickListener onClickListener) {
		this.onclick = onClickListener;
		return this;
	}	
	
	public ObdListDialog setList(String[] phones) {
		this.data = phones;
		return this;
	}


	public interface OnClickListener {
		void onClick(String value);
	}


	@Override
	public void show() {
		if(context instanceof Activity&&!((Activity)context).isFinishing()) {
			super.show();
		}
		
		if(text_title!=null) {
			if(StringUtils.isNullOrEmpty(title)) {
				text_title.setVisibility(View.GONE);
			}else {
				text_title.setVisibility(View.VISIBLE);
				text_title.setText(title);
			}
		}
		
	}

		
}
