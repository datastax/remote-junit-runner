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

import org.junit.runner.notification.Failure;

public class JUnitFailureUtil
{

    /**
     * Replace the input Throwable with RuntimeException and a reason
     *
     * @param msg : Reason to replace the Throwable
     * @param th  : Throwable to be replaced
     * @return RuntimeException
     */
    private static RuntimeException replaceException(String msg, Throwable th)
    {
        RuntimeException rte = new RuntimeException(String.format("Reason for replacing: %s\nReplaced exception is: %s",
                msg, th.toString()));
        rte.setStackTrace(th.getStackTrace());
        return rte;
    }

    /**
     * Repack the input Failure because of the input exception
     *
     * @param e1      : Exception that causes the failure unable to be fired
     * @param failure : The failure that needs to be repacked
     * @return
     */
    public static Failure repackFailure(Exception e1, Failure failure)
    {
        Failure fail1 = new Failure(failure.getDescription(),
                JUnitFailureUtil.replaceException(e1.toString(), failure.getException()));
        return fail1;
    }

}