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
package com.datastax.junit.remote

import org.junit.Assert
import org.junit.Test
import org.junit.runner.manipulation.NoTestsRemainException
import org.junit.runner.notification.RunNotifier
import spock.lang.Specification

class RemoteRunnerSpec extends Specification
{

    static class Tests {

        @Test
        void fail() {
            Assert.fail("fail")
        }

        @Test
        void test1() {

        }
    }

    def runner

    def setup() {
        runner = new Remote(Tests)
    }

    def "failing remote test"() {
        given:
        def notifier = Mock(RunNotifier)

        when:
        runner.run(notifier)

        then:
        1 * notifier.fireTestFailure(_)
    }

    def "getDescription returns the description for the class under test"() {
        expect:
        runner.description.className == Tests.name
    }

    def "testCount returns the number of tests in the test class"() {
        expect:
        runner.testCount() == 2
    }

    def "filter out all tests"() {
        given:
        def filter = Mock(org.junit.runner.manipulation.Filter)
        filter.shouldRun(_) >> false

        when:
        runner.filter(filter)

        then:
        thrown NoTestsRemainException
    }

    def "run only single test"() {
        given:
        def filter = Mock(org.junit.runner.manipulation.Filter)
        filter.shouldRun( { it.displayName.startsWith('test1')}) >> true
        def notifier = Mock(RunNotifier)

        when:
        runner.filter(filter)
        runner.run(notifier)

        then:
        1 * notifier.fireTestStarted( { it.displayName.startsWith('test1')})
        1 * notifier.fireTestFinished( { it.displayName.startsWith('test1')})
    }
}
