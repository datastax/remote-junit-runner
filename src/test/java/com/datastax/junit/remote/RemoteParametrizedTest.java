/**
 * Copyright DataStax, Inc.
 *
 * Please see the included license file for details.
 */
package com.datastax.junit.remote;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(Remote.class)
@Remote.RunWith(JUnitParamsRunner.class)
public class RemoteParametrizedTest
{
    @Parameters({
            "1, 2, 3",
            "2, 3, 5"

    })
    @Test
    public void test(int a, int b, int c)
    {
        Assert.assertEquals(c, a + b);
    }
}
