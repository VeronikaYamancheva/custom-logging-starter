package ru.itis.vhsroni.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "ru.itis.vhsroni.logging-starter")
public class LoggingStarterProperties {

    private boolean enabled = false;

    private boolean logToFile = false;

    private String errorLogsFilePath = "log/error_logs.log";

    private String springLogsFilePath = "log/spring_logs.log";

    private String basePackages = "ru.itis.vhsroni";
}