package com.coalminesoftware.dbunit.android.dataset;

import org.dbunit.database.AmbiguousTableNameException;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.filter.ExcludeTableFilter;

/** FilteredDataSet implementation that excludes both android_metadata and SQLite's sqlite_*
 * tables from the given IDataSet, allowing assertions to be made between complete data sets. */
public class AndroidFilteredDataSet extends FilteredDataSet {
	public static final String[] EXCLUDED_TABLE_NAMES = new String[] {
			"sqlite_*",
			"android_metadata"};

	public AndroidFilteredDataSet(IDataSet dataSet) throws AmbiguousTableNameException {
		super(new ExcludeTableFilter(EXCLUDED_TABLE_NAMES), dataSet);
	}
}


