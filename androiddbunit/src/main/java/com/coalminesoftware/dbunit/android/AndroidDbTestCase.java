package com.coalminesoftware.dbunit.android;

import android.content.Context;
import android.content.res.Resources;
import android.test.InstrumentationTestCase;
import android.test.IsolatedContext;
import android.test.RenamingDelegatingContext;
import android.test.mock.MockContentResolver;
import android.test.mock.MockContext;

import org.dbunit.DBTestCase;
import org.dbunit.IDatabaseTester;
import org.dbunit.IOperationListener;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;

import java.io.File;

public abstract class AndroidDbTestCase extends InstrumentationTestCase {
	private static final String FILENAME_PREFIX = "dbunit.";
    private static final String SQLITE_DATABASE_FILE_EXTENSION = ".sqlite";

    private IsolatedContext databaseContext;
    private final DelegateDbTestCase dbTestCase = new DelegateDbTestCase();
    private String databaseFilename;

    /**
     * Creates a test case that will use a database with a name generated based on the test class's
     * simple name.
     *
     * @see Class#getSimpleName()
     */
    public AndroidDbTestCase() {
        databaseFilename = generateDefaultDatabaseFilename();
    }

    /**
     * Creates a test case that will use the given filename for the test database.
     */
    public AndroidDbTestCase(String databaseFilename) {
        this.databaseFilename = databaseFilename;
    }

    protected IDatabaseTester newDatabaseTester() throws Exception {
        return new AndroidSQLiteDatabaseTester(getDatabaseContext(), getDatabaseName());
    }

	/**
     * @see DBTestCase#getDataSet()
     */
    protected abstract IDataSet getDataSet() throws Exception;

    /**
     * @return The filename to be used for the test database. The value is set during construction.
     */
    protected String getDatabaseName() {
        return databaseFilename;
    }

    private String generateDefaultDatabaseFilename() {
        return getClass().getSimpleName() + SQLITE_DATABASE_FILE_EXTENSION;
    }

    /**
     * Creates the database that will be used for testing. The table will later be populated with
     * the data provided by {@link #getDataSet()}.
     */
    protected abstract void onCreateDatabase(Context context);

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
        IDatabaseConnection connection = dbTestCase.callGetConnection();

        // SQLite.JDBC only accepts fetch size 1.  See http://dbunit.sourceforge.net/properties.html
        connection.getConfig().setProperty("http://www.dbunit.org/properties/fetchSize", 1);

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
            return AndroidDbTestCase.this.newDatabaseTester();
        }

        @Override
        protected IDataSet getDataSet() throws Exception {
            return AndroidDbTestCase.this.getDataSet();
        }

        @Override
        public void setUpDatabaseConfig(DatabaseConfig config) {
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

        // This public method is an unfortunate necessity, to give the enclosing class a way to call the private
        // getConnection() method, which is final and can't be overridden with a less-restrictive access modifier like
        // the rest of these methods.
        public IDatabaseConnection callGetConnection() throws Exception {
            return getConnection();
        }
    }

    private class DirectoryPrefixingMockContext extends MockContext {
        private static final String	DIRECTORY_PREFIX = "dbunit_";

		@Override
        public Resources getResources() {
            return getInstrumentation().getTargetContext().getResources();
        }

        @Override
        public File getDir(String name, int mode) {
            // Name the directory differently than the one created through the regular Context
            return getInstrumentation().getTargetContext().getDir(DIRECTORY_PREFIX + name, mode);
        }

        @Override
        public Context getApplicationContext() {
            return this;
        }
    }
}
