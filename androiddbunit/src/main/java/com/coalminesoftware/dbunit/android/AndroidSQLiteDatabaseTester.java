package com.coalminesoftware.dbunit.android;

import android.content.Context;

import org.dbunit.JdbcDatabaseTester;

import SQLite.JDBCDriver;

public class AndroidSQLiteDatabaseTester extends JdbcDatabaseTester {
	private static final String CONNECTION_URL_SCHEMA = "jdbc:sqlite:%s";
	public static final String JDBC_DRIVER_CLASS_NAME = JDBCDriver.class.getName();

	public AndroidSQLiteDatabaseTester(Context context, String databaseName) throws Exception {
		super(JDBC_DRIVER_CLASS_NAME, buildConnectionUrl(context, databaseName), null, null);
	}

	private static String buildConnectionUrl(Context context, String databaseName) {
		return String.format(CONNECTION_URL_SCHEMA,
				context.getDatabasePath(databaseName).getAbsolutePath());
	}
}
