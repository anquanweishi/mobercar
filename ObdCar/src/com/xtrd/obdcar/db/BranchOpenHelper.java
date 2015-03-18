package com.xtrd.obdcar.db;


import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.xtrd.obdcar.entity.CarBranch;
import com.xtrd.obdcar.entity.CarModel;
import com.xtrd.obdcar.entity.CarSeries;
import com.xtrd.obdcar.utils.StringUtils;

public class BranchOpenHelper extends SQLiteOpenHelper {
	private static final String TAG = "BranchOpenHelper";
	private Context context;
	private static final String DBNAME = "xtrd_branch";
	private static final int VERSION = 1;

	//branch
	public static final String Branch_DB = "table_branch";
	public static final String BRANCHID = "branchId";
	public static final String BRANCH = "branch";
	public static final String NAME = "name";
	public static final String COUNTRY = "country";
	public static final String ACRONYM = "acronym";
	public static final String HASCHILDREN = "hasChildren";
	public static final String BRACH_LETTER = "brach_letter";
	//series
	public static final String Series_DB = "table_series";
	public static final String SERIESID = "seriesId";
	public static final String MANUFACTURERID = "manufacturerId";
	public static final String CATEGORY = "category";
	public static final String SERIES_NAME = "name";
	public static final String TECHNOLOGY = "technology";
	public static final String SERIES_HASCHILDREN = "hasChildren";

	//model
	public static final String Model_DB = "table_model";
	public static final String MODELID = "modelId";
	public static final String MODEL_SERIESID = "seriesId";
	public static final String YEAR = "year";
	public static final String DISPLACEMENT = "displacement";
	public static final String MODEL_NAME = "name";
	public static final String TANKCAPACITY = "tankCapacity";
	public static final String SUPPORT = "support";


	//车品牌
	private static final String Car_Create_Sql = "CREATE TABLE IF NOT EXISTS "+Branch_DB+" (" +
			""+BRANCHID+" integer primary key autoincrement, "
			+BRANCH+" TEXT, "
			+NAME+" TEXT, "
			+COUNTRY+" TEXT, "
			+ACRONYM+" TEXT, "
			+BRACH_LETTER+" TEXT, "
			+HASCHILDREN+" TEXT )";


	//车系
	private static final String Car_Series_Create_Sql = "CREATE TABLE IF NOT EXISTS "+Series_DB+" (" +
			""+SERIESID+" integer primary key autoincrement, "
			+MANUFACTURERID+" INTEGER, "
			+BRANCHID+" INTEGER, "
			+CATEGORY+" TEXT, "
			+SERIES_NAME+" TEXT, "
			+TECHNOLOGY+" TEXT, "
			+SERIES_HASCHILDREN+" INTEGER )";

	//车型
	private static final String Car_Model_Create_Sql = "CREATE TABLE IF NOT EXISTS "+Model_DB+" (" +
			""+MODELID+" integer primary key autoincrement, "
			+MODEL_SERIESID+" INTEGER, "
			+YEAR+" TEXT, "
			+DISPLACEMENT+" INTEGER, "
			+MODEL_NAME+" TEXT, "
			+SUPPORT+" INTEGER, "
			+TANKCAPACITY+" INTEGER )";




	private static BranchOpenHelper mDatabase;
	private SQLiteDatabase mSQLiteDatabase;

	public BranchOpenHelper(Context context) {
		super(context, DBNAME, null, VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(Car_Create_Sql);
		db.execSQL(Car_Series_Create_Sql);
		db.execSQL(Car_Model_Create_Sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public static synchronized BranchOpenHelper getInstance(Context context) {
		if (mDatabase == null) {
			mDatabase = new BranchOpenHelper(context);
		}
		return mDatabase;
	}

	public void open() throws SQLException {
		mSQLiteDatabase = mDatabase.getWritableDatabase();
	}

	public void close() {
		mDatabase.close();
	}

	public boolean isBranchExist(String branchId) {
		if(StringUtils.isNullOrEmpty(branchId)) {
			return false;
		}

		if(mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}

		Cursor cursor = mSQLiteDatabase.query(Branch_DB, null, BRANCHID+"=?", new String[]{branchId}, null, null, null);
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


	private ContentValues putBranchContentValues(CarBranch item) {
		if(item==null) {
			return null;
		}

		ContentValues values = new ContentValues();
		values.put(BRANCHID, item.getBranchId());
		values.put(BRANCH,item.getBranch());
		values.put(NAME, item.getName());
		values.put(COUNTRY, item.getCountry());
		values.put(ACRONYM, item.getAcronym());
		values.put(HASCHILDREN, item.getHasChildren());
		values.put(BRACH_LETTER, item.getSortLetters());
		return values;
	}
	/**
	 * @return
	 */
	public boolean insertItem(CarBranch item) {
		if(item == null) {
			return false;
		}
		if(mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}
		ContentValues values = putBranchContentValues(item);
		if(values.size()>0) {
			return mSQLiteDatabase.insert(Branch_DB, null, values)>0;
		}

		return false;
	}

	public boolean updateItem(CarBranch item) {
		if(item == null) {
			return false;
		}
		if(mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}
		ContentValues values = putBranchContentValues(item);

		if(values.size()>0) {
			return mSQLiteDatabase.update(Branch_DB, values,BRANCHID+"=?",new String[]{item.getBranchId()+""})>0;
		}
		return false;
	}

	public void batchInfos(ArrayList<CarBranch> list) {
		if(list==null||list.size()<=0) {
			return ;
		}
		if(mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}
		mSQLiteDatabase.beginTransaction();
		try {
			for(CarBranch info : list) {
				if(!isBranchExist(info.getBranchId()+"")){
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


	public ArrayList<CarBranch> getLocalBranchs() {
		ArrayList<CarBranch> data = new ArrayList<CarBranch>();
		if(mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}
		Cursor cursor = mSQLiteDatabase.query(Branch_DB, null, null, null, null, null, BRACH_LETTER+" ASC");

		if(cursor==null) {
			return data;
		}

		if(cursor.getCount()==0) {
			cursor.close();
			return data;
		}


		int branchIdIndex = cursor.getColumnIndexOrThrow(BRANCHID);
		int branchIndex = cursor.getColumnIndexOrThrow(BRANCH);
		int nameIndex = cursor.getColumnIndexOrThrow(NAME);
		int countryIndex = cursor.getColumnIndexOrThrow(COUNTRY);
		int acronymIndex = cursor.getColumnIndexOrThrow(ACRONYM);
		int hasChildIndex = cursor.getColumnIndexOrThrow(HASCHILDREN);
		int sortIndex = cursor.getColumnIndexOrThrow(BRACH_LETTER);

		CarBranch result = null;
		for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()) {
			result = new CarBranch();
			result.setBranchId(cursor.getInt(branchIdIndex));
			result.setBranch(cursor.getString(branchIndex));
			result.setName(cursor.getString(nameIndex));
			result.setCountry(cursor.getString(countryIndex));
			result.setAcronym(cursor.getString(acronymIndex));
			result.setHasChildren(cursor.getInt(hasChildIndex));
			result.setSortLetters(cursor.getString(sortIndex));
			data.add(result);
		}
		return data;
	}


	public void deleteBranchs() {
		if(mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}
		mSQLiteDatabase.delete(Branch_DB, null,null);
	}

	//////////////////////////////////////////////////
	//车系
	public boolean isSeriesExist(String seriesId) {
		if(StringUtils.isNullOrEmpty(seriesId)) {
			return false;
		}

		if(mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}

		Cursor cursor = mSQLiteDatabase.query(Series_DB, null, SERIESID+"=?", new String[]{seriesId}, null, null, null);
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


	private ContentValues putSeriesContentValues(CarSeries item) {
		if(item==null) {
			return null;
		}

		ContentValues values = new ContentValues();
		values.put(SERIESID, item.getSeriesId());
		values.put(BRANCHID, item.getBranchId());
		values.put(MANUFACTURERID,item.getManufacturerId());
		values.put(CATEGORY, item.getCategory());
		values.put(SERIES_NAME, item.getName());
		values.put(TECHNOLOGY, item.getTechnology());
		values.put(SERIES_HASCHILDREN, item.getHasChildren());
		return values;
	}
	/**
	 * @return
	 */
	public boolean insertSeriesItem(CarSeries item) {
		if(item == null) {
			return false;
		}
		if(mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}
		ContentValues values = putSeriesContentValues(item);
		if(values.size()>0) {
			return mSQLiteDatabase.insert(Series_DB, null, values)>0;
		}

		return false;
	}

	public boolean updateSeriesItem(CarSeries item) {
		if(item == null) {
			return false;
		}
		if(mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}
		ContentValues values = putSeriesContentValues(item);

		if(values.size()>0) {
			return mSQLiteDatabase.update(Series_DB, values,SERIESID+"=?",new String[]{item.getSeriesId()+""})>0;
		}
		return false;
	}

	public void batchSeriesInfos(ArrayList<CarSeries> list) {
		if(list==null||list.size()<=0) {
			return ;
		}
		if(mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}
		mSQLiteDatabase.beginTransaction();
		try {
			for(CarSeries info : list) {
				if(!isSeriesExist(info.getSeriesId()+"")){
					insertSeriesItem(info);
				}else{
					updateSeriesItem(info);
				}
			}
			mSQLiteDatabase.setTransactionSuccessful();  
		} catch(RuntimeException e){  
			mSQLiteDatabase.endTransaction();  
			throw e;  
		}
		mSQLiteDatabase.endTransaction();
	}



	public ArrayList<CarSeries> getLocalSeries(int branchId) {
		ArrayList<CarSeries> data = new ArrayList<CarSeries>();
		if(mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}
		Cursor cursor = mSQLiteDatabase.query(Series_DB, null, BRANCHID+"=?", new String[]{branchId+""}, null, null, SERIESID+" ASC");

		if(cursor==null) {
			return data;
		}

		if(cursor.getCount()==0) {
			cursor.close();
			return data;
		}


		int seriesIdIndex = cursor.getColumnIndexOrThrow(SERIESID);
		int branchIdIndex = cursor.getColumnIndexOrThrow(BRANCHID);
		int manufacturerIndex = cursor.getColumnIndexOrThrow(MANUFACTURERID);
		int nameIndex = cursor.getColumnIndexOrThrow(SERIES_NAME);
		int cateIndex = cursor.getColumnIndexOrThrow(CATEGORY);
		int technologyIndex = cursor.getColumnIndexOrThrow(TECHNOLOGY);
		int hasChildIndex = cursor.getColumnIndexOrThrow(SERIES_HASCHILDREN);

		CarSeries result = null;
		for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()) {
			result = new CarSeries();
			result.setSeriesId(cursor.getInt(seriesIdIndex));
			result.setBranchId(cursor.getInt(branchIdIndex));
			result.setName(cursor.getString(nameIndex));
			result.setManufacturerId(cursor.getInt(manufacturerIndex));
			result.setCategory(cursor.getString(cateIndex));
			result.setTechnology(cursor.getString(technologyIndex));
			result.setHasChildren(cursor.getInt(hasChildIndex));
			data.add(result);
		}
		return data;
	}


	public void deleteSeries() {
		if(mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}
		mSQLiteDatabase.delete(Series_DB, null,null);
	}


	//////////////////////////////////////////////
	//车型
	public boolean isModelExist(String modelId) {
		if(StringUtils.isNullOrEmpty(modelId)) {
			return false;
		}

		if(mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}

		Cursor cursor = mSQLiteDatabase.query(Model_DB, null, MODELID+"=?", new String[]{modelId}, null, null, null);
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


	private ContentValues putModelContentValues(CarModel item) {
		if(item==null) {
			return null;
		}

		ContentValues values = new ContentValues();
		values.put(MODELID, item.getModelId());
		values.put(MODEL_SERIESID, item.getSeriesId());
		values.put(YEAR,item.getYear());
		values.put(DISPLACEMENT, item.getDisplacement());
		values.put(MODEL_NAME,item.getName());
		values.put(TANKCAPACITY, item.getTankCapacity());
		values.put(TANKCAPACITY, item.getSupport());
		return values;
	}
	/**
	 * @return
	 */
	public boolean insertModelItem(CarModel item) {
		if(item == null) {
			return false;
		}
		if(mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}
		ContentValues values = putModelContentValues(item);

		if(values.size()>0) {
			return mSQLiteDatabase.insert(Model_DB, null, values)>0;
		}

		return false;
	}

	public boolean updateModelItem(CarModel item) {
		if(item == null) {
			return false;
		}
		if(mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}
		ContentValues values = putModelContentValues(item);

		if(values.size()>0) {
			return mSQLiteDatabase.update(Model_DB, values,MODELID+"=?",new String[]{item.getModelId()+""})>0;
		}
		return false;
	}

	public void batchModelInfos(ArrayList<CarModel> list) {
		if(list==null||list.size()<=0) {
			return ;
		}
		if(mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}
		mSQLiteDatabase.beginTransaction();
		try {
			for(CarModel info : list) {
				if(!isModelExist(info.getSeriesId()+"")){
					insertModelItem(info);
				}else{
					updateModelItem(info);
				}
			}
			mSQLiteDatabase.setTransactionSuccessful();  
		} catch(RuntimeException e){  
			mSQLiteDatabase.endTransaction();  
			throw e;  
		}
		mSQLiteDatabase.endTransaction();
	}

	public ArrayList<CarModel> getLocalModel(int seriesId) {
		ArrayList<CarModel> data = new ArrayList<CarModel>();
		if(mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}
		Cursor cursor = mSQLiteDatabase.query(Model_DB, null, MODEL_SERIESID+"=?", new String[]{seriesId+""}, null, null, MODELID+" ASC");

		if(cursor==null) {
			return data;
		}

		if(cursor.getCount()==0) {
			cursor.close();
			return data;
		}

		int modelIdIndex = cursor.getColumnIndexOrThrow(MODELID);
		int seriesIdIndex = cursor.getColumnIndexOrThrow(SERIESID);
		int yearIndex = cursor.getColumnIndexOrThrow(YEAR);
		int displacementIndex = cursor.getColumnIndexOrThrow(DISPLACEMENT);
		int nameIndex = cursor.getColumnIndexOrThrow(MODEL_NAME);
		int tankIndex = cursor.getColumnIndexOrThrow(TANKCAPACITY);
		int supportIndex = cursor.getColumnIndexOrThrow(SUPPORT);
		CarModel result = null;
		for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()) {
			result = new CarModel();
			result.setModelId(cursor.getInt(modelIdIndex));
			result.setSeriesId(cursor.getInt(seriesIdIndex));
			result.setName(cursor.getString(nameIndex));
			result.setYear(cursor.getString(yearIndex));
			result.setDisplacement(cursor.getInt(displacementIndex));
			result.setTankCapacity(cursor.getInt(tankIndex));
			result.setSupport(cursor.getInt(supportIndex));
			data.add(result);
		}
		cursor.close();
		return data;
	}


	public void deleteModel() {
		if(mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}
		mSQLiteDatabase.delete(Model_DB, null,null);
	}

	public int getBranchId(int modelId) {
		String sql = "select branchId from table_series where seriesId = (select seriesId from table_model where modelId ="+modelId+")";
		Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
		if(cursor!=null&&cursor.getCount()>0) {
			cursor.moveToFirst();
			return cursor.getInt(cursor.getColumnIndexOrThrow(BRANCHID));
		}
		return 0;
	}

	public String getSeries(int modelId) {
		String sql = "select name from table_series where seriesId = (select seriesId from table_model where modelId ="+modelId+")";
		Cursor cursor = mSQLiteDatabase.rawQuery(sql,null);
		if(cursor!=null&&cursor.getCount()>0) {
			cursor.moveToFirst();
			return cursor.getString(cursor.getColumnIndexOrThrow(SERIES_NAME));
		}
		return "";
	}
	public String getBranch(int modelId) {
		String sql = "select branch from table_branch where branchId = (select branchId from table_series where seriesId = (select seriesId from table_model where modelId ="+modelId+"))";
		Cursor cursor = mSQLiteDatabase.rawQuery(sql,null);
		if(cursor!=null&&cursor.getCount()>0) {
			cursor.moveToFirst();
			return cursor.getString(cursor.getColumnIndexOrThrow(BRANCH));
		}
		return "";
	}

}
