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

import com.thoughtworks.selenium.Selenium;
import org.junit.Test;
import org.testatoo.cartridge.html4.HtmlComponentFactory;
import org.testatoo.cartridge.html4.evaluator.selenium.ConditionChain;
import org.testatoo.cartridge.html4.evaluator.selenium.SeleniumHtmlEvaluator;
import org.testatoo.cartridge.html4.evaluator.selenium.SeleniumProvider;
import org.testatoo.cartridge.html4.evaluator.selenium.condition.PageLoaded;
import org.testatoo.config.cartridge.EvaluatorListenerAdapter;
import org.testatoo.config.testatoo.Testatoo;

import static org.hamcrest.Matchers.is;
import static org.testatoo.container.TestatooContainer.JETTY;
import static org.testatoo.core.ComponentFactory.*;

public final class F5SeleniumWithCustomCartridgeTest {

    @Test
    public void test() throws Exception {

        Testatoo testatoo = Testatoo.configure(new AbstractTestatooModule() {
            @Override
            protected void configure() {
                install(commonModule());

                Provider<Selenium> provider = createSeleniumSession()
                        .website("http://127.0.0.1:7896/")
                        .browser("*mock")
                        .serverHost("127.0.0.1")
                        .serverPort(4444)
                        .build();

                seleniumSessions()
                        .register(provider)
                        .scope(Scope.TEST_CLASS)
                        .withTimeout(20000)
                        .add(new EvaluatorListenerAdapter<Selenium>() {
                            @Override
                            public void afterStart(final Selenium session) {
                                SeleniumProvider seleniumProvider = new SeleniumProvider() {
                                    @Override
                                    public Selenium get() {
                                        return session;
                                    }
                                };
                                SeleniumHtmlEvaluator evaluator = new SeleniumHtmlEvaluator(seleniumProvider);
                                ConditionChain conditionChain = ConditionChain.create();
                                conditionChain.addCondition(new PageLoaded(seleniumProvider));
                                evaluator.setWaitingCondition(conditionChain);
                                HtmlComponentFactory.get().use(evaluator);
                            }
                        });
            }
        });

        testatoo.start();
        page().open("/index.xhtml");
        assertThat(textfield("lang").value(), is("x"));
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
