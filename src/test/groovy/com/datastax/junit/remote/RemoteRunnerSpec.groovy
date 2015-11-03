/*
 * Copyright DataStax, Inc.
 *
 * Please see the included license file for details.
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
