/*
 * Copyright 2019 the original author or authors.
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

package org.gradle.testing.junit

import org.gradle.integtests.fixtures.DefaultTestExecutionResult
import org.gradle.integtests.fixtures.TargetCoverage
import org.gradle.testing.fixture.JUnitMultiVersionIntegrationSpec

import static org.gradle.testing.fixture.JUnitCoverage.JUNIT_4_LATEST
import static org.gradle.testing.fixture.JUnitCoverage.JUNIT_VINTAGE

@TargetCoverage({ JUNIT_4_LATEST + JUNIT_VINTAGE })
class Specs2IntegrationTest extends JUnitMultiVersionIntegrationSpec {

    def 'can run Specs2 tests'() {
        given:
        buildFile << """
            plugins {
                id("scala")
            }

            ${mavenCentralRepository()}

            dependencies {
                implementation 'org.scala-lang:scala-library:2.11.8'
                testImplementation 'org.specs2:specs2_2.11:3.7'
                testImplementation 'org.specs2:specs2-junit_2.11:4.7.0'
                ${getDependencyBlockContents()}
            }
        """
        file('src/test/scala/BasicSpec.scala') << '''
            import org.junit.runner.RunWith
            import org.specs2.runner.JUnitRunner
            import org.specs2.mutable.Specification

            @RunWith(classOf[JUnitRunner])
            class BasicSpec extends Specification {
              "Basic Math" >> {
                (1 + 1) mustEqual 2
              }
            }
        '''

        when:
        succeeds('test')

        then:
        new DefaultTestExecutionResult(testDirectory)
            .testClass("BasicSpec").assertTestCount(1, 0, 0)
            .assertTestPassed('Basic Math')
    }

}
