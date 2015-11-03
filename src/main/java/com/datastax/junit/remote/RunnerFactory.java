/**
 * Copyright DataStax, Inc.
 *
 * Please see the included license file for details.
 */
package com.datastax.junit.remote;

import java.rmi.RemoteException;

/**
 * Remote runner factory
 */
public interface RunnerFactory extends java.rmi.Remote
{
    /**
     *
     * @param runnerClassName
     * @param testClassName
     * @return runner instance for executing tests remotelly
     * @throws RemoteException
     */
    public Runner create(String runnerClassName, String testClassName) throws RemoteException;
}
