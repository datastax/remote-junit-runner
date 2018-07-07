/*
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

    @Override public void fireTestRunStarted(Description description)
    {
        delegate.fireTestRunStarted(description);
    }

    @Override public void fireTestRunFinished(Result result)
    {
        delegate.fireTestRunFinished(result);
    }

    @Override public void fireTestStarted(Description description)
    {
        delegate.fireTestStarted(description);
    }

    @Override
    public void fireTestFailure(Failure failure)
    {
        // wrap in a try-catch block to capture a potential Exception that would cause a failure to be swallowed
        // and not reported because the code was unable to perform fireTestFailure(). See DSP-15784 for details.
        try
        {
            delegate.fireTestFailure(failure);
        } catch (Exception e1)
        {
            delegate.fireTestFailure(JUnitFailureUtil.repackFailure(e1, failure));
        }
    }

    @Override
    public void fireTestAssumptionFailed(Failure failure)
    {
        // wrap in a try-catch block to capture a potential Exception that would cause a failure to be swallowed
        // and not reported.
        try
        {
            delegate.fireTestAssumptionFailed(failure);
        } catch (Exception e1)
        {
            try
            {
                delegate.fireTestAssumptionFailed(JUnitFailureUtil.repackFailure(e1, failure));
            } catch (Exception e2)
            {
                throw new RuntimeException(e1.toString(), e2);
            }
        }
    }

    @Override public void fireTestIgnored(Description description)
    {
        delegate.fireTestIgnored(description);
    }

    @Override public void fireTestFinished(Description description)
    {
        delegate.fireTestFinished(description);
    }
}
