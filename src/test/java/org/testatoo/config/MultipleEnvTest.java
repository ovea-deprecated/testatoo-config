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
import org.junit.runner.RunWith;
import org.testatoo.config.annotation.TestatooModules;
import org.testatoo.config.env.MainModule;
import org.testatoo.config.junit.TestatooJunitRunner;
import org.testatoo.core.component.TextField;

import static org.hamcrest.Matchers.is;
import static org.testatoo.core.ComponentFactory.component;
import static org.testatoo.core.ComponentFactory.page;
import static org.testatoo.core.Language.assertThat;

@RunWith(TestatooJunitRunner.class)
@TestatooModules(MainModule.class)
public final class MultipleEnvTest {

    @Test
    public void test_1() {
        page().open("/index.xhtml");
        assertThat(component(TextField.class, "lang").value(), is("fr"));
    }

    @Test
    public void test_2() {
        page().open("/index.xhtml");
        assertThat(component(TextField.class, "lang").value(), is("fr"));
    }

    @Test
    public void test_3() {
        throw new IllegalStateException("This test should be skipped by MethodInterceptor in module MyModule");
    }

}