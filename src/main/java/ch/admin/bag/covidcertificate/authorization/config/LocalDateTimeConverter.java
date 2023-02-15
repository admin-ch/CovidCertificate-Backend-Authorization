package ch.admin.bag.covidcertificate.authorization.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * Class to convert a ISO_LOCAL_DATE_TIME string to a LocalDateTime object.
 */
@Component
@ConfigurationPropertiesBinding
public class LocalDateTimeConverter implements Converter<String, LocalDateTime> {

    /**
     * Parses the given string named timestamp to a LocalDateTime object.
     * It uses simply the parse method of the same.
     *
     * @param timestamp as string formatted as ISO_LOCAL_DATE_TIME
     * @return the LocalDateTime object or a runtime exception.
     * @throws DateTimeParseException if the given timestamp string is not formatted as ISO_LOCAL_DATE_TIME
     * @throws NullPointerException   if the given timestamp string is null
     */
    @Override
    public LocalDateTime convert(String timestamp) {
        return LocalDateTime.parse(timestamp);
    }
}