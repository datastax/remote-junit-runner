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
import java.util.HashMap;
import java.util.Map;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;

/**
 * JUnit Filter around remote filter so that is again usable by JUnit runner on the remote side
 */
public class FilterAdapter extends org.junit.runner.manipulation.Filter
{
    private final Filter delegate;

    private final Map<Description, Boolean> shouldRunCache = new HashMap<>();

    public FilterAdapter(Filter delegate)
    {
        this.delegate = delegate;
    }

    public void apply(Object child) throws NoTestsRemainException
    {
        if (!(child instanceof Filterable)) {
            return;
        }
        Filterable filterable = (Filterable) child;
        filterable.filter(this);
    }

    @Override public org.junit.runner.manipulation.Filter intersect(final org.junit.runner.manipulation.Filter second)
    {
        if (second == this || second == ALL) {
            return this;
        }
        final org.junit.runner.manipulation.Filter first = this;
        return new org.junit.runner.manipulation.Filter() {
            @Override
            public boolean shouldRun(Description description) {
                return first.shouldRun(description)
                        && second.shouldRun(description);
            }

            @Override
            public String describe() {
                return first.describe() + " and " + second.describe();
            }
        };
    }

    @Override public boolean shouldRun(Description description)
    {
        try
        {
            Boolean result = shouldRunCache.get(description);
            if (result == null) {
                result = delegate.shouldRun(description);
                shouldRunCache.put(description, result);
            }
            return result;
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override public String describe()
    {
        try
        {
            return delegate.describe();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
