package com.example.test5.contentprovider;

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import com.example.test5.database.DatabaseClass;
import com.example.test5.database.PlannerTable;

public class PlannerContentProvider extends ContentProvider {

	private DatabaseClass database;
	private static final String BASE_CONTENTPROVIDER = "com.example.test5.contentprovider";
	private static final String BASE_PATH = "planners";
	public static final Uri CONTENT_URI = Uri.parse("content://" + BASE_CONTENTPROVIDER + "/" + BASE_PATH);
	private static final int TASKS = 10;
	private static final int TASK_ID = 20;
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/planner";
	private static final UriMatcher URIMPlanner = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		URIMPlanner.addURI(BASE_CONTENTPROVIDER, BASE_PATH, TASKS);
		URIMPlanner.addURI(BASE_CONTENTPROVIDER, BASE_PATH + "/#", TASK_ID);
	}

	@Override
	public boolean onCreate() {
		database = new DatabaseClass(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		checkColumns(projection);
		queryBuilder.setTables(PlannerTable.NAME_TABLE);

		int uriType = URIMPlanner.match(uri);
		switch (uriType) {
		case TASK_ID:
			queryBuilder.appendWhere(PlannerTable.COLUMN_ID + "=" + uri.getLastPathSegment());
			break;
		case TASKS:
			break;
		default:
			throw new IllegalArgumentException("URI is bad: " + uri);
		}

		SQLiteDatabase db = database.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = URIMPlanner.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		long id = 0;
		switch (uriType) {
		case TASKS:
			id = sqlDB.insert(PlannerTable.NAME_TABLE, null, values);
			break;
		default:
			throw new IllegalArgumentException("URI is bad: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(BASE_PATH + "/" + id);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = URIMPlanner.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsDeleted = 0;
		switch (uriType) {
		case TASKS:
			rowsDeleted = sqlDB.delete(PlannerTable.NAME_TABLE, selection,
					selectionArgs);
			break;
		case TASK_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(PlannerTable.NAME_TABLE,
						PlannerTable.COLUMN_ID + "=" + id, null);
			} else {
				rowsDeleted = sqlDB.delete(PlannerTable.NAME_TABLE,
						PlannerTable.COLUMN_ID + "=" + id + " and " + selection,
						selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("URI is bad: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		int uriType = URIMPlanner.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int updateKent = 0;
		switch (uriType) {
		case TASK_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				updateKent = sqlDB.update(PlannerTable.NAME_TABLE, values,
						PlannerTable.COLUMN_ID + "=" + id, null);
			} else {
				updateKent = sqlDB.update(PlannerTable.NAME_TABLE, values,
						PlannerTable.COLUMN_ID + "=" + id + " and " + selection,
						selectionArgs);
			}
			break;
		case TASKS:
			updateKent = sqlDB.update(PlannerTable.NAME_TABLE, values, selection, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("URI is bad: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return updateKent;
	}

	private void checkColumns(String[] projection) {
		String[] available = { PlannerTable.COLUMN_URGENT,
				PlannerTable.COLUMN_BRIEF, PlannerTable.COLUMN_DESCRIPTION,
				PlannerTable.COLUMN_ID };
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException("Unknown columns in projection");
			}
		}
	}
}
