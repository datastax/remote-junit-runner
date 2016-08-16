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
