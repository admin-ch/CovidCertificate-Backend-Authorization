package ch.admin.bag.covidcertificate.authorization.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Data
@Configuration
@ConfigurationProperties(prefix = "services")
public class AuthorizationConfig {

    private ServiceData webUi;
    private ServiceData apiGateway;
    private ServiceData management;
    private ServiceData report;
    private ServiceData notifications;
}
