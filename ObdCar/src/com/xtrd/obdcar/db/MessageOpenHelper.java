package com.xtrd.obdcar.db;


import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.xtrd.obdcar.entity.EMMessage;

public class MessageOpenHelper extends SQLiteOpenHelper {

	private Context context;
	private static final String DBNAME = "xtrd_message";
	private static final int VERSION = 1;

	public static final String MESSAGE_DB = "table_message";
	public static final String ID = "_id";
	public static final String BrachID = "branchId";
	public static final String NickName = "nickname";
	public static final String Content = "content";
	public static final String Type = "type";
	public static final String Time = "time";


	private static final String Car_Create_Sql = "CREATE TABLE IF NOT EXISTS "+MESSAGE_DB+" (" +
			""+ID+" integer primary key autoincrement, "
			+BrachID+" INTEGER, "
			+NickName+" TEXT, "
			+Content+" TEXT, "
			+Type+" INTEGER, "
			+Time+" LONG )";



	private static MessageOpenHelper mDatabase;
	private SQLiteDatabase mSQLiteDatabase;

	public MessageOpenHelper(Context context) {
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

	public static synchronized MessageOpenHelper getInstance(Context context) {
		if (mDatabase == null) {
			mDatabase = new MessageOpenHelper(context);
		}
		return mDatabase;
	}

	public void open() throws SQLException {
		mSQLiteDatabase = mDatabase.getWritableDatabase();
	}

	public void close() {
		mDatabase.close();
	}



	private ContentValues putResultContentValues(EMMessage item) {
		if(item==null) {
			return null;
		}

		ContentValues values = new ContentValues();
		values.put(BrachID,item.getBranchId());
		values.put(NickName, item.getUser());
		values.put(Content, item.getContent());
		values.put(Type, item.getType());
		values.put(Time, item.getTime());
		return values;
	}
	/**
	 * 插入package
	 * @param city 
	 * @param package
	 * @return
	 */
	public boolean insertItem(EMMessage item) {
		if(item == null) {
			return false;
		}
		if(mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}
		ContentValues values = putResultContentValues(item);

		if(values.size()>0) {
			return mSQLiteDatabase.insert(MESSAGE_DB, null, values)>0;
		}

		return false;
	}




	public ArrayList<EMMessage> getMessages() {
		ArrayList<EMMessage> data = new ArrayList<EMMessage>();
		if(mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}
		Cursor cursor = mSQLiteDatabase.query(MESSAGE_DB, null, null, null, null, null, Time+" ASC");

		if(cursor==null) {
			return data;
		}

		if(cursor.getCount()==0) {
			cursor.close();
			return data;
		}

		int branchIndex = cursor.getColumnIndexOrThrow(BrachID);
		int contentIndex = cursor.getColumnIndexOrThrow(Content);
		int typeIndex = cursor.getColumnIndexOrThrow(Type);
		int timeIndex = cursor.getColumnIndexOrThrow(Time);

		EMMessage result = null;
		for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()) {
			result = new EMMessage();
			result.setBranchId(cursor.getInt(branchIndex));
			result.setContent(cursor.getString(contentIndex));
			result.setType(cursor.getInt(typeIndex));
			result.setTime(cursor.getLong(timeIndex));
			data.add(result);
		}
		return data;
	}

	public EMMessage getReceiveMsg() {
		if(mSQLiteDatabase==null||!mSQLiteDatabase.isOpen()) {
			open();
		}
		Cursor cursor = mSQLiteDatabase.query(MESSAGE_DB, null, Type+"=1", null, null, null, Time+" DESC");

		if(cursor==null) {
			return null;
		}

		if(cursor.getCount()==0) {
			cursor.close();
			return null;
		}

		int branchIndex = cursor.getColumnIndexOrThrow(BrachID);
		int contentIndex = cursor.getColumnIndexOrThrow(Content);
		int typeIndex = cursor.getColumnIndexOrThrow(Type);
		int timeIndex = cursor.getColumnIndexOrThrow(Time);
		cursor.moveToFirst();
		EMMessage result = new EMMessage();
		result.setBranchId(cursor.getInt(branchIndex));
		result.setContent(cursor.getString(contentIndex));
		result.setType(cursor.getInt(typeIndex));
		result.setTime(cursor.getLong(timeIndex));
		return result;
	}
}
