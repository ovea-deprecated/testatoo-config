package org.testatoo.config.container;

import com.ovea.tajin.server.Container;
import com.ovea.tajin.server.ContainerConfiguration;
import org.testatoo.config.Provider;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2013-01-30
 */
public final class ContainerInfo implements Provider<Container> {
    public final ContainerConfiguration configuration;
    public final String implementation;

    public ContainerInfo(ContainerConfiguration configuration, String implementation) {
        this.configuration = configuration;
        this.implementation = implementation;
    }

    @Override
    public Container get() {
        return configuration.buildContainer(implementation);
    }
}
