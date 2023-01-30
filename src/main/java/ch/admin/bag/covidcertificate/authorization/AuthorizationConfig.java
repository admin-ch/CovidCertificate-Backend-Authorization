package ch.admin.bag.covidcertificate.authorization;

import ch.admin.bag.covidcertificate.authorization.config.ServiceData;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Data
@Configuration
@ComponentScan
@ConfigurationProperties(prefix = "services")
public class AuthorizationConfig {

    private ServiceData webUi;
    private ServiceData apiGateway;
    private ServiceData management;
    private ServiceData report;
    private ServiceData notifications;
}
