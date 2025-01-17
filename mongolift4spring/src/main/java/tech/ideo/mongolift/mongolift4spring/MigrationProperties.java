package tech.ideo.mongolift.mongolift4spring;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.nio.file.Path;
import java.util.Map;

@ConditionalOnProperty(name = "migration.enabled", havingValue = "true", matchIfMissing = true)
@Configuration
@ConfigurationProperties(prefix = "migration")
@Data
@Validated
public class MigrationProperties {
    private Path directory;
    private Map<String, MigrationPlanProperties> plans;

}