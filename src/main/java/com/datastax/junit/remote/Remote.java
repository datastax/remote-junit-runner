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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.rmi.Naming;
import java.rmi.RemoteException;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.healthmarketscience.rmiio.RemoteOutputStream;
import com.healthmarketscience.rmiio.SimpleRemoteOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unitils.util.AnnotationUtils;

/**
 * Run JUnit tests remotely, in another JVM. Specify it using the standard JUnit @{@link org.junit.runner.RunWith} mechanism.
 */
public class Remote extends Runner implements Filterable {

    private final static Logger log = LoggerFactory.getLogger(RemoteTestServer.class);

    /**
     * Specify the host where the test are going to be executed
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Host {

        /**
         * Host name
         */
        String name() default "localhost";

        /**
         * The port where the test runner service is listening
         */
        int port() default RemoteTestServer.DEFAULT_PORT;

    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface RunWith {

        /**
         * The runner class that is going to be used for running tests on the remote side.
         */
        Class<? extends Runner> value() default BlockJUnit4ClassRunner.class;

    }

    private com.datastax.junit.remote.Runner delegate;

    protected String endpoint;
    protected Class<? extends Runner> remoteRunnerClass;

    protected Class<?> clazz;

    public Remote(Class<?> clazz) {
        this.clazz = clazz;
    }

    /**
     * Override this if you need to do some setup, before trying to run the tests
     * (e.g. launching process where the tests should be executed
     */
    public void setup() {

    }

    /**
     * Override this if you need to do some teardown, after running the tests
     * (e.g. shutting down the process where the tests were executed
     */
    public void teardown() {

    }

    protected void init() {
        RunWith runWith = AnnotationUtils.getClassLevelAnnotation(RunWith.class, clazz);
        remoteRunnerClass = runWith != null ? runWith.value() : BlockJUnit4ClassRunner.class;
        Host host = AnnotationUtils.getClassLevelAnnotation(Host.class, clazz);
        endpoint = host != null ? String.format("//{}:{}/{})", host.name(), host.port(), RemoteTestServer.NAME) :
                "//localhost:"+ RemoteTestServer.DEFAULT_PORT+"/"+ RemoteTestServer.NAME;
        setup();
    }

    @Override
    public Description getDescription() {
        ensureDelegate();
        try
        {
            return delegate.getDescription();
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run(RunNotifier notifier) {
        ensureDelegate();
        try
        {
            RemoteOutputStream out = new SimpleRemoteOutputStream(new UnclosableOutputStream(System.out));
            RemoteOutputStream err = new SimpleRemoteOutputStream(new UnclosableOutputStream(System.err));
            delegate.run(new RunNotifierFascade(notifier), out, err);
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        } finally
        {
            teardown();
        }

    }

    @Override
    public void filter(Filter filter) throws NoTestsRemainException
    {
        ensureDelegate();
        try
        {
            delegate.filter(new RemoteFilter(filter));
        } catch (RemoteException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RemoteException && cause.getMessage().startsWith("notestsremain")) {
                throw (NoTestsRemainException) cause.getCause();
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    private void ensureDelegate() {
        if (delegate == null) {
            init();
            try
            {
                RunnerFactory runnerFactory = (RunnerFactory) Naming.lookup(endpoint);
                delegate = runnerFactory.create(remoteRunnerClass.getName(), clazz.getName());
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }


}
