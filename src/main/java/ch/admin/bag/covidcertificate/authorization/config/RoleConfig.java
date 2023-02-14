package ch.admin.bag.covidcertificate.authorization.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;


/**
 * Configured data object that contains a list of roles with their internal, EIAM and mapped claim name.
 * EIAM is an acronym and means eGovernment Identity und Access Management.
 * The claim name is the name used inside the JW-Token (JSON Web Token).
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "roles")
public class RoleConfig {

    private List<RoleData> mappings;

}
