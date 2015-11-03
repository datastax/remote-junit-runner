/**
 * Copyright DataStax, Inc.
 *
 * Please see the included license file for details.
 */
package com.datastax.junit.remote;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Remote.class)
@Remote.RunWith(Suite.class)
@Suite.SuiteClasses({RemoteSuite.Test1.class})
public class RemoteSuite
{

    public static class Test1
    {
        @Test
        public void test1()
        {
            System.out.println("test1");
        }
    }
}
