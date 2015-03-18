package com.xtrd.obdcar.db;


import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.xtrd.obdcar.entity.CarInfo;
import com.xtrd.obdcar.utils.StringUtils;

public class CarOpenHelper extends SQLiteOpenHelper {

	private Context context;
	private static final String DBNAME = "xtrd_car";
	private static final int VERSION = 1;

	public static final String Car_DB = "table_car";
	public static final String ID = "_id";
	public static final String VEHICLEID = "vehicleId";
	public static final String PLATENUMBER = "plateNumber";
	public static final String MODELID = "modelId";
	public static final String BRANCHID = "branchId";
	public static final String BRANCH = "branch";
	public static final String SERIES = "series";
	public static final String FUELTYPEID = "fuelTypeId";
	public static final String DRIVINGCODE = "drivingCode";
	public static final String DRIVINGAREA = "drivingArea";
	public static final String DISTANCE = "distance";


	private static final String Car_Create_Sql = "CREATE TABLE IF NOT EXISTS "+Car_DB+" (" +
			""+ID+" integer primary key autoincrement, "
			+VEHICLEID+" INTEGER, "
			+PLATENUMBER+" TEXT, "
			+MODELID+" INTEGER, "
			+BRANCHID+" INTEGER, "
			+BRANCH+" TEXT, "
			+SERIES+" TEXT, "
			+FUELTYPEID+" INTEGER, "
			+DRIVINGCODE+" TEXT, "
			+DRIVINGAREA+" TEXT, "
			+DISTANCE+" LONG )";



	private static CarOpenHelper mDatabase;
	private SQLiteDatabase mSQLiteDatabase;

