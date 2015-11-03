/**
 * Copyright DataStax, Inc.
 *
 * Please see the included license file for details.
 */
package com.datastax.junit.remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * RMI Wrapper around standard JUnit RunNotifier
 */
public class RunNotifierFascade extends UnicastRemoteObject implements RunNotifier
{
    private final org.junit.runner.notification.RunNotifier delegate;

    public RunNotifierFascade(org.junit.runner.notification.RunNotifier delegate) throws RemoteException
    {
        this.delegate = delegate;
    }

    @Override public void fireTestRunStarted(Description description) throws RemoteException
    {
        delegate.fireTestRunStarted(description);
    }

    @Override public void fireTestRunFinished(Result result) throws RemoteException
    {
        delegate.fireTestRunFinished(result);
    }

    @Override public void fireTestStarted(Description description) throws RemoteException
    {
        delegate.fireTestStarted(description);
    }

    @Override public void fireTestFailure(Failure failure) throws RemoteException
    {
        delegate.fireTestFailure(failure);
    }

    @Override public void fireTestAssumptionFailed(Failure failure) throws RemoteException
    {
        delegate.fireTestAssumptionFailed(failure);
    }

    @Override public void fireTestIgnored(Description description) throws RemoteException
    {
        delegate.fireTestIgnored(description);
    }

    @Override public void fireTestFinished(Description description) throws RemoteException
    {
        delegate.fireTestFinished(description);
    }
}
