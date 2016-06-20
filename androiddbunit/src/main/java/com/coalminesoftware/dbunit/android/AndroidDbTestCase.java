package com.coalminesoftware.dbunit.android;

import android.content.Context;
import android.content.res.Resources;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.test.InstrumentationTestCase;
import android.test.IsolatedContext;
import android.test.RenamingDelegatingContext;
import android.test.mock.MockContentResolver;
import android.test.mock.MockContext;

import com.coalminesoftware.dbunit.android.dataset.AndroidFilteredDataSet;

import org.dbunit.DBTestCase;
import org.dbunit.IDatabaseTester;
import org.dbunit.IOperationListener;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.SortedDataSet;
import org.dbunit.operation.DatabaseOperation;

import java.io.File;

public abstract class AndroidDbTestCase extends InstrumentationTestCase {
	private static final String FILENAME_PREFIX = "dbunit_";

    private IsolatedContext databaseContext;
    private final DelegateDbTestCase dbTestCase = new DelegateDbTestCase();
    private String databaseName;
    private IDatabaseConnection connection;

    /**
     * Creates a test case that will use the given filename for the test database.
     */
    public AndroidDbTestCase(String databaseName) {
        this.databaseName = databaseName;
    }

	/**
     * @see DBTestCase#getDataSet()
     */
    protected abstract IDataSet getDataSet() throws Exception;

    /**
     * @return The filename to be used for the test database. The value is set during construction.
     */
    protected String getDatabaseName() {
        return databaseName;
    }

    protected SQLiteDatabase getDatabase() {
        String databasePath = getDatabaseContext().getDatabasePath(databaseName).getAbsolutePath();
        return SQLiteDatabase.openDatabase(databasePath, null, 0);
    }

    /**
     * Creates the database that will be used for testing. The table will later be populated with
     * the data provided by {@link #getDataSet()}.
     */
    protected abstract void onCreateDatabase(Context context);

    /**
     * Convenience method for creating the schema in the database that will be used for testing.
     *
     * @param schemaRevision The schema version number to initialize the database with. This is the
     * value a SQLiteOpenHelper uses to determine whether the database being opened needs to be
     * upgraded via its
     * {@link android.database.sqlite.SQLiteOpenHelper#onUpgrade(SQLiteDatabase, int, int)
     * implementation.
     * @param schemaCreationSql Comma-separated SQL commands to create tables, views, indexes, etc.
     */
    protected void createDatabase(int schemaRevision, String schemaCreationSql) {
        DatabaseUtils.createDbFromSqlStatements(getDatabaseContext(),
                getDatabaseName(),
                schemaRevision,
                schemaCreationSql);
    }

	/**
     * @see DBTestCase#getDatabaseTester()
     */
    protected IDatabaseTester getDatabaseTester() throws Exception {
        return dbTestCase.getDatabaseTester();
    }

	/**
     * @see DBTestCase#getSetUpOperation()
     */
    protected DatabaseOperation getSetUpOperation() throws Exception {
        return dbTestCase.getSetUpOperation();
    }

	/**
     * @see DBTestCase#getTearDownOperation()
     */
    protected DatabaseOperation getTearDownOperation() throws Exception {
        return dbTestCase.getTearDownOperation();
    }

	/**
     * @see DBTestCase#getOperationListener()
     */
    protected IOperationListener getOperationListener() {
        return dbTestCase.getOperationListener();
    }

    /**
     * @return A sorted data set representing the database's state when called, built from the
     * current database connection and filtered of metadata tables used by SQLite and Android.
     *
     * @see AndroidFilteredDataSet
     */
    protected IDataSet createFilteredConnectionDataSet() throws Exception {
        return createFilteredConnectionDataSet(true);
    }

