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

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.rmi.transport.proxy.RMIMasterSocketFactory;

/**
 * Server starts services required to the remote JUnit test execution. Under the hood,
 * it uses good old Java RMI for communication with the client JVM.
 */
public class RemoteTestServer
{
    public static final int DEFAULT_PORT = 4567;
    public static final String NAME = "remoteRunnerFactory";

    private final static Logger log = LoggerFactory.getLogger(RemoteTestServer.class);

    private final int port;
    private InetAddress bindAddrees;
    private Registry registry;

    /**
     *
     * @param port where RMI registry should listen at
     */
    public RemoteTestServer(int port)
    {
        this.port = port;
    }

    /**
     * Creates the test infrastructure having RMI registry on the port 4567
     */
    public RemoteTestServer()
    {
        this(DEFAULT_PORT);
    }

    /**
     *
     * @param socketAddress RMI registry will listen at the given socket
     */
    public RemoteTestServer(InetSocketAddress socketAddress)
    {
        this(socketAddress.getPort());
        this.bindAddrees = socketAddress.getAddress();
    }

    /**
     * Starts the test services
     */
    public void start() throws Exception
    {
        if (bindAddrees == null) {
           registry = LocateRegistry.createRegistry(port);
        } else {
            SocketFactory socketFactory = new SocketFactory(bindAddrees);
            registry = LocateRegistry.createRegistry(port, null, socketFactory);
        }
        RunnerFactory factory = new DefaultRunnerFactory();

        registry.rebind(NAME, factory);
        log.info("Remote Test Runner service started, the RMI service registry listening at {}:{}", (bindAddrees == null ? "0.0.0.0": bindAddrees), port);
    }

    /**
     * Stops the services
     */
    public void stop() throws Exception
    {
        registry.unbind(NAME);
        UnicastRemoteObject.unexportObject(registry, true);
    }

    public static void main(String[] args)
    {
        try
        {
            new RemoteTestServer().start();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static class SocketFactory implements RMIServerSocketFactory
    {
        private final InetAddress bindAddress;

        public SocketFactory(InetAddress bindAddress)
        {
            this.bindAddress = bindAddress;
        }

        @Override public ServerSocket createServerSocket(int port) throws IOException
        {
            return new ServerSocket(port, 50, bindAddress);
        }
    }
}
