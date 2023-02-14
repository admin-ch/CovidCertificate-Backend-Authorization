package ch.admin.bag.covidcertificate.authorization.config;

import lombok.Data;

/**
 * Configured data object that contains a list of roles with their internal, EIAM and mapped claim name.
 */
@Data
public class RoleData {

    private String intern;
    private String eiam;
    private String claim;
}
