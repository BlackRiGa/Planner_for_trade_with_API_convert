package com.example.test5.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PlannerTable {

	public static final String NAME_TABLE = "planner_table";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_URGENT = "category";
	public static final String COLUMN_BRIEF = "brief";
	public static final String COLUMN_DESCRIPTION = "description";

	private static final String DATABASE_CREATE = "create table " 
			+ NAME_TABLE
			+ "(" + COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_URGENT + " text not null, "
			+ COLUMN_BRIEF + " text not null,"
			+ COLUMN_DESCRIPTION + " text not null" + ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(PlannerTable.class.getName(), "database update: " + oldVersion + " to " + newVersion);
		database.execSQL("DROP TABLE IF EXISTS " + NAME_TABLE);
		onCreate(database);
	}
}
