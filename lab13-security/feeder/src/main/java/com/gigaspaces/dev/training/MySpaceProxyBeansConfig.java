package com.gigaspaces.dev.training;

import org.openspaces.core.config.annotation.SpaceProxyBeansConfig;
import org.openspaces.core.space.SecurityConfig;
import org.openspaces.core.space.SpaceProxyFactoryBean;
import org.springframework.beans.factory.annotation.Value;

public class MySpaceProxyBeansConfig extends SpaceProxyBeansConfig {

    @Value("${space.name}")
    private String spaceName;

    @Value("${username}")
    private String username;

    @Value("${password}")
    private String password;

    @Override
    protected void configure(SpaceProxyFactoryBean factoryBean) {
        super.configure(factoryBean);

        factoryBean.setSpaceName(spaceName);
        SecurityConfig securityConfig = new SecurityConfig(username, password);
        factoryBean.setSecurityConfig(securityConfig);
    }
}
