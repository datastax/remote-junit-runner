/**
 * Copyright DataStax, Inc.
 *
 * Please see the included license file for details.
 */
package com.datastax.junit.remote;

import java.rmi.RemoteException;

import org.junit.runner.Description;

import com.healthmarketscience.rmiio.RemoteOutputStream;

/**
 * JUnit runner that can be exposed via Java RMI
 */
public interface Runner extends java.rmi.Remote
{
    public Description getDescription() throws RemoteException;

    public void run(RunNotifier runNotifier, RemoteOutputStream out, RemoteOutputStream err) throws RemoteException;

    public int testCount() throws RemoteException;

    public void filter(Filter filter) throws RemoteException;

}
