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

package org.testatoo.config.testatoo;

import com.ovea.tajin.server.Container;
import org.testatoo.config.Provider;
import org.testatoo.config.Scope;
import org.testatoo.config.ScopedProvider;
import org.testatoo.config.SingletonProvider;
import org.testatoo.config.container.ContainerConfig;

import java.util.HashSet;
import java.util.Set;

import static org.testatoo.config.testatoo.Ensure.notNull;

final class DefaultContainerConfig implements ContainerConfig {

    private static final Set<Integer> SCOPE_TEST_SUITE = new HashSet<Integer>();

    private final DefaultTestatooConfig config;

    public DefaultContainerConfig(DefaultTestatooConfig config) {
        this.config = config;
    }

    @Override
    public ScopedProvider<ContainerConfig> register(final Container container) {
        notNull(container, "Container");
        return register(new Provider<Container>() {
            @Override
            public Container get() {
                return container;
            }
        });
    }

    @Override
    public ScopedProvider<ContainerConfig> register(Provider<Container> container) {
        notNull(container, "Container provider");
        final Provider<Container> singleton = SingletonProvider.from(container);
        return new ScopedProvider<ContainerConfig>() {
            @Override
            public ContainerConfig scope(Scope scope) {
                switch (scope) {
                    case TEST_SUITE:
                        config.register(new EventListener(Priority.CONTAINER) {
                            @Override
                            void onStart() {
                                Container container = singleton.get();
                                if (!SCOPE_TEST_SUITE.contains(container.port())) {
                                    container.start();
                                    SCOPE_TEST_SUITE.add(container.port());
                                }
                            }

                            @Override
                            void onStop() {
                            }
                        });
                        break;
                    case TEST_CLASS:
                        config.register(new EventListener(Priority.CONTAINER) {
                            @Override
                            void onStart() {
                                singleton.get().start();
                            }

                            @Override
                            void onStop() {
                                singleton.get().stop();
                            }
                        });
                        break;
                }
                return DefaultContainerConfig.this;
            }
        };
    }

}
