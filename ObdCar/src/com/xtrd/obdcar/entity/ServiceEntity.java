package com.xtrd.obdcar.entity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

import com.xtrd.obdcar.IllegalActivity;
import com.xtrd.obdcar.carcheck.CarCheckActivity;
import com.xtrd.obdcar.nearby.NearCategoryActivity;
import com.xtrd.obdcar.obdservice.AddOilActivity;
import com.xtrd.obdcar.obdservice.BillboardActivity;
import com.xtrd.obdcar.obdservice.CarBookActivity;
import com.xtrd.obdcar.obdservice.CarUseReportActivity;
import com.xtrd.obdcar.obdservice.FastAssistantActivity;
import com.xtrd.obdcar.obdservice.FindLocationActivity;
import com.xtrd.obdcar.obdservice.PreferentialActivity;
import com.xtrd.obdcar.obdservice.ReportActivity;
import com.xtrd.obdcar.oil.OilReportActivity;
import com.xtrd.obdcar.trip.DateTripActivity;
import com.xtrd.obdcar.trip.DriveScoreActivity;
import com.xtrd.obdcar.tumi.R;
import com.xtrd.obdcar.vc.ProfessorQAActivity;

/**
 * 仅保留name和code
 * 
 * @author Administrator
 * 
 */
public class ServiceEntity {
	private int moduleId;
	private String code;
	private String name;
	private String type;
	private String url;
	private int order;
	private int status;

	private int res;
	private Class<Activity> dest;

	public int getModuleId() {
		return moduleId;
	}

	public void setModuleId(int moduleId) {
		this.moduleId = moduleId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getRes() {
		return res;
	}

	public void setRes(int res) {
		this.res = res;
	}

	public Class<Activity> getDest() {
		return dest;
	}

	public void setDest(Class dest) {
		this.dest = dest;
	}

	public void parser(JSONObject opt) {
		try {
			if (opt != null) {
				if (opt.has("name")) {
					setName(opt.getString("name"));
				}
				if (opt.has("code")) {
					setCode(opt.getString("code"));
					setOther(getCode());
				}

				// 以下暂时不用
				if (opt.has("moduleId")) {
					setModuleId(opt.getInt("moduleId"));
				}

				if (opt.has("type")) {
					setType(opt.getString("type"));
				}
				if (opt.has("url")) {
					setUrl(opt.getString("url"));
				}
				if (opt.has("order")) {
					setOrder(opt.getInt("order"));
				}
				if (opt.has("status")) {
					setStatus(opt.getInt("status"));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private void setOther(String code) {
		if ("a".equals(code)) {//上报油价
			setDest(OilReportActivity.class);
			setRes(R.drawable.ic_service_price);
		} else if ("b".equals(code)) {//油耗统计
			setDest(ReportActivity.class);
			setRes(R.drawable.ic_service_oil_wear);
		} else if ("c".equals(code)) {//我要加油
			setDest(AddOilActivity.class);
			setRes(R.drawable.ic_service_add_oil);
		} else if ("d".equals(code)) {//提问专家
			setDest(ProfessorQAActivity.class);
			setRes(R.drawable.ic_service_qa);
		} else if ("e".equals(code)) {//用车报告
			setDest(CarUseReportActivity.class);
			setRes(R.drawable.ic_service_use_report);
		} else if ("f".equals(code)) {//快赔助理
			setDest(FastAssistantActivity.class);
			setRes(R.drawable.ic_service_fast);
		} else if ("g".equals(code)) {//行程查询
			setDest(DateTripActivity.class);
			setRes(R.drawable.ic_service_trip);
		} else if ("h".equals(code)) {//车况检测
			setDest(CarCheckActivity.class);
			setRes(R.drawable.ic_service_check);
		} else if ("i".equals(code)) {//驾驶评分
			setDest(DriveScoreActivity.class);
			setRes(R.drawable.ic_service_score);
		} else if ("j".equals(code)) {//驾车宝典
			setDest(CarBookActivity.class);
			setRes(R.drawable.ic_service_book);
		}else if ("k".equals(code)) {//附近周边
			setDest(NearCategoryActivity.class);
			setRes(R.drawable.ic_service_near);
		}else if ("l".equals(code)) {//寻车定位
			setDest(FindLocationActivity.class);
			setRes(R.drawable.ic_service_find_loc);
		}else if ("m".equals(code)) {//风云榜
			setDest(BillboardActivity.class);
			setRes(R.drawable.ic_service_list);
		}else if ("n".equals(code)) {//特惠商家
			setDest(PreferentialActivity.class);
			setRes(R.drawable.ic_service_prefer);
		}else if ("o".equals(code)) {//违章查询
			setDest(IllegalActivity.class);
			setRes(R.drawable.ic_service_violation);
		}

	}

	public void toJson(JSONObject obj) {
		// TODO Auto-generated method stub

	}

}
