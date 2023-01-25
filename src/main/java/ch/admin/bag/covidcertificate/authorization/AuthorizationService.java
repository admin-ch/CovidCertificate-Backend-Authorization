package ch.admin.bag.covidcertificate.authorization;

import ch.admin.bag.covidcertificate.authorization.config.RoleData;
import ch.admin.bag.covidcertificate.authorization.config.ServiceData;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface AuthorizationService {

    public static final String SERVICE_WEB_UI = "web-ui";
    public static final String SERVICE_API_GATEWAY = "api-gateway";
    public static final String SERVICE_MANAGEMENT = "management";
    public static final String SERVICE_REPORT = "report";
    public static final String SERVICE_NOTIFICATIONS = "notifications";

    /**
     * Returns all permitted functions by given roles at given service.
     * This permission is bound to time and may change during time.
     *
     * @param service  the requesting service
     * @param rawRoles the current roles of the user (either from eIAM or from Claim)
     * @return list of permitted functions
     */
    Set<String> getCurrent(String service, List<String> rawRoles);

    /**
     * Returns the definition of given service.
     *
     * @param service the requesting service
     * @return the service's definition
     */
    ServiceData getDefinition(String service);

    /**
     * Returns the role mapping.<p>
     * Roles consist of different perspectives. The mapping aligns them:
     * <ul>
     *     <li><code>claim</code> = role name used in JWT tokens</li>
     *     <li><code>eiam</code> = role name used in eIAM</li>
     *     <li><code>intern</code> = role name used in this libraries function permissions</li>
     * </ul>
     * @return the mapping for the supported roles
     */
    List<RoleData> getRoleMapping();

    /**
     * Returns <code>true</code> if the user based upon his roles is permitted to generally use the application.
     * @param rawRoles the current roles of the user (either from eIAM or from Claim)
     * @return <code>true</code> if permitted, otherwise <code>false</code>
     */
    boolean isUserPermitted(Collection<String> rawRoles);

    /**
     * Returns a list of {@linkplain ch.admin.bag.covidcertificate.authorization.config.ServiceData.Function} that match given uri and http method.<p>
     * A single entry is a trustful identification, more or less than that indicates that the identification is NOT trustworthy.
     * @param service identifies the current service
     * @param uri the uri the function has to match
     * @param httpMethod the http method the function has to match
     * @return List of {@linkplain ch.admin.bag.covidcertificate.authorization.config.ServiceData.Function} that match given uri and http method
     */
    List<ServiceData.Function> identifyFunction(String service, String uri, String httpMethod);

    /**
     * Returns <code>true</code> for given function if the one-of setting contains the role needed
     * for the function to be accessed. If one-of isn't configured false will be returned.
     *
     * @param rawRoles the current roles of the user (either from eIAM or from Claim)
     * @param function the function to check
     * @return <code>true</code> for given function if the one-of setting contains the role needed
     *         for the function to be accessed. If one-of isn't configured false will be returned.
     */
    boolean isGranted(Set<String> rawRoles, ServiceData.Function function);

    /**
     * Returns a set with the role names as expected by the {@linkplain ch.admin.bag.covidcertificate.authorization.config.ServiceData.Function}.
     * @param rawRoles the current roles of the user (either from eIAM or from Claim)
     * @return List with role names
     */
    Set<String> mapRawRoles(Collection<String> rawRoles);
}
