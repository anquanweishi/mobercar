package com.xtrd.obdcar.config;

import com.xtrd.obdcar.XtrdApp;
import com.xtrd.obdcar.db.SettingLoader;

public class ApiConfig {
	public static final String TEST_API_URL = "http://192.168.1.200:8080/obd-server/";//北京内网
	public static final String JILIN_API_URL = "http://app.sevoh.com/obd-server/";//吉林外网
	public static final String BEIJING_API_URL = "http://inforstack.wicp.net:8081/obd-server/";//北京外网
	public static final String RELEASE_JILIN_API_URL = "http://xtrd.sevoh.com/obd-server/";//吉林
//	public static final String DEFAULT_API_URL = "http://obd.sevoh.com/obd-server/";//正式服务器
	public static final String DEFAULT_API_URL = JILIN_API_URL;//正式服务器

	public static final int InvilidSession = 10005;	
	//服务条款
	public static final String APP_Service_Right = DEFAULT_API_URL+"/wiki/serviceme.html";
	public static final String Paizhao_Url = DEFAULT_API_URL+"/wiki/paizhaozhinan.html";
	
	//app 更新
	public static final String APP_Update_Url = "p/app/version/check";
	
	
	public static final String Simulate_Url = "example";
	
	//2.0 url
	public static final String AUTH_CODE_URL = "user/validate/code";
	public static final String Vilidate_Phone_Url = "user/phone/existe";
	public static final String Reg_Url = "user/phone/register";
	
	
	//找回密码
	public static final String Send_Code_Url = "user/forget/pwd";
	public static final String Vilidate_Code_Url = "user/forget/next";
	
	
	//激活盒子
	public static final String Active_Add_Car_Url = "p/user/bind/vehicles";
	public static final String Active_Bind_Box_Url = "p/vehicle/bind/sn";
	public static final String Active_Verify_Url = "p/vehicle/verify";
	
	
	//登录注册
	public static final String Forget_Pwd_Url = "password/forget";
	public static final String Login_Url = "p/dologin";
	public static final String AREA_Url = "area/children";
	
	//燃油
	public static final String OIL_Url = "fuel/type/list";
	//车辆相关
	public static final String Car_Branch_Url = "vehicle/branch/list";
	public static final String Car_Logo_Url = "vehicle/branch/icon";
	public static final String Car_Manufacturer_Url = "vehicle/manufacturer/list";
	public static final String Car_Series_Url = "vehicle/series/list";
	public static final String Car_Model_Url = "vehicle/model/list";
	public static final String Car_List_Url = "p/user/vehicles";
	public static final String NO_ACTIVE_CAR_LIST_URL = "p/vehicle/noactive";
	public static final String Default_Car_Set_Url = "p/user/vehicle/default/update";
	public static final String Bind_Car_Url = "p/user/vehicle/bind";
	public static final String Unbind_Car_Url = "p/user/vehicle/unbind";
	public static final String Health_Check_Url = "p/vehicle/health/check";
	public static final String OBD_Detail_Check_Url = "p/vehicle/data/obd/detail";
	public static final String OBD_Date_Get_Url = "p/vehicle/data/obd/search";
	public static final String Car_Alarm_Url = "p/vehicle/move/alarm/test";
	public static final String Car_Owner_Url = "p/vehicle/owner/info";
	public static final String Car_Owner_Update_Url = "p/vehicle/owner/update";
	//城市
	public static final String City_Url = "area/children";
	
	//主页
	public static final String Car_Default_Url = "p/user/vehicle/default";
	public static final String Car_Info_Url = "p/vehicle/info";
	public static final String WEATHER_URL = "weather/info";
	public static final String Real_Price_URL = "p/station/real/price";
	public static final String Car_Limit_URL = "vehicle/restrict";
	public static final String Car_Alarm_URL = "p/vehicle/move/alarm/find";
	public static final String Car_Alarm_Update_URL = "p/vehicle/move/alarm/update";
	public static final String Car_4S_URL = "p/ssss/list";
	public static final String Car_Insurance_Company_URL = "p/ic/list";
	public static final String Car_Fault_URL = "p/vehicle/obd/fault/list";
	
	//专家问答
	public static final String QA_Url = "p/my/q/ask";
	
	//保养
	public static final String Maintain_Url = "p/maintain/info";
	public static final String Maintain_Recoders_Url = "p/maintain/records";
	public static final String Maintain_Add_Recoders_Url = "p/maintain/add";
	public static final String Maintain_Item_Url = "p/maintain/parts";
	public static final String Maintain_Zhouqi_Set_Url = "p/maintain/cycle/set";
	public static final String Maintain_DISTANCE_Zhouqi_Set_Url = "p/maintain/distance/set";
	public static final String MainTain_Items_Url = "p/maintain/parts/list";
	
