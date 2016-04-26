package com.coalminesoftware.dbunit.android.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final int SCHEMA_VERSION = 1;


	public DatabaseHelper(Context context) {
		super(context, "widget.sqlite", null, SCHEMA_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL("CREATE TABLE widget(_id INTEGER, name TEXT)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		upgradeDatabase(database);
	}

	public void upgradeDatabase(SQLiteDatabase database) {
		database.execSQL("CREATE TABLE IF NOT EXISTS some_new_table(_id INTEGER NOT NULL)");
		database.execSQL("ALTER TABLE widget ADD COLUMN description TEXT");
	}
}


