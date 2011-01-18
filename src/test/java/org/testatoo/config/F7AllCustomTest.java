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

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import org.junit.Test;
import org.testatoo.cartridge.html4.HtmlComponentFactory;
import org.testatoo.cartridge.html4.evaluator.selenium.ConditionChain;
import org.testatoo.cartridge.html4.evaluator.selenium.SeleniumHtmlEvaluator;
import org.testatoo.cartridge.html4.evaluator.selenium.SeleniumProvider;
import org.testatoo.cartridge.html4.evaluator.selenium.condition.PageLoaded;
import org.testatoo.config.testatoo.Testatoo;

import static org.hamcrest.Matchers.is;
import static org.testatoo.container.TestatooContainer.JETTY;
import static org.testatoo.core.ComponentFactory.*;

public final class F7AllCustomTest {

    @Test
    public void test() throws Exception {

        Testatoo testatoo = Testatoo.configure(new AbstractTestatooModule() {
            @Override
            protected void configure() {
                install(commonModule());

                lifecycle().onStart(new Runnable() {
                    @Override
                    public void run() {

                        final Selenium session = new DefaultSelenium("127.0.0.1", 4444, "*mock", "http://127.0.0.1:7896/");
                        session.start();
                        session.setTimeout("60000");

                        SeleniumProvider seleniumProvider = new SeleniumProvider() {
                            @Override
                            public Selenium get() {
                                return session;
                            }
                        };

                        try {
                            SeleniumHtmlEvaluator evaluator = new SeleniumHtmlEvaluator(seleniumProvider);
                            ConditionChain conditionChain = ConditionChain.create();
                            conditionChain.addCondition(new PageLoaded(seleniumProvider));
                            evaluator.setWaitingCondition(conditionChain);
                            HtmlComponentFactory.get().use(evaluator);
                        } catch (Exception e) {
                            throw new RuntimeException(e.getMessage(), e);
                        }

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