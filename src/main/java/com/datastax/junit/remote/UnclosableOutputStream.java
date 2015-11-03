/**
 * Copyright DataStax, Inc.
 *
 * Please see the included license file for details.
 */
package com.datastax.junit.remote;

import java.io.IOException;
import java.io.OutputStream;

/**
 * OutputStream facade that ignores the close operation.
 */
public class UnclosableOutputStream extends OutputStream
{
    private final OutputStream delegate;

    public UnclosableOutputStream(OutputStream delegate)
    {
        this.delegate = delegate;
    }

    @Override public void write(int b) throws IOException
    {
        delegate.write(b);
    }

    @Override public void write(byte[] b) throws IOException
    {
        delegate.write(b);
    }

    @Override public void write(byte[] b, int off, int len) throws IOException
    {
        delegate.write(b, off, len);
    }

    @Override public void flush() throws IOException
    {
        delegate.flush();
    }

    @Override public void close() throws IOException
    {
        // no op
    }
}
