package ch.admin.bag.covidcertificate.authorization.impl;

import ch.admin.bag.covidcertificate.authorization.AuthorizationConfig;
import ch.admin.bag.covidcertificate.authorization.AuthorizationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AuthorizationConfig.class})
@ActiveProfiles("authorization")
@EnableConfigurationProperties
public class AuthorizationServiceImplTest {

    @Autowired
    private AuthorizationService authorizationService;

    @Test
    public void testGetDefinitions() {
        assertNotNull(authorizationService.getDefinition(AuthorizationService.SERVICE_WEB_UI));
        assertNotNull(authorizationService.getDefinition(AuthorizationService.SERVICE_API_GATEWAY));
        assertNotNull(authorizationService.getDefinition(AuthorizationService.SERVICE_MANAGEMENT));
        assertNotNull(authorizationService.getDefinition(AuthorizationService.SERVICE_REPORT));
        assertNotNull(authorizationService.getDefinition(AuthorizationService.SERVICE_NOTIFICATIONS));
    }

    @Test
    public void testGetMappings() {
        assertNotNull(authorizationService.getRoleMapping());
    }

    @Test
    public void testSimpleGrant() {
        List<String> rawRoles = new ArrayList<>();
        rawRoles.add("bag-cc-web_ui_user");
        Set<String> authFunctions = authorizationService.getCurrent(AuthorizationService.SERVICE_WEB_UI, rawRoles);
        assertTrue(authFunctions.stream().anyMatch(func -> func.equals("create-certificate-web")));
    }

    @Test
    public void testDelegatedGrant() {
        List<String> rawRoles = new ArrayList<>();
        rawRoles.add("bag-cc-web_ui_user");
        rawRoles.add("bag-cc-vacccert_creator");
        Set<String> authFunctions = authorizationService.getCurrent(AuthorizationService.SERVICE_WEB_UI, rawRoles);
        assertTrue(authFunctions.stream().anyMatch(func -> func.equals("create-vaccine-certificate")));
    }

    @Test
    public void testSimpleGrantEiam() {
        List<String> rawRoles = new ArrayList<>();
        rawRoles.add("9500.GGG-Covidcertificate.Web-UI-User");
        Set<String> authFunctions = authorizationService.getCurrent(AuthorizationService.SERVICE_WEB_UI, rawRoles);
        assertTrue(authFunctions.stream().anyMatch(func -> func.equals("create-certificate-web")));
    }

    @Test
    public void testDelegatedGrantEiam() {
        List<String> rawRoles = new ArrayList<>();
        rawRoles.add("9500.GGG-Covidcertificate.Web-UI-User");
        rawRoles.add("9500.GGG-Covidcertificate.VaccCert-Tourist-Creator");
        Set<String> authFunctions = authorizationService.getCurrent(AuthorizationService.SERVICE_WEB_UI, rawRoles);
        assertTrue(authFunctions.stream().anyMatch(func -> func.equals("create-vaccine-tourist-certificate")));
    }

    @Test
    public void testSimpleNoGrant() {
        List<String> rawRoles = new ArrayList<>();
        rawRoles.add("bag-cc-api_gw_user");
        Set<String> authFunctions = authorizationService.getCurrent(AuthorizationService.SERVICE_WEB_UI, rawRoles);
        //web base creator right must not be granted because right not assigned to API-GW user
        assertFalse(authFunctions.stream().anyMatch(func -> func.equals("create-certificate-web")));
    }

    @Test
    public void testDelegatedNoGrant() {
        List<String> rawRoles = new ArrayList<>();
        rawRoles.add("bag-cc-api_gw_user");
        rawRoles.add("bag-cc-vacccert_tourist_creator");
        Set<String> authFunctions = authorizationService.getCurrent(AuthorizationService.SERVICE_WEB_UI, rawRoles);
        //web creator right must not be granted because additional right ${service.webui.functions.create-certificate-web} not assigned to API-GW user
        assertFalse(authFunctions.stream().anyMatch(func -> func.equals("create-vaccine-tourist-certificate")));
    }

    @Test
    public void testSimpleNoGrantEiam() {
        List<String> rawRoles = new ArrayList<>();
        rawRoles.add("9500.GGG-Covidcertificate.API-GW-User");
        Set<String> authFunctions = authorizationService.getCurrent(AuthorizationService.SERVICE_WEB_UI, rawRoles);
        //web base creator right must not be granted because right not assigned to API-GW user
        assertFalse(authFunctions.stream().anyMatch(func -> func.equals("create-certificate-web")));
    }

    @Test
    public void testDelegatedNoGrantEiam() {
        List<String> rawRoles = new ArrayList<>();
        rawRoles.add("9500.GGG-Covidcertificate.API-GW-User");
        rawRoles.add("9500.GGG-Covidcertificate.VaccCert-Tourist-Creator");
        Set<String> authFunctions = authorizationService.getCurrent(AuthorizationService.SERVICE_WEB_UI, rawRoles);
        //web creator right must not be granted because additional right ${service.webui.functions.create-certificate-web} not assigned to API-GW user
        assertFalse(authFunctions.stream().anyMatch(func -> func.equals("create-vaccine-tourist-certificate")));
    }
}
