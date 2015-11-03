/**
 * Copyright DataStax, Inc.
 *
 * Please see the included license file for details.
 */
package com.datastax.junit.remote;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Remote.class)
public class RemoteTest
{

    @Test
    public void test1() {
        System.out.println("test1");
    }

    @Test
    public void test2() {
        System.out.println("test2");
    }

    @Ignore
    public void test3() {
        System.out.println("test3");
    }

}
