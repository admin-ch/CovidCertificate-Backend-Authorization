package ch.admin.bag.covidcertificate.authorization.impl;

import ch.admin.bag.covidcertificate.authorization.AuthorizationService;
import ch.admin.bag.covidcertificate.authorization.ProfileRegistry;
import ch.admin.bag.covidcertificate.authorization.config.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
@Profile({"!"+ ProfileRegistry.AUTHORIZATION_MOCK, ProfileRegistry.AUTHORIZATION })
public class AuthorizationServiceImpl implements AuthorizationService {

    private final AuthorizationConfig authorizationConfig;
    private final RoleConfig roleConfig;

    private Map<String, ServiceData> services;
    private Map<String, String> roleMapping;

    @Override
    public Set<String> getCurrent(String service, List<String> rawRoles) {
        Set<String> grantedFunctions = Collections.emptySet();
        // check support for given service
        ServiceData serviceData = services.get(service);
        if (serviceData == null) {
            log.info("service '{}' unknown", service);
        } else {
            // map the raw roles to the configured roles
            final Set<String> roles = mapRawRoles(rawRoles);
            if (roles.isEmpty()) {
                log.info("no supported roles in '{}'", rawRoles);
            } else {
                // keep authorizations which are currently valid
                val functions = serviceData.getFunctions();
                List<ServiceData.Function> functionsByPointInTime =
                        filterByPointInTime(LocalDateTime.now(), functions.values());
                // identify the functions granted to this time by given roles
                grantedFunctions = functionsByPointInTime.stream()
                        .filter(function -> isGrantedIntern(roles, function))
                        .map(ServiceData.Function::getIdentifier)
                        .collect(Collectors.toSet());
            }
        }
        log.info("grants: " + grantedFunctions);
        return grantedFunctions;
    }

    @Override
    public ServiceData getDefinition(String service) {
        return services.get(service);
    }

    @Override
    public List<RoleData> getRoleMapping() {
        return roleConfig.getMappings();
    }

    @Override
    public boolean isGranted(Set<String> rawRoles, ServiceData.Function function) {
        Set<String> roles = this.mapRawRoles(rawRoles);
        return isGrantedIntern(roles, function);
    }

    private boolean isGrantedIntern(Set<String> roles, ServiceData.Function function) {

        boolean isActive = function.isBetween(LocalDateTime.now());
        if (!isActive) {
            return false;
        }

        boolean allAdditionalValid = true;
        if (CollectionUtils.isNotEmpty(function.getAdditional())) {
            // check additional functions which are currently valid
            List<ServiceData.Function> activeAdditionalFunctions =
                    filterByPointInTime(LocalDateTime.now(), function.getAdditional());

            //TODO: avoid infinite recursion!
            allAdditionalValid = activeAdditionalFunctions.stream().allMatch(func -> isGrantedIntern(roles, func));
        }
        List<String> oneOf = function.getOneOf();
        if (CollectionUtils.isEmpty(oneOf)) {
            return allAdditionalValid;
        }
        boolean oneOfValid = oneOf.stream().anyMatch(roles::contains);
        return (allAdditionalValid && oneOfValid);
    }

    @Override
    public boolean isUserPermitted(Collection<String> rawRoles) {
        boolean userIsPermitted = true;
        boolean isHinUser = rawRoles.contains("bag-cc-hin-epr") || rawRoles.contains("bag-cc-hin");
        boolean isHinCodeOrPersonal = rawRoles.contains("bag-cc-hincode") || rawRoles.contains("bag-cc-personal");
        if (isHinUser && !isHinCodeOrPersonal) {
            log.warn("HIN-User not allowed to use the application...");
            log.warn("userroles: {}", rawRoles);
            userIsPermitted = false;
        }
        return userIsPermitted;
    }

    @Override
    public List<ServiceData.Function> identifyFunction(String uri, String httpMethod) {

        List<ServiceData.Function> functions = getDefinition("management")
                .getFunctions()
                .values()
                .stream()
                .filter(f -> StringUtils.hasText(f.getUri()))
                .filter(f -> f.matchesUri(uri))
                .filter(f -> f.matchesHttpMethod(httpMethod))
                .filter(f -> f.isBetween(LocalDateTime.now()))
                .toList();

        return functions;
    }

    @Override
    public Set<String> mapRawRoles(Collection<String> rawRoles) {
        return rawRoles.stream()
                .map(role -> roleMapping.get(role))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private List<ServiceData.Function> filterByPointInTime(LocalDateTime pointInTime, Collection<ServiceData.Function> functions) {
        List<ServiceData.Function> result = Collections.emptyList();
        if (functions != null && pointInTime != null) {
            result = functions.stream()
                    .parallel()
                    .filter(function -> function.isBetween(pointInTime))
                    .toList();
        }
        return result;
    }

    @PostConstruct
    void init() {
        services = new TreeMap<>();
        services.put(SERVICE_NOTIFICATIONS, enrichServiceData(authorizationConfig.getNotifications()));
        services.put(SERVICE_API_GATEWAY, enrichServiceData(authorizationConfig.getApiGateway()));
        services.put(SERVICE_MANAGEMENT, enrichServiceData(authorizationConfig.getManagement()));
        services.put(SERVICE_WEB_UI, enrichServiceData(authorizationConfig.getWebUi()));
        services.put(SERVICE_REPORT, enrichServiceData(authorizationConfig.getReport()));

        roleMapping = new TreeMap<>();
        for (RoleData roleData : roleConfig.getMappings()) {
            if (roleMapping.containsKey(roleData.getClaim()) || roleMapping.containsKey(roleData.getEiam())) {
                throw new IllegalStateException(MessageFormat.format(
                        "role mappings for \"{0}\" not unique (conflicts with either eiam \"{1}\" or claim \"{2}\")",
                        roleData.getIntern(), roleData.getEiam(), roleData.getClaim()));
            } else {
                roleMapping.put(roleData.getClaim(), roleData.getIntern());
                roleMapping.put(roleData.getEiam(), roleData.getIntern());
            }
        }
    }

    private ServiceData enrichServiceData(ServiceData serviceData) {
        if (serviceData == null) {
            return null;
        }
        serviceData.getFunctions().values()
                .forEach(function -> enrichFunction(function, serviceData.getFunctions()));
        return serviceData;
    }

    private void enrichFunction(ServiceData.Function function, Map<String, ServiceData.Function> repo) {
        function.setAdditional(buildAdditionalList(function.getAdditionalRef(), repo));
        if (function.getOneOf() == null) {
            function.setOneOf(Collections.emptyList());
        }
    }

    private List<ServiceData.Function> buildAdditionalList(List<String> refs, Map<String, ServiceData.Function> repo) {
        List<ServiceData.Function> result = new ArrayList<>();
        if (refs != null)
            for (String ref : refs) {
                ServiceData.Function func = repo.get(ref);
                if (func != null) {
                    //found matching function
                    result.add(func);
                } else {
                    throw new IllegalStateException(MessageFormat.format("referenced Function in Authorization Config not found: \"{0}\"", ref));
                }

            }
        return result;
    }
}
