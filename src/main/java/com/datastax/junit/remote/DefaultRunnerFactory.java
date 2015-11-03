/**
 * Copyright DataStax, Inc.
 *
 * Please see the included license file for details.
 */
package com.datastax.junit.remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;

/**
 * Default runner factory uses standard JUnit mechanism to construct runners
 */
public class DefaultRunnerFactory extends UnicastRemoteObject implements RunnerFactory
{
    public DefaultRunnerFactory() throws RemoteException
    {
    }

    @Override public Runner create(String runnerClassName, String testClassName) throws RemoteException
    {
        try
        {
            return new RunnerFascade(new RunnerBuilder(true).build(
                    (Class<? extends org.junit.runner.Runner>) Class.forName(runnerClassName), Class.forName(testClassName)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private class RunnerBuilder extends AllDefaultPossibilitiesBuilder {

        public RunnerBuilder(boolean canUseSuiteMethod)
        {
            super(canUseSuiteMethod);
        }

        public org.junit.runner.Runner build(Class<? extends org.junit.runner.Runner> runnerClass, Class<?> testClass) throws Exception {
            return annotatedBuilder().buildRunner(runnerClass, testClass);
        }
    }
}
