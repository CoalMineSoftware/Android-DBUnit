package com.coalminesoftware.dbunit.android.provider;

import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.coalminesoftware.dbunit.android.dataset.AndroidDataSetUtils;
import com.coalminesoftware.dbunit.android.AndroidDbTestCase;
import com.coalminesoftware.dbunit.android.dataset.AndroidFilteredDataSet;
import com.coalminesoftware.dbunit.android.example.test.R;

import org.dbunit.Assertion;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;

public class WidgetContentProviderDatabaseTest extends AndroidDbTestCase {
	public WidgetContentProviderDatabaseTest() {
		super("widget.sqlite"); // The DB file will be created in onCreateDatabase() if it doesn't already exist.
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return AndroidDataSetUtils.createXmlDataSet(
				getInstrumentation().getContext(),
				R.raw.initial_dataset);
	}

	@Override
	protected void onCreateDatabase(Context context) {
		// Only create the database here.  It's populated by DbUnit with the data returned by getDataSet().
		// Commands MUST be separated by line breaks, not spaces.
		String CREATION_SQL = "DROP TABLE IF EXISTS widget;\n"
				+ "CREATE TABLE widget(_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT);\n";

		DatabaseUtils.createDbFromSqlStatements(context, getDatabaseName(), 1, CREATION_SQL);
	}

	public void testTableCreation() throws Exception {
		// Fetch the freshly (re-)initialized database
		IDataSet actualDataSet = getConnection().createDataSet();
		ITable actualTable = actualDataSet.getTable("widget");

		// Load expected data from the XML dataset
		IDataSet expectedDataSet = getDataSet();
		ITable expectedTable = expectedDataSet.getTable("widget");

		// Data sets can be compared as a whole but the actual data set needs to be
		// filtered since it includes tables used internally by SQLite and Android.
		IDataSet filteredActualDataSet = new AndroidFilteredDataSet(actualDataSet);
		Assertion.assertEquals(expectedDataSet, filteredActualDataSet);

		// A real world test would more likely take some action and then assert
		// that the database's contents match a new data set, not the original.

		// This assertion from AndroidJUnit4's example test compares a single table.
		Assertion.assertEquals(expectedTable, actualTable);
	}

	public void testUpgrade() throws Exception {
		// Do database-y stuff with application code.
		DatabaseHelper databaseHelper = new DatabaseHelper(getDatabaseContext(), getDatabaseName()); // It's unclear what the difference is between getContext() and getDatabaseContext() but AndroidDbTestCase uses the mock context as the database context.  And it's this context that's passed to onCreateDatabase().
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		databaseHelper.upgradeDatabase(database);

		// Now make sure it worked.
		IDataSet actualDataSet = getConnection().createDataSet();

		assertNotNull("Expected some_new_table to be created when upgrading database.",
				actualDataSet.getTable("some_new_table"));

		// This will throw an exception if the column doesn't exist, so there's not really anything to assert.
		actualDataSet.getTable("widget").getTableMetaData().getColumnIndex("description");
	}
}


