package tech.ideo.mongolift.mongolift4spring;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Collections.emptyMap;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static tech.ideo.mongolift.mongolift4spring.commands.CommandName.*;

@Configuration
@ConditionalOnProperty(name = "migration.enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnClass(MongoTemplate.class)
@EnableMongoRepositories("tech.ideo.mongolift.mongolift4spring")
public class MongoliftAutoConfiguration {

    public static final Path DEFAULT_ROOT_PATH;

    static {
        try {
            DEFAULT_ROOT_PATH = getDefaultRootPath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static Path getDefaultRootPath() throws URISyntaxException {
        URL resource = requireNonNull(MongoliftAutoConfiguration.class.getClassLoader().getResource("db/migrations"));
        return Path.of(resource.toURI());
    }

    @Bean
    public Migration migration(MigrationProperties migrationProperties, MongoliftDataRepository mongoLiftDataRepository, MongoliftMetadataRepositoryAdapter mongoliftMetadataRepositoryAdapter, MigrationLoggerListener migrationLoggerListener) throws IOException, URISyntaxException {
        return Migration.builder()
            .enabled(true)
            .path(resolveMigrationsPath(migrationProperties))
            .mongoliftDataRepository(mongoLiftDataRepository)
            .mongoliftMetadataRepository(mongoliftMetadataRepositoryAdapter)
            .plan(referential(resolveMigrationsPath(migrationProperties)))
            .plan(indexes(resolveMigrationsPath(migrationProperties)))
            .plans(getCustomPlansFrom(migrationProperties))
            .listener(migrationLoggerListener)
            .build()
            .migrate();
    }

    private static Path resolveMigrationsPath(MigrationProperties migrationProperties) {
        return ofNullable(migrationProperties.getDirectory()).orElse(DEFAULT_ROOT_PATH);
    }

    private static MigrationPlan referential(Path root) throws IOException, URISyntaxException {
        var referentialPath = root.resolve("referential");
        return MigrationPlan.builder()
            .name("referential")
            .enabled(true)
            .commandName(REMOVE_ALL)
            .commandName(INSERT)
            .path(referentialPath)
            .includes(filesInPath(referentialPath))
            .build();
    }

    private static MigrationPlan indexes(Path root) throws IOException, URISyntaxException {
        var indexesPath = root.resolve("indexes");
        return MigrationPlan.builder()
            .name("indexes")
            .enabled(true)
            .commandName(UPDATE_INDEXES)
            .path(indexesPath)
            .includes(filesInPath(indexesPath))
            .build();
    }

    private static List<String> filesInPath(Path directory) throws IOException, URISyntaxException {
        Path path = Path.of(directory.toUri());
        if (Files.isDirectory(path)) {
            try (Stream<Path> stream = Files.list(path)) {
                return stream
                    .map(p -> p.getFileName().toString())
                    .toList();
            }
        }
        throw new IOException(MessageFormat.format("path {0} is not a directory", directory));
    }

    private List<MigrationPlan> getCustomPlansFrom(MigrationProperties migrationProperties) {
        var directory = resolveMigrationsPath(migrationProperties);
        return ofNullable(migrationProperties.getPlans()).orElse(emptyMap())
            .entrySet()
            .stream()
            .map(toMigrationPlan(directory))
            .toList();
    }

    private static Function<Map.Entry<String, MigrationPlanProperties>, MigrationPlan> toMigrationPlan(Path directory) {
        return entry -> {
            var originalPlan = entry.getValue();
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