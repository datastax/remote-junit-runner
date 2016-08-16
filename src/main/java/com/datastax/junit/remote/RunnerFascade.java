/**
 * Copyright (C) 2016 DataStax Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.datastax.junit.remote;

import java.io.IOException;
import java.io.PrintStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import com.healthmarketscience.rmiio.RemoteOutputStream;
import com.healthmarketscience.rmiio.RemoteOutputStreamClient;

/**
 * RMI Wrapper around standard JUnit runner
 */
public class RunnerFascade extends UnicastRemoteObject implements Runner
{

    private final org.junit.runner.Runner delegate;

    public RunnerFascade(org.junit.runner.Runner delegate) throws RemoteException
    {
        this.delegate = delegate;
    }

    @Override public Description getDescription() throws RemoteException
    {
        return delegate.getDescription();
    }

    @Override public int testCount() throws RemoteException
    {
        return delegate.testCount();
    }

    @Override public void run(RunNotifier remoteRunNotifier, RemoteOutputStream out, RemoteOutputStream err) throws RemoteException
    {
        RunNotifierAdapter adapter = new RunNotifierAdapter(remoteRunNotifier);
        org.junit.runner.notification.RunNotifier runNotifier = new org.junit.runner.notification.RunNotifier();
        runNotifier.addListener(adapter);
        PrintStream stdOut = System.out;
        PrintStream stdErr = System.err;
        try
        (
            PrintStream pout = new PrintStream(RemoteOutputStreamClient.wrap(out), true);
            PrintStream perr = new PrintStream(RemoteOutputStreamClient.wrap(err), true);
        )
        {
            System.setOut(pout);
            System.setErr(perr);
            delegate.run(runNotifier);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        } finally
        {
            System.setOut(stdOut);
            System.setErr(stdErr);
        }
    }

    @Override
    public void filter(Filter filter) throws  RemoteException
    {
        if (delegate instanceof Filterable)
        {
            try
            {
                ((Filterable) delegate).filter(new FilterAdapter(filter));
            } catch (NoTestsRemainException e)
            {
                throw new RemoteException("notestsremain", e);
            }
        }
    }

    private static class RunNotifierAdapter extends RunListener
    {
        private final RunNotifier delegate;

        public RunNotifierAdapter(RunNotifier delegate)
        {
            this.delegate = delegate;
        }

        @Override public void testRunStarted(Description description) throws Exception
        {
            delegate.fireTestRunStarted(description);
        }

        @Override public void testRunFinished(Result result) throws Exception
        {
            flushStreams();
            delegate.fireTestRunFinished(result);
        }

        @Override public void testStarted(Description description) throws Exception
        {
            delegate.fireTestStarted(description);
        }

        @Override public void testFinished(Description description) throws Exception
        {
            flushStreams();
            delegate.fireTestFinished(description);
        }

        @Override public void testFailure(Failure failure) throws Exception
        {
            flushStreams();
            delegate.fireTestFailure(failure);
        }

        @Override public void testAssumptionFailure(Failure failure)
        {
            try
            {
                flushStreams();
                delegate.fireTestAssumptionFailed(failure);
            } catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }

        @Override public void testIgnored(Description description) throws Exception
        {
            flushStreams();
            delegate.fireTestIgnored(description);
        }

        private void flushStreams() throws Exception
        {
            System.out.flush();
            System.err.flush();
        }
    }
}
