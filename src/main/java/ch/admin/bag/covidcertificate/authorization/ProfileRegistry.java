package ch.admin.bag.covidcertificate.authorization;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Class to register the profile names segregating mock and real AuthorizationService beans from each other.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileRegistry {
    public static final String AUTHORIZATION_MOCK = "mock-authorization";
    public static final String AUTHORIZATION = "authorization";
}
