package tech.ideo.mongolift;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Configuration
@ConditionalOnProperty(name = "migration.enabled", havingValue = "true")
public class MongoliftAutoConfiguration {

    @Bean
    public Migration migration(MigrationProperties migrationProperties, MongoliftDataRepository mongoLiftDataRepository, MongoliftMetadataRepositoryAdapter mongoliftMetadataRepositoryAdapter, MigrationLoggerListener migrationLoggerListener) {
        return Migration.builder()
            .enabled(true)
            .mongoliftDataRepository(mongoLiftDataRepository)
            .mongoliftMetadataRepository(mongoliftMetadataRepositoryAdapter)
            .plans(getPlansFrom(migrationProperties))
            .path(migrationProperties.getDirectory())
            .listener(migrationLoggerListener)
            .build();
    }

    private Map<String, MigrationPlan> getPlansFrom(MigrationProperties migrationProperties) {
        Path directory = migrationProperties.getDirectory();

        return migrationProperties.getPlans().entrySet().stream()
            .collect(toMap(
                Map.Entry::getKey,
                toMigrationPlan(directory)
            ));
    }

    private Function<Map.Entry<String, MigrationPlanProperties>, MigrationPlan> toMigrationPlan(Path directory) {
        return entry -> {
            MigrationPlanProperties originalPlan = entry.getValue();
            return new MigrationPlan(
                entry.getKey(),
                originalPlan.enabled(),
                directory.resolve(originalPlan.path()),
                originalPlan.commands(),
                originalPlan.includes()
            );
        };
    }
}