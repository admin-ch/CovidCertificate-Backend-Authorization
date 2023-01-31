# CovidCertificate-Backend-Authorization

The backend authorization is responsible for managing authorization validation on a service's REST requests.

This project is released by the Federal Office of Information Technology, Systems and Telecommunication FOITT on behalf of the Federal Office of Public Health FOPH.

## Configuration

This library is auto-configuring. Add the profile `authorization` to the profiles and add the library as dependency.

### How it works

The file <br><i>META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports</i><br> 
tells Spring-Auto-Configuration to load the class `AuthorizationConfig`. That class uses the annotation `@ComponentScan`
to add spring beans delivered by this library to the spring context.