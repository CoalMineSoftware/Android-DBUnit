package com.coalminesoftware.dbunit.android.dataset;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.NoSuchTableException;

/**
 * An empty data set, useful as a stating point for testing code that initializes a uninitialized
 * database with no tables, like Android SQLiteOpenHelper implementations.
 */
public class EmptyDataSet implements IDataSet {
    public static final IDataSet INSTANCE = new EmptyDataSet();
    private static final ITableIterator EMPTY_TABLE_ITERATOR = buildEmptyTableIterator();

    private static ITableIterator buildEmptyTableIterator() {
        return new ITableIterator() {
            @Override
            public boolean next() throws DataSetException {
                return false;
            }

            @Override
            public ITableMetaData getTableMetaData() throws DataSetException {
                throw new NoSuchTableException("Cannot retrieve table metadata from an empty iterator.");
            }

            @Override
            public ITable getTable() throws DataSetException {
                throw new NoSuchTableException("Cannot retrieve table from an empty iterator.");
            }
        };
    }

    private EmptyDataSet() { }

    @Override
    public String[] getTableNames() throws DataSetException {
        return new String[0];
    }

    @Override
    public ITableMetaData getTableMetaData(String tableName) throws DataSetException {
        throw new NoSuchTableException("No tables are defined in data set.");
    }

    @Override
    public ITable getTable(String tableName) throws DataSetException {
        throw new NoSuchTableException("No tables are defined in data set.");
    }

    @Override
    public ITable[] getTables() throws DataSetException {
        return new ITable[0];
    }

    @Override
    public ITableIterator iterator() throws DataSetException {
        return EMPTY_TABLE_ITERATOR;
    }

    @Override
    public ITableIterator reverseIterator() throws DataSetException {
        return EMPTY_TABLE_ITERATOR;
    }

    @Override
    public boolean isCaseSensitiveTableNames() {
        return false;
    }
}
