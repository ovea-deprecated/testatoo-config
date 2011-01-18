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

package org.testatoo.config.env;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.testatoo.config.AbstractTestatooModule;
import org.testatoo.config.Scope;

import static org.testatoo.container.TestatooContainer.JETTY;

final class CommonModule extends AbstractTestatooModule {

    CommonModule() {
    }

    @Override
    protected void configure() {
        containers().register(createContainer()
                .implementedBy(JETTY)
                .webappRoot("src/test/webapp")
                .port(7896)
                .build())
                .scope(Scope.TEST_CLASS);

        lifecycle()
                .onTest(new MethodInterceptor() {
                    @Override
                    public Object invoke(MethodInvocation invocation) throws Throwable {
                        if (!invocation.getMethod().getName().equals("test3")) {
                            System.out.println("====> Running: " + invocation.getMethod());
                            return invocation.proceed();
                        } else {
                            System.out.println("====> Skipping: " + invocation.getMethod());
                            return null;
                        }
                    }
                })
                .onStart(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("STARTING !!!");
                    }
                })
                .onStop(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("STOPPING !!!");
                    }
                });
    }
}