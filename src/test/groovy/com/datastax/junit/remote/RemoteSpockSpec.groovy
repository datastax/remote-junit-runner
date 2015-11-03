/*
 * Copyright DataStax, Inc.
 *
 * Please see the included license file for details.
 */
package com.datastax.junit.remote

import org.junit.runner.RunWith
import org.spockframework.runtime.Sputnik
import spock.lang.Specification

@RunWith(Remote)
@Remote.RunWith(Sputnik)
class RemoteSpockSpec extends Specification
{

    def test() {
        expect:
        true
    }
}
