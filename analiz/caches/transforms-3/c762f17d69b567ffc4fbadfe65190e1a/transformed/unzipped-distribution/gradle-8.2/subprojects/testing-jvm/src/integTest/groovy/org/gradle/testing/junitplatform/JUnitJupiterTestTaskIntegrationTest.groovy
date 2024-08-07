/*
 * Copyright 2023 the original author or authors.
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

package org.gradle.testing.junitplatform

import org.gradle.integtests.fixtures.TargetCoverage
import org.gradle.testing.AbstractTestTaskIntegrationTest

import static org.gradle.testing.fixture.JUnitCoverage.getJUNIT_JUPITER

@TargetCoverage({ JUNIT_JUPITER })
class JUnitJupiterTestTaskIntegrationTest extends AbstractTestTaskIntegrationTest implements JUnitJupiterMultiVersionTest {
    @Override
    String getStandaloneTestClass() {
        return testClass('MyTest')
    }

    @Override
    String testClass(String className) {
        return """
            import org.junit.jupiter.api.*;

            public class $className {
               @Test
               @Tag("MyTest\$Fast")
               public void fastTest() {
                  System.out.println(System.getProperty("java.version"));
                  Assertions.assertEquals(1,1);
               }

               @Test
               @Tag("MyTest\$Slow")
               public void slowTest() {
                  System.out.println(System.getProperty("java.version"));
                  Assertions.assertEquals(1,1);
               }
            }
        """.stripIndent()
    }
}