    /**
     * @return A data set representing the database's state when called, built from the current
     * database connection and filtered of metadata tables used by SQLite and Android.
     *
     * @param sorted Whether to return a {@link SortedDataSet}.
     *
     * @see AndroidFilteredDataSet
     */
    protected IDataSet createFilteredConnectionDataSet(boolean sorted) throws Exception {
        IDataSet dataSet = new AndroidFilteredDataSet(getConnection().createDataSet());

        return sorted ? new SortedDataSet(dataSet) : dataSet;
    }

    /**
     * Replaces the database's existing data with the given data set.
     */
    protected void replaceDatabaseDataSet(IDataSet dataSet) throws Exception {
        DatabaseOperation.CLEAN_INSERT.execute(getConnection(), dataSet);
    }

    @Override
	protected void setUp() throws Exception {
        super.setUp();

        RenamingDelegatingContext targetContextWrapper = new RenamingDelegatingContext(
        		new DirectoryPrefixingMockContext(), // The context that most methods are delegated to
                getInstrumentation().getTargetContext(), // The context that file methods are delegated to
                FILENAME_PREFIX);

        databaseContext = new IsolatedContext(new MockContentResolver(), targetContextWrapper);

        onCreateDatabase(databaseContext);

        dbTestCase.setUp();
    }

    @Override
	protected void tearDown() throws Exception {
        dbTestCase.tearDown();
        super.tearDown();
    }

    public IDatabaseConnection getConnection() throws Exception {
        if(connection == null) {
            connection = dbTestCase.callGetConnection();
        }

        return connection;
    }

    /**
     * @return The {@link IsolatedContext} created during initialization for use when interacting
     * with the database.
     */
    public IsolatedContext getDatabaseContext() {
        return databaseContext;
    }

    /**
     * DBTestCase subclass to which AndroidDbTestCase delegates the necessary JUnit lifecycle calls.
     * This subclass also overrides some methods simply for the sake of making them public, so that
     * otherwise protected or private methods can be called by the enclosing class.
     */
    private class DelegateDbTestCase extends DBTestCase {
        @Override
        protected IDatabaseTester newDatabaseTester() throws Exception {
            return new AndroidSQLiteDatabaseTester(getDatabaseContext(), getDatabaseName());
        }

        @Override
        protected IDataSet getDataSet() throws Exception {
            return AndroidDbTestCase.this.getDataSet();
        }

        @Override
        public void setUpDatabaseConfig(DatabaseConfig config) {
            // SQLite.JDBC only accepts fetch size 1.  See http://dbunit.sourceforge.net/properties.html
            config.setProperty("http://www.dbunit.org/properties/fetchSize", 1);

            super.setUpDatabaseConfig(config);

        }

        @Override
        public IDatabaseTester getDatabaseTester() throws Exception {
            return super.getDatabaseTester();
        }

        @Override
        public DatabaseOperation getSetUpOperation() throws Exception {
            return super.getSetUpOperation();
        }

        @Override
        public DatabaseOperation getTearDownOperation() throws Exception {
            return super.getTearDownOperation();
        }

        @Override
        public void setUp() throws Exception {
            super.setUp();
        }

        @Override
        public void tearDown() throws Exception {
            super.tearDown();
        }

        @Override
        public IOperationListener getOperationListener() {
            return super.getOperationListener();
        }

        // This public method is an unfortunate necessity, to give the enclosing class a way to call
        // the private getConnection() method, which is final and can't be overridden with a less-
        // restrictive access modifier like the rest of these methods.
        public IDatabaseConnection callGetConnection() throws Exception {
            return getConnection();
        }
    }

    // TODO Verify whether this is necessary since it's being provided to a RenamingDelegatingContext.
    private class DirectoryPrefixingMockContext extends MockContext {
		@Override
        public Resources getResources() {
            return getInstrumentation().getTargetContext().getResources();
        }

        @Override
        public File getDir(String name, int mode) {
            // Name the directory differently than the one created through the regular Context
            return getInstrumentation().getTargetContext().getDir(FILENAME_PREFIX + name, mode);
        }

        @Override
        public Context getApplicationContext() {
            return this;
        }
    }
}
