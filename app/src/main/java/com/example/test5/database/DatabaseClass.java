package com.example.test5.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseClass extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "planner.db";

	public DatabaseClass(Context context) {
		super(context, DATABASE_NAME, null, 3);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		PlannerTable.onCreate(database);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		PlannerTable.onUpgrade(database, oldVersion, newVersion);
	}
}
