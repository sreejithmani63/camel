/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.itest.osgi.http;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.itest.osgi.OSGiIntegrationTestSupport;
import org.apache.karaf.testing.Helper;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;

import static org.ops4j.pax.exam.CoreOptions.equinox;
import static org.ops4j.pax.exam.CoreOptions.felix;
import static org.ops4j.pax.exam.OptionUtils.combine;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.scanFeatures;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.workingDirectory;

/**
 *
 */
@RunWith(JUnit4TestRunner.class)
public class HttpTest extends OSGiIntegrationTestSupport {

    @Test
    public void testHttpGet() throws Exception {
        String reply = template.requestBody("http://localhost:9081/foo", null, String.class);
        assertEquals("Bye World", reply);
    }

    @Test
    public void testHttpPost() throws Exception {
        String reply = template.requestBody("http://localhost:9081/foo", "Hello World", String.class);
        assertEquals("Bye World", reply);
    }

    @Test
    @Ignore("Requires online internet for testing")
    public void testHttpGoogle() throws Exception {
        String reply = template.requestBody("http://www.google.se", null, String.class);
        assertNotNull(reply);
        log.info(reply);
    }

    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                from("jetty:http://0.0.0.0:9081/foo")
                    .transform(constant("Bye World"));
            }
        };
    }

    @Configuration
    public static Option[] configure() throws Exception {
        Option[] options = combine(
            // Default karaf environment
            Helper.getDefaultOptions(
                    // this is how you set the default log level when using pax logging (logProfile)
                    Helper.setLogLevel("WARN")),
            // using the features to install the camel components
            scanFeatures(getCamelKarafFeatureUrl(),
                          "camel-core", "camel-test", "camel-jetty", "camel-http"),

            workingDirectory("target/paxrunner/"),

            felix(), equinox());

        return options;
    }

}
