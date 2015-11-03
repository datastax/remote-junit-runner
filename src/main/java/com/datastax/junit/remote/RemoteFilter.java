/**
 * Copyright DataStax, Inc.
 *
 * Please see the included license file for details.
 */
package com.datastax.junit.remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.junit.runner.Description;

public class RemoteFilter extends UnicastRemoteObject implements Filter
{

    private final org.junit.runner.manipulation.Filter delegate;

    public RemoteFilter(org.junit.runner.manipulation.Filter delegate) throws RemoteException
    {
        this.delegate = delegate;
    }

    @Override
    public boolean shouldRun(Description description) throws RemoteException
    {
        return delegate.shouldRun(description);
    }

    @Override public String describe() throws RemoteException
    {
        return delegate.describe();
    }
}
