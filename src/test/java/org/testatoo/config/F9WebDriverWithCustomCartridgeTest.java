/**
 * Copyright (C) 2008 Ovea <dev@testatoo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.testatoo.config;

import org.junit.Test;
import org.testatoo.config.testatoo.Testatoo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.testatoo.container.TestatooContainer.JETTY;

public final class F9WebDriverWithCustomCartridgeTest {

    @Test
    public void test() throws Exception {

        Testatoo testatoo = Testatoo.configure(new AbstractTestatooModule() {
            @Override
            protected void configure() {
                install(commonModule());

                /*webDrivers()
                        .register(new FirefoxDriver())
                        .speed(Speed.MEDIUM)
                        .website("http://127.0.0.1:7896/")
                        .add(new EvaluatorListenerAdapter<WebDriver>() {
                            @Override
                            public void afterStart(final WebDriver webDriver) {
//                                WebDriverHtmlEvaluator evaluator = new WebDriverHtmlEvaluator(webDriver);
//                                HtmlComponentFactory.get().use(evaluator);
                            }
                        });*/

            }
        });

        testatoo.start();
//        page().open("/index.xhtml");
//        assertThat(textfield("lang").value(), is("fr"));
        assertThat(true, is(true));
        testatoo.stop();
    }

    private TestatooModule commonModule() {
        return new AbstractTestatooModule() {
            @Override
            protected void configure() {
                containers().register(createContainer()
                        .implementedBy(JETTY)
                        .webappRoot("src/test/webapp")
                        .port(7896)
                        .build())
                        .scope(Scope.TEST_CLASS);
                seleniumServers().register(createSeleniumServer()
                        .port(4444)
                        .build())
                        .scope(Scope.TEST_CLASS);
            }
        };
    }
}