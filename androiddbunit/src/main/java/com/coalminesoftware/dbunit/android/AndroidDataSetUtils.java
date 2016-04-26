package com.coalminesoftware.dbunit.android;

import android.app.Instrumentation;
import android.content.Context;
import android.test.InstrumentationTestCase;

import com.coalminesoftware.dbunit.android.closeable.CloseableTemplate;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.dbunit.dataset.xml.XmlDataSet;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.InputStream;

/** Set of Android-specific helper methods for creating {@link IDataSet}s from local resources. */
public class AndroidDataSetUtils {
    private AndroidDataSetUtils() { }

    /**
     * Creates a {@link FlatXmlDataSet} from the raw resource with the given ID. When provided with
     * the {@link Context} from an {@link android.app.Instrumentation} instance, the referenced
     * resource is expected to be located in the project-under-test's androidTest/res/raw directory.
     *
     * @see InstrumentationTestCase#getInstrumentation()
     * @see Instrumentation#getContext()
     */
    public static IDataSet getFlatXmlDataSet(final Context instrumentationContext, final int rawResourceId) throws Exception {
        return new DataSetCloseableTemplate() {
            @Override
            protected InputStream openCloseable() throws IOException {
                return instrumentationContext.getResources().openRawResource(rawResourceId);
            }

            @Override
            protected IDataSet useCloseable(InputStream inputStream) throws Exception {
                return new FlatXmlDataSet(new FlatXmlProducer(new InputSource(inputStream), false));
            }
        }.execute();
    }

    /**
     * Creates a {@link FlatXmlDataSet} for the file with the given name. File paths are relative to
     * the androidTest/resources directory.
     */
    public static IDataSet getFlatXmlDataSet(final Context context, final String resourcesFilename) throws Exception {
        return new DataSetCloseableTemplate() {
            @Override
            protected InputStream openCloseable() throws IOException {
                return context.getClassLoader().getResourceAsStream(resourcesFilename);
            }

            @Override
            protected IDataSet useCloseable(InputStream inputStream) throws Exception {
                return new FlatXmlDataSet(new FlatXmlProducer(new InputSource(inputStream), false));
            }
        }.execute();
    }

    /**
     * Creates an {@link XmlDataSet} from the raw resource with the given ID. When provided with the
     * {@link Context} from an {@link android.app.Instrumentation} instance, the referenced resource
     * is expected to be located in the project-under-test's androidTest/res/raw directory.
     *
     * @see InstrumentationTestCase#getInstrumentation()
     * @see Instrumentation#getContext()
     */
    public static IDataSet createXmlDataSet(final Context instrumentationContext,
            final int rawResourceId) throws Exception {
        return new DataSetCloseableTemplate() {
            @Override
            protected InputStream openCloseable() throws IOException {
                return instrumentationContext.getResources().openRawResource(rawResourceId);
            }

            @Override
            protected IDataSet useCloseable(InputStream inputStream) throws Exception {
                return new XmlDataSet(inputStream);
            }
        }.execute();
    }

    /**
     * Creates an {@link XmlDataSet} for the file with the given name. File paths are relative to
     * the androidTest/resources directory.
     */
    public static IDataSet createXmlDataSet(final Context context, final String resourceFilename) throws Exception {
        return new DataSetCloseableTemplate() {
            @Override
            protected InputStream openCloseable() throws IOException {
                return context.getClassLoader().getResourceAsStream(resourceFilename);
            }

            @Override
            protected IDataSet useCloseable(InputStream inputStream) throws Exception {
                return new XmlDataSet(inputStream);
            }
        }.execute();
    }

    /**
     * {@link CloseableTemplate} that opens an {@link InputStream} and returns an {@link IDataSet}.
     */
    private static  abstract class DataSetCloseableTemplate extends CloseableTemplate<IDataSet, InputStream> { }
}
