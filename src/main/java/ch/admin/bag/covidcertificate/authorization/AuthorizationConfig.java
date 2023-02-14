package ch.admin.bag.covidcertificate.authorization;

import ch.admin.bag.covidcertificate.authorization.config.ServiceData;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


/**
 * Holder object for all named services that need a service configuration using ServiceData.
 */
@Data
@Configuration
@ComponentScan
@ConfigurationProperties(prefix = "services")
public class AuthorizationConfig {

    /**
     * The service called Web UI that uses the management service to generate, revoke, covid certificates.
     */
    private ServiceData webUi;

    /**
     * The API Gateway service that uses the management service to generate, revoke covid certificates.
     */
    private ServiceData apiGateway;

    /**
     * The management service as central service to generate, revoke, print, sign covid certificates.
     */
    private ServiceData management;

    /**
     * The report service that allows responsible people to get statistical data about the generated covid certificates.
     */
    private ServiceData report;

    /**
     * A service to read, store and delete notification messages, mainly used to get maintenance information.
     */
    private ServiceData notifications;
}
