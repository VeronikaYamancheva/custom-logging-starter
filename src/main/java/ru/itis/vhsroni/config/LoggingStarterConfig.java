package ru.itis.vhsroni.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.itis.vhsroni.aspect.ControllerLoggingAspect;
import ru.itis.vhsroni.aspect.RepositoryLoggingAspect;
import ru.itis.vhsroni.aspect.ServiceLoggingAspect;
import ru.itis.vhsroni.properties.LoggingStarterProperties;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(LoggingStarterProperties.class)
@ConditionalOnProperty(prefix = "ru.itis.vhsroni.logging-starter", value = "enabled", havingValue = "true")
public class LoggingStarterConfig {

    @Bean
    @ConditionalOnMissingBean(ControllerLoggingAspect.class)
    public ControllerLoggingAspect controllerLoggerAspect() {
        return new ControllerLoggingAspect();
    }

    @Bean
    @ConditionalOnMissingBean(RepositoryLoggingAspect.class)
    public RepositoryLoggingAspect repositoryLoggerAspect() {
        return new RepositoryLoggingAspect();
    }

    @Bean
    @ConditionalOnMissingBean(ServiceLoggingAspect.class)
    public ServiceLoggingAspect serviceLoggerAspect() {
        return new ServiceLoggingAspect();
    }
}
