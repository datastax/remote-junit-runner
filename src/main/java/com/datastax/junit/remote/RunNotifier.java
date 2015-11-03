/**
 * Copyright DataStax, Inc.
 *
 * Please see the included license file for details.
 */
package com.datastax.junit.remote;

import java.rmi.RemoteException;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * JUnit notifier that can be exposed via RMI
 */
public interface RunNotifier extends java.rmi.Remote
{
    public void fireTestRunStarted(Description description) throws RemoteException;

    public void fireTestRunFinished(Result result) throws RemoteException;

    public void fireTestStarted(Description description) throws RemoteException;

    public void fireTestFailure(Failure failure) throws RemoteException;

    public void fireTestAssumptionFailed(Failure failure) throws RemoteException;

    public void fireTestIgnored(Description description) throws RemoteException;

    public void fireTestFinished(Description description) throws RemoteException;
}
