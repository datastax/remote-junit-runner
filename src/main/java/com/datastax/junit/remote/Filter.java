/**
 * Copyright DataStax, Inc.
 *
 * Please see the included license file for details.
 */
package com.datastax.junit.remote;

import java.rmi.RemoteException;

import org.junit.runner.Description;

/**
 * Exposing JUnit filter API via RMI
 */
public interface Filter extends java.rmi.Remote
{
    public abstract boolean shouldRun(Description description) throws RemoteException;

    public abstract String describe() throws RemoteException;

}
