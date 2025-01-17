package tech.ideo.mongolift4springtest;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import tech.ideo.mongolift.mongolift4spring.Migration;
import tech.ideo.mongolift.mongolift4spring.MigrationMetadata;
import tech.ideo.mongolift.mongolift4spring.MigrationPlan;
import tech.ideo.mongolift.mongolift4spring.MongoliftAutoConfiguration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static tech.ideo.mongolift.mongolift4spring.Migration.MIGRATION_COLLECTION;
import static tech.ideo.mongolift.mongolift4spring.MigrationStatus.SUCCESS;
import static tech.ideo.mongolift.mongolift4spring.commands.CommandName.*;
import static tech.ideo.mongolift4springtest.MongoIndexBuilder.with;


@SpringBootTest(classes = Mongolift4springApplication.class)
@ActiveProfiles("test")
@Testcontainers
@ImportAutoConfiguration(MongoliftAutoConfiguration.class)
class Mongolift4SpringIT {
    public static final Query FIND_ALL = new Query();

    @ServiceConnection
    private final static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.0.10")
        .withCreateContainerCmdModifier(cmd -> cmd.withName("mongolift-testcontainer"));

    @Autowired
    private Migration migration;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void shouldVerifyMigrationIsCorrectlyConfigured() {
        assertThat(migration.getPlans()).hasSize(2);
        assertThat(migration.getPlans())
            .filteredOn(migrationPlan -> migrationPlan.getName().equals("referential"))
            .singleElement()
            .satisfies(plan -> {
                assertThat(plan.getPath()).endsWith(Paths.get("db", "migrations", "referential"));
                assertThat(plan.getCommandNames()).containsExactly(REMOVE_ALL, INSERT);
                assertThat(plan.getIncludes()).containsExactly("countries.json", "products.json");
            });
    }

    @Test
    void shouldMigrateAllCountriesToDatabase() {
        assertThat(mongoTemplate.count(FIND_ALL, "countries")).isEqualTo(3);
    }

    @Test
    void shouldUpdateMigrationsLogs() {
        Query query = query(where("fileName").is("countries.json").and("planName").is("referential"));
        assertThat(mongoTemplate.find(query, MigrationMetadata.class, MIGRATION_COLLECTION)).hasSize(2)
            .allSatisfy(migration -> {
                    assertThat(migration.getFileName()).isEqualTo("countries.json");
                    assertThat(migration.getPlanName()).isEqualTo("referential");
                    assertThat(migration.getStatus()).isEqualTo(SUCCESS);
                }
            ).map(MigrationMetadata::getCommand).containsExactly(REMOVE_ALL, INSERT);

        query = query(where("fileName").is("products.json"));
        assertThat(mongoTemplate.find(query, MigrationMetadata.class, MIGRATION_COLLECTION)).hasSize(3)
            .allSatisfy(migration -> {
                assertThat(migration.getFileName()).isEqualTo("products.json");
                assertThat(migration.getStatus()).isEqualTo(SUCCESS);
            }).map(MigrationMetadata::getCommand).containsExactly(REMOVE_ALL, INSERT, UPDATE_INDEXES);
    }

    @Test
    void shouldCorrectlyApplyMigrationsWhenInvokedMultipleTimes() {
        assertThat(mongoTemplate.find(FIND_ALL, MigrationMetadata.class, MIGRATION_COLLECTION)).hasSize(6);
        migration.migrate();
        assertThat(mongoTemplate.find(FIND_ALL, MigrationMetadata.class, MIGRATION_COLLECTION)).hasSize(6);
    }

    @Test
    void shouldApplyIndexesCreation() {
        with(mongoTemplate)
            .checkThatCollection("countries")
            .hasIndex("name_1")
            .withField("name")
            .andDirection(ASC);
    }

    @Test
    @Order(1)
    void shouldNotUpdateIndex() throws IOException, URISyntaxException {
        with(mongoTemplate)
            .checkThatCollection("countries")
            .hasIndex("name_1")
            .withField("name")
            .andDirection(ASC);

        Migration newIndexMigration = Migration.builder()
            .path(migration.getPath())
            .plan(shouldNotUpdateIndexPlan())
            .mongoliftMetadataRepository(migration.getMongoliftMetadataRepository())
            .mongoliftDataRepository(migration.getMongoliftDataRepository())
            .build();
        newIndexMigration.migrate();

        with(mongoTemplate)
            .checkThatCollection("countries")
            .hasIndex("name_1")
            .withField("name")
            .andDirection(ASC);
    }


    @Test
    @Order(2)
    void shouldUpdateIndex() throws IOException, URISyntaxException {
        with(mongoTemplate)
            .checkThatCollection("countries")
            .hasIndex("name_1")
            .withField("name")
            .andDirection(ASC);

        Migration newIndexMigration = Migration.builder()
            .path(migration.getPath())
            .plan(shouldUpdateIndexPlan())
            .mongoliftMetadataRepository(migration.getMongoliftMetadataRepository())
            .mongoliftDataRepository(migration.getMongoliftDataRepository())
            .build();

        newIndexMigration.migrate();

        with(mongoTemplate)
            .checkThatCollection("countries")
            .hasIndex("name_1")
            .withField("name")
            .andDirection(DESC);
    }


    private MigrationPlan shouldUpdateIndexPlan() throws IOException, URISyntaxException {
        return MigrationPlan.builder()
            .name("shouldUpdateIndex")
            .enabled(true)
            .commandName(UPDATE_INDEXES)
            .path(migration.getPath().resolve(Path.of("shouldUpdateIndex")))
            .include("countries.json")
            .build();
    }

    private MigrationPlan shouldNotUpdateIndexPlan() throws IOException, URISyntaxException {
        return MigrationPlan.builder()
            .name("shouldNotUpdateIndex")
            .enabled(true)
            .commandName(UPDATE_INDEXES)
            .path(migration.getPath().resolve(Path.of("shouldNotUpdateIndex")))
            .include("countries.json")
            .build();
    }

};
