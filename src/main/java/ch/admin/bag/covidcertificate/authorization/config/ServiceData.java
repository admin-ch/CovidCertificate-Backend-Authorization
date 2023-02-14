package ch.admin.bag.covidcertificate.authorization.config;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.http.HttpMethod;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Configured data object that contains all service and function mappings needed to operate the Covid Certificate
 * Services. All entries have an identifier and a validity using the from / until attributes.
 * There is also an uri that has to match the requested path of an endpoint in combination with its method.
 * If a match got found the underlying roles configured as one of list define if the token holder gets access or not.
 */
@Data
public class ServiceData {

    @NestedConfigurationProperty
    private Map<String, Function> functions = new HashMap<>();

    @Data
    public static class Function {
        private String identifier;
        private String uri;
        private LocalDateTime from;
        private LocalDateTime until;
        private List<String> additionalRef;
        private List<Function> additional;
        private List<String> oneOf;
        private List<HttpMethod> http;

        public boolean isBetween(LocalDateTime pointInTime) {
            boolean fromSmallerEquals = (this.getFrom() == null || this.getFrom().isBefore(pointInTime) || this.getFrom().isEqual(pointInTime));
            boolean untilLargerEquals = (this.getUntil() == null || this.getUntil().isAfter(pointInTime) || this.getUntil().isEqual(pointInTime));
            return fromSmallerEquals && untilLargerEquals;
        }

        public boolean matchesUri(String uri) {
            String[] paths = this.uri.split("/");
            String[] pathsToCompare = uri.split("/");

            if (paths.length != pathsToCompare.length) {
                return false;
            }

            return IntStream.range(0, paths.length)
                    .map(i -> paths.length - 1 - i) // reverse since uris start with /api/v1/
                    .filter(i -> !paths[i].startsWith("{") && !paths[i].endsWith("}"))
                    .allMatch(i -> Objects.equals(paths[i], pathsToCompare[i]));
        }

        public boolean matchesHttpMethod(String method) {
            return CollectionUtils.isEmpty(http) ||
                    http.stream().anyMatch(m -> m.matches(method.toUpperCase(Locale.ROOT)));
        }
    }
}