	//商家
	public static final String Merchants_Url = "p/merchant/list";
	public static final String Merchant_Detail_Url = "p/merchant/detail";
	public static final String Discount_Url = "p/merchant/discount/show";
	public static final String Order_Url = "p/merchant/schedule";
	public static final String Take_Order_Url = "p/merchant/schedule/apply";
	public static final String Cancel_Order_Url = "p/merchant/schedule/cancel";
	public static final String My_Order_Url = "p/merchant/my/schedule";
	public static final String Store_Merchant_Url = "p/merchant/favorite";
	public static final String Comment_Url = "p/merchant/comment/show";
	public static final String Evaluate_Url = "p/merchant/comment";
	
	//油
	public static final String My_Subscrption_Url = "p/station/my/attent";
	public static final String Cancel_Subscrption_Url = "p/station/attent/cancel";
	public static final String Subscribe_Url = "p/station/attent";
	public static final String Subscrption_Url = "p/station/search";
	public static final String GasStation_Detail_Url = "p/station/detail";
	public static final String GasStation_Comment_Url = "p/station/comment/show";
	public static final String Oil_Price_Report_Url = "p/station/refuel/show";
	public static final String Oil_Price_Report_Add_Url = "p/station/comment/add";
	public static final String POI_Exist_Url = "p/station/position/existed";
	//周边
	public static final String Near_Category_Url = "p/station/keywords";
	public static final String Fav_Shop_Url = "p/station/position/add";
	
	//排行榜
	public static final String Bill_Board_Url = "p/my/sort";
	public static final String User_Detail_Url = "p/my/info";
	//特惠商家
	public static final String Discount_Merchant_Url = "p/merchant/discount/search";
	
	
	//理赔
	public static final String Incident_Type_Url = "p/accident/types";
	public static final String Incident_Upload_Url = "p/accident/upload";
	
	//一键救援
	public static final String Onekey_Help_Url = "p/accident/rescue/show";
	public static final String Onekey_Help_Send_Url = "p/my/rescue";
	
	//用车报告
	public static final String Car_Use_Url = "p/vehicle/report";
	
	//故障码
	public static final String Fault_Search_Url = "fault/search";
	public static final String Fault_Stats_Url = "fault/stats";
	
	//车况
	public static final String VC_Show_Url = "health/show";
	public static final String Dash_Board_Show_Url = "p/vehicle/trip/detail";
	
	//行程
	public static final String Car_Trip_URL = "p/vehicle/trip/search";
	public static final String Car_Trip_DATE_URL = "p/vehicle/data/trip/search";//时间查询
	public static final String Car_Trip_Fuel_Detail_URL = "p/vehicle/data/ifc/search";//油耗详情
	public static final String Trip_Gps_Url = "p/vehicle/data/trip/gps/search";//gps信息
	public static final String Trip_Load_Url = "p/vehicle/data/trip/load";//gps信息
	public static final String Trip_Current_Load_Url = "p/vehicle/currentTime/trip";//gps信息
	public static final String Trip_Current_Time_Load_Url = "p/vehicle/currentTime/trip/gps";//gps信息
	//安全驾驶
	public static final String Car_Drive_URL = "p/vehicle/data/trip/velocity/search";
	public static final String Car_Drive_Rank_URL = "p/vehicle/data/trip/score/rank";
	//违章查询
	public static final String Violation_City_Url = "violation/city/list";
	public static final String Violation_Plate_Url = "violation/plate/category/list";
	public static final String Violation_Query_Url = "violation/info";
	//用户
	public static final String User_Get_Url = "p/user";
	public static final String My_Info_Get_Url = "p/my";
	public static final String User_Update_Url = "p/user/update";
	public static final String Pwd_Update_Url = "user/pwd/change";
	public static final String Notify_Type_Url = "p/my/msg/types";
	public static final String Notify_Url = "p/my/msg";
	public static final String Notify_Set_Url = "p/my/msg/set";
	public static final String My_fav_Url = "p/my/favorite";
	
	
	
	//设置
	public static final String Trip_Hide_Url = "p/my/hide/trip";
	
	//服务
	public static final String Module_List_Url = "module/list";
	public static final String Module_Statis_Url = "p/user/module/open";//服务统计
	public static final String Car_Book_Url = "wiki/index.html";//服务统计
	public static final String Car_Gps_Url = "p/vehicle/data/gps/load";
	public static final String Service_Trip_Url = "p/vehicle/data/trip/load";
	public static final String Service_Fuel_Date_Url = "p/vehicle/data/stats/list";
	public static final String Service_Fuel_Date_Info_Url = "p/vehicle/data/stats";
	public static final String Service_Fuel_Days_Url = "p/vehicle/data/stats/day/recent/list";
	public static final String Service_Fuel_Days_Info_Url = "p/vehicle/data/stats/day/recent";
	//image upload
	public static final String Img_Upload_Url = "p/picture/save";
	
	//设置
	public static final String Feedback_Url = "p/feedback";
	//身份信息
	public static final String IdCard_Url = "dictionary/find";
	
	//百度坐标转换
	public static final String Geo_Con_Url = "http://api.map.baidu.com/geoconv/v1/?";
	
	//jpush
	public static final String Jpush_Reg_Url = "p/device/register";
	
	public static String getRequestUrl(String action) {
		return SettingLoader.getSvr(XtrdApp.getAppContext()) + action;
	}
}
