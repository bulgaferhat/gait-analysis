/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.util.internal

import org.gradle.internal.os.OperatingSystem
import spock.lang.Specification

import static PathTraversalChecker.isUnsafePathName
import static org.junit.Assume.assumeFalse

class PathTraversalCheckerTest extends Specification {

    def "identifies potentially unsafe zip entry names"() {
        setup:
        assumeFalse(
            ": is only unsafe on Windows systems",
            unsafePath.contains(':') && !isWindows()
        )

        expect:
        isUnsafePathName(unsafePath)
        !isUnsafePathName(safePath)

        where:
        unsafePath     | safePath
        "/"            | "foo/"
        "\\"           | "foo\\"
        "/foo"         | "foo"
        "\\foo"        | "foo"
        "foo/.."       | "foo/bar"
        "foo\\.."      | "foo\\bar"
        "C:/foo"       | "foo"
        "../foo"       | "..foo"
        "..\\foo"      | "..foo"
        "foo/../bar"   | "foo/..bar"
        "foo\\..\\bar" | "foo\\..bar"
    }

    private static boolean isWindows() {
        OperatingSystem.current().isWindows()
    }
}