	public CarOpenHelper(Context context) {
		super(context, DBNAME, null, VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(Car_Create_Sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public static synchronized CarOpenHelper getInstance(Context context) {
		if (mDatabase == null) {
			mDatabase = new CarOpenHelper(context);
		}
		return mDatabase;
	}

	public void open() throws SQLException {
		mSQLiteDatabase = mDatabase.getWritableDatabase();
	}

	public void close() {
		mDatabase.close();
	}
	
	public boolean isCarExist(String platenumber) {
		if(StringUtils.isNullOrEmpty(platenumber)) {
			return false;
		}

		if(mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}

		Cursor cursor = mSQLiteDatabase.query(Car_DB, null, PLATENUMBER+"=?", new String[]{platenumber}, null, null, null);
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


	private ContentValues putResultContentValues(CarInfo item) {
		if(item==null) {
			return null;
		}

		ContentValues values = new ContentValues();
		values.put(VEHICLEID, item.getVehicleId());
		values.put(PLATENUMBER,item.getPlateNumber());
		values.put(MODELID, item.getModelId());
		values.put(BRANCHID, item.getBranchId());
		values.put(BRANCH, item.getBranch());
		values.put(SERIES, item.getSeries());
		values.put(FUELTYPEID, item.getDefaultFuelTypeId());
		values.put(DRIVINGCODE, item.getDrivingAreaCode());
		values.put(DRIVINGAREA, item.getDrivingArea());
		values.put(DISTANCE, item.getDistance());
		return values;
	}
	/**
	 * 插入package
	 * @param city 
	 * @param package
	 * @return
	 */
	public boolean insertItem(CarInfo item) {
		if(item == null) {
			return false;
		}
		if(mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}
		ContentValues values = putResultContentValues(item);

		if(isCarExist(item.getPlateNumber())) {
			if(values.size()>0) {
				return mSQLiteDatabase.update(Car_DB, values, PLATENUMBER+"=?",new String[]{item.getPlateNumber()})>0;
			}
		}else {
			if(values.size()>0) {
				return mSQLiteDatabase.insert(Car_DB, null, values)>0;
			}
		}

		return false;
	}

	public boolean updateItem(CarInfo item) {
		if(item == null) {
			return false;
		}
		if(mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}
		ContentValues values = putResultContentValues(item);

		if(values.size()>0) {
			return mSQLiteDatabase.update(Car_DB, values,PLATENUMBER+"=?",new String[]{item.getPlateNumber()})>0;
		}
		return false;
	}

	public void batchInfos(ArrayList<CarInfo> list) {
		if(list==null||list.size()<=0) {
			return ;
		}
		if(mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}
		mSQLiteDatabase.beginTransaction();
		try {
			for(CarInfo info : list) {
				if(!isCarExist(info.getPlateNumber())){
					insertItem(info);
				}else{
					updateItem(info);
				}
			}
			mSQLiteDatabase.setTransactionSuccessful();  
		} catch(RuntimeException e){  
			mSQLiteDatabase.endTransaction();  
		    throw e;  
		}
		mSQLiteDatabase.endTransaction();
	}
	

	public ArrayList<CarInfo> getLocalCars() {
		ArrayList<CarInfo> data = new ArrayList<CarInfo>();
		if(mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}
		Cursor cursor = mSQLiteDatabase.query(Car_DB, null, null, null, null, null, ID+" ASC");

		if(cursor==null) {
			return data;
		}

		if(cursor.getCount()==0) {
			cursor.close();
			return data;
		}
		int vehicleIdIndex = cursor.getColumnIndexOrThrow(VEHICLEID);
		int plateIndex = cursor.getColumnIndexOrThrow(PLATENUMBER);
		int modelIndex = cursor.getColumnIndexOrThrow(MODELID);
		int branchIndex = cursor.getColumnIndexOrThrow(BRANCH);
		int branchIdIndex = cursor.getColumnIndexOrThrow(BRANCHID);
		int seriesIndex = cursor.getColumnIndexOrThrow(SERIES);
		int fuelIndex = cursor.getColumnIndexOrThrow(FUELTYPEID);
		int areaIndex = cursor.getColumnIndexOrThrow(DRIVINGCODE);
		int driveAreaIndex = cursor.getColumnIndexOrThrow(DRIVINGAREA);
		int distanceIndex = cursor.getColumnIndexOrThrow(DISTANCE);

		CarInfo result = null;
		for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()) {
			result = new CarInfo();
			result.setVehicleId(cursor.getInt(vehicleIdIndex));
			result.setPlateNumber(cursor.getString(plateIndex));
			result.setModelId(cursor.getInt(modelIndex));
			result.setBranchId(cursor.getInt(branchIdIndex));
			result.setBranch(cursor.getString(branchIndex));
			result.setSeries(cursor.getString(seriesIndex));
			result.setDefaultFuelTypeId(cursor.getInt(fuelIndex));
			result.setDrivingAreaCode(cursor.getString(areaIndex));
			result.setDrivingArea(cursor.getString(driveAreaIndex));
			result.setDistance(cursor.getInt(distanceIndex));
			data.add(result);
		}
		return data;
	}
	

	public String getCityByPlate(String carPlate) {
		if(mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}
		Cursor cursor = mSQLiteDatabase.query(Car_DB, null, PLATENUMBER+"=?", new String[]{carPlate}, null, null, ID+" ASC");

		if(cursor==null) {
			return "";
		}

		if(cursor.getCount()==0) {
			cursor.close();
			return "";
		}
		cursor.moveToFirst();
		int driveAreaIndex = cursor.getColumnIndexOrThrow(DRIVINGAREA);
		String city = cursor.getString(driveAreaIndex);
		cursor.close();
		return city;
	}

	public void deleteCar(String plateNumber) {
		if(mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}
		
		if(StringUtils.isNullOrEmpty(plateNumber)) {
			return ;
		}
		mSQLiteDatabase.delete(Car_DB, PLATENUMBER+"=?", new String[]{plateNumber});
	}
	
	public void deleteCars() {
		if(mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}
		mSQLiteDatabase.delete(Car_DB, null,null);
	}

	
}
