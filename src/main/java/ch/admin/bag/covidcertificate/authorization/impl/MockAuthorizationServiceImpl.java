package ch.admin.bag.covidcertificate.authorization.impl;

import ch.admin.bag.covidcertificate.authorization.AuthorizationService;
import ch.admin.bag.covidcertificate.authorization.ProfileRegistry;
import ch.admin.bag.covidcertificate.authorization.config.RoleData;
import ch.admin.bag.covidcertificate.authorization.config.ServiceData;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * The mock implementation of AuthorizationService used if the profile AUTHORIZATION_MOCK is active.
 */
@Service
@Profile(ProfileRegistry.AUTHORIZATION_MOCK + " && !" + ProfileRegistry.AUTHORIZATION)
public class MockAuthorizationServiceImpl implements AuthorizationService {

    @Override
    public Set<String> getCurrent(String service, List<String> rawRoles) {
        return Collections.emptySet();
    }

    @Override
    public ServiceData getDefinition(String service) {
        return new ServiceData();
    }

    @Override
    public List<RoleData> getRoleMapping() {
        return Collections.emptyList();
    }

    @Override
    public boolean isUserPermitted(Collection<String> rawRoles) {
        return true;
    }

    @Override
    public List<ServiceData.Function> identifyFunction(String service, String uri, String httpMethod) {
        return Arrays.asList(new ServiceData.Function());
    }

    @Override
    public boolean isGranted(Set<String> rawRoles, ServiceData.Function function) {
        return true;
    }

    @Override
    public Set<String> mapRawRoles(Collection<String> rawRoles) {
        return Collections.emptySet();
    }
}
