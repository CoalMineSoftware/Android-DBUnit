package com.coalminesoftware.dbunit.android.closeable;

import java.io.Closeable;
import java.io.IOException;

/**
 * Handles the boilerplate code of using a {@link Closeable}, giving the user callbacks in which to
 * open a Closeable and use the Closeable, but handling closing it.
 *
 * @param <ReturnType> Type of data returned as a result of using the Closeable. If unneeded,
 * consider using {@link Void} as the ReturnType.
 * @param <CloseableType> Closeable subclass opened by {@link #openCloseable()} and provided to
 * {@link #useCloseable(Closeable)}.
 */
public abstract class CloseableTemplate<ReturnType, CloseableType extends Closeable> {
    public ReturnType execute() throws Exception {
        CloseableType closeable = null;
        try {
            closeable = openCloseable();

            return useCloseable(closeable);
        } finally {
            if(closeable != null) {
                closeable.close();
            }
        }
    }

    protected abstract ReturnType useCloseable(CloseableType closeable) throws Exception;
    protected abstract CloseableType openCloseable() throws IOException;
}
