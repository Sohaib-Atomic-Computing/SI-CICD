package io.satra.iconnect.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@PropertySource(value = {"classpath:${envTarget:LOCAL}_application.properties"})
@Service
public class PropertiesLoaderService implements ApplicationContextAware {
    @Autowired
    private static Environment environment;
    private @Autowired
    AutowireCapableBeanFactory beanFactory;
    private ApplicationContext applicationContext;

    public static String getEnvProperty(String KEY) {
        return environment.getProperty(KEY);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
