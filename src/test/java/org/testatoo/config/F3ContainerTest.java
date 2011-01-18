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

import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;
import org.junit.Test;
import org.testatoo.config.testatoo.Testatoo;
import org.testatoo.container.Container;
import org.testatoo.container.ContainerConfiguration;

import javax.net.ServerSocketFactory;
import java.net.ServerSocket;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.testatoo.container.TestatooContainer.JETTY;
import static org.testatoo.container.TestatooContainer.TOMCAT;

public final class F3ContainerTest {

    @Test
    public void test() throws Exception {

        Testatoo testatoo = Testatoo.configure(new AbstractTestatooModule() {
            @Override
            protected void configure() {
                Provider<Container> provider = createContainer()
                        .implementedBy(TOMCAT)
                        .webappRoot("src/test/webapp")
                        .context("/mycontext")
                        .port(7896)
                        .build();
                containers()
                        .register(provider)
                        .scope(Scope.TEST_CLASS)
                        .register(ContainerConfiguration.create()
                                .webappRoot("src/test/webapp")
                                .context("/mycontext")
                                .port(7897)
                                .buildContainer(JETTY))
                        .scope(Scope.TEST_CLASS);

            }
        });

        assertTrue("Verify that port 7896 is free", isPortFree(7896));
        assertTrue("Verify that port 7897 is free", isPortFree(7897));

        testatoo.start();

        assertFalse("Verify that port 7896 is used", isPortFree(7896));
        assertFalse("Verify that port 7897 is used", isPortFree(7897));

        verify("http://127.0.0.1:7896/mycontext/index.xhtml");
        verify("http://127.0.0.1:7897/mycontext/index.xhtml");

        testatoo.stop();

        assertTrue("Verify that port 7896 is free", isPortFree(7896));
        assertTrue("Verify that port 7897 is free", isPortFree(7897));
    }

    private void verify(String url) throws Exception {
        WebConversation wc = new WebConversation();
        WebResponse resp = wc.getResponse(url);
        assertEquals(resp.getTitle(), "HTML file");
        assertThat(resp.getText(), containsString("HTML file"));
    }

    private boolean isPortFree(int port) {
        try {
            ServerSocket server = ServerSocketFactory.getDefault().createServerSocket(port);
            server.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
