/**
 * Copyright DataStax, Inc.
 *
 * Please see the included license file for details.
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
