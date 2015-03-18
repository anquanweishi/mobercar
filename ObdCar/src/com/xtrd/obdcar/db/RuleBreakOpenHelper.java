package com.xtrd.obdcar.db;


import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.xtrd.obdcar.entity.ViolationResult;
import com.xtrd.obdcar.utils.StringUtils;

public class RuleBreakOpenHelper extends SQLiteOpenHelper {

	private Context context;
	private static final String DBNAME = "xtrd";
	private static final int VERSION = 2;

	public static final String VIO_DB = "table_rule_break";
	public static final String ID = "_id";
	public static final String VEHICLEID = "vehicleId";
	public static final String VIO_DATE = "date";
	public static final String VIO_AREA = "area";
	public static final String VIO_ACT = "act";
	public static final String VIO_CODE = "code";
	public static final String VIO_FEN = "fen";
	public static final String VIO_MONEY = "money";
	//1.0.4 add column
	public static final String VIO_CITY = "choose_city";


	private static final String Vio_Create_Sql = "CREATE TABLE IF NOT EXISTS "+VIO_DB+" (" +
			""+ID+" integer primary key autoincrement, "
			+VEHICLEID+" TEXT, "
			+VIO_DATE+" TEXT, "
			+VIO_AREA+" TEXT, "
			+VIO_ACT+" TEXT, "
			+VIO_CODE+" TEXT, "
			+VIO_CITY+" TEXT, "
			+VIO_FEN+" INTEGER, "
			+VIO_MONEY+" INTEGER )";



	private static RuleBreakOpenHelper mDatabase;
	private SQLiteDatabase mSQLiteDatabase;

	public RuleBreakOpenHelper(Context context) {
		super(context, DBNAME, null, VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(Vio_Create_Sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion == 1) { // oldVersion 1.0.1
			db.execSQL("ALTER TABLE " + VIO_DB + " ADD "
					+ VIO_CITY + " TEXT ");	
			ContentValues values = new ContentValues();
			values.put(VIO_CITY, SettingLoader.getIllegalCity(context));
			db.update(VIO_DB, values, null, null);
		}
	}

	public static synchronized RuleBreakOpenHelper getInstance(Context context) {
		if (mDatabase == null) {
			mDatabase = new RuleBreakOpenHelper(context);
		}
		return mDatabase;
	}

	public void open() throws SQLException {
		mSQLiteDatabase = mDatabase.getWritableDatabase();
	}

	public void close() {
		mDatabase.close();
	}
	
	public boolean isDateExist(String vehicleId,String city) {
		if(StringUtils.isNullOrEmpty(vehicleId)) {
			return false;
		}

		if(mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}

		Cursor cursor = mSQLiteDatabase.query(VIO_DB, null, VEHICLEID+"=? AND " + VIO_CITY+"=?", new String[]{vehicleId,city}, null, null, null);
		if(cursor==null) {
			return false;
		}else {
			if(cursor.getCount()<=0) {
				cursor.close();
				return false;
			}
		}
		return true;
	}


	public boolean isItemExist(String vehicleId,String date) {
		if(StringUtils.isNullOrEmpty(vehicleId)||StringUtils.isNullOrEmpty(date)) {//false  不为空 true 为空
			return false;
		}

		if(mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}

		Cursor cursor = mSQLiteDatabase.query(VIO_DB, null, VEHICLEID+"=? AND " +VIO_DATE+"=?", new String[]{vehicleId,date}, null, null, null);
		if(cursor==null) {
			return false;
		}else {
			if(cursor.getCount()<=0) {
				cursor.close();
				return false;
			}
		}
		return true;
	}


	private ContentValues putResultContentValues(String vehicleid,ViolationResult item, String city) {
		if(item==null) {
			return null;
		}

		ContentValues values = new ContentValues();
		values.put(VEHICLEID, vehicleid);
		values.put(VIO_DATE,item.getDate());
		values.put(VIO_AREA, item.getArea());
		values.put(VIO_ACT, item.getAct());
		values.put(VIO_CODE, item.getCode());
		values.put(VIO_FEN, item.getFen());
		values.put(VIO_MONEY, item.getMoney());
		values.put(VIO_CITY, city);
		return values;
	}
	/**
	 * 插入package
	 * @param city 
	 * @param package
	 * @return
	 */
	public boolean insertItem(String vehicleid,ViolationResult item, String city) {
		if(StringUtils.isNullOrEmpty(vehicleid)) {
			return false;
		}
		if(mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}
		ContentValues values = putResultContentValues(vehicleid,item,city);

		if(isItemExist(vehicleid,item.getDate())) {
			if(values.size()>0) {
				return mSQLiteDatabase.update(VIO_DB, values, VEHICLEID+"=? AND " + VIO_DATE+"=?",new String[]{vehicleid,item.getDate()})>0;
			}
		}else {
			if(values.size()>0) {
				return mSQLiteDatabase.insert(VIO_DB, null, values)>0;
			}
		}

		return false;
	}

	public boolean updateItem(String vehicleid,ViolationResult item, String city) {
		if(StringUtils.isNullOrEmpty(vehicleid)&&StringUtils.isNullOrEmpty(item.getDate())) {
			return false;
		}
		if(mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}
		ContentValues values = putResultContentValues(vehicleid, item,city);

		if(values.size()>0) {
			return mSQLiteDatabase.update(VIO_DB, values,VEHICLEID+"=? AND " + VIO_DATE+"=?",new String[]{vehicleid,item.getDate()})>0;
		}
		return false;
	}

	public void batchInfos(String vehicleId,ArrayList<ViolationResult> list,String city) {
		if(list==null||list.size()<=0) {
			return ;
		}
		if(mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}
		mSQLiteDatabase.beginTransaction();
		try {
			for(ViolationResult info : list) {
				if(!isItemExist(vehicleId, info.getDate())){
					insertItem(vehicleId,info,city);
				}else{
					updateItem(vehicleId,info,city);
				}
			}
			mSQLiteDatabase.setTransactionSuccessful();  
		} catch(RuntimeException e){  
			mSQLiteDatabase.endTransaction();  
		    throw e;  
		}
		mSQLiteDatabase.endTransaction();
	}
	

	public ArrayList<ViolationResult> getResult(String vehicleid,String city) {
		ArrayList<ViolationResult> data = new ArrayList<ViolationResult>();
		if(mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}
		Cursor cursor = mSQLiteDatabase.query(VIO_DB, null, VEHICLEID+"=? AND " + VIO_CITY +"=?", new String[]{vehicleid,city}, null, null, ID+" ASC");

		if(cursor==null) {
			return data;
		}

		if(cursor.getCount()==0) {
			cursor.close();
			return data;
		}
		int idIndex = cursor.getColumnIndexOrThrow(ID);
		int vehicleIdIndex = cursor.getColumnIndexOrThrow(VEHICLEID);
		int dateIndex = cursor.getColumnIndexOrThrow(VIO_DATE);
		int areaIndex = cursor.getColumnIndexOrThrow(VIO_AREA);
		int actIndex = cursor.getColumnIndexOrThrow(VIO_ACT);
		int codeIndex = cursor.getColumnIndexOrThrow(VIO_CODE);
		int fenIndex = cursor.getColumnIndexOrThrow(VIO_FEN);
		int moneyIndex = cursor.getColumnIndexOrThrow(VIO_MONEY);

		ViolationResult result = null;
		for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()) {
			result = new ViolationResult();
			result.setId(cursor.getInt(idIndex));
			result.setVehicleId(cursor.getString(vehicleIdIndex));
			result.setDate(cursor.getString(dateIndex));
			result.setAct(cursor.getString(actIndex));
			result.setArea(cursor.getString(areaIndex));
			result.setCode(cursor.getString(codeIndex));
			result.setFen(cursor.getInt(fenIndex));
			result.setMoney(cursor.getInt(moneyIndex));
			data.add(result);
		}
		return data;
	}

	public void deleteRuleByVehicleId(String vehicleId) {
		if(mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}
		
		if(StringUtils.isNullOrEmpty(vehicleId)) {
			return ;
		}
		mSQLiteDatabase.delete(VIO_DB, VEHICLEID+"=?", new String[]{vehicleId});
	}
	
}
