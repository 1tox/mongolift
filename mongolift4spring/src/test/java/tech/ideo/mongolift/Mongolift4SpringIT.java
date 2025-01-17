package tech.ideo.mongolift;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static tech.ideo.mongolift.Migration.MIGRATION_COLLECTION;
import static tech.ideo.mongolift.MigrationStatus.SUCCESS;
import static tech.ideo.mongolift.commands.CommandName.INSERT;
import static tech.ideo.mongolift.commands.CommandName.REMOVE_ALL;


@SpringBootTest
@Testcontainers
class Mongolift4SpringIT {
    public static final Query FIND_ALL = new Query();

    @ServiceConnection
    private final static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.0.10")
        .withCreateContainerCmdModifier(cmd -> cmd.withName("mongolift-testcontainer"));

    @Autowired
    private Migration migration;

    @Autowired
    private MongoTemplate mongoTemplate;

    @AfterEach
    void cleanUp() {
        mongoTemplate.remove(FIND_ALL, "countries");
        mongoTemplate.remove(FIND_ALL, "products");
        mongoTemplate.remove(FIND_ALL, "migrations");
    }

    @Test
    void shouldVerifyMigrationIsCorrectlyConfigured() {
        assertThat(migration.getPlans()).hasSize(1);
        assertThat(migration.getPlans()).values().singleElement().satisfies(plan -> {
            assertThat(plan.getPath()).endsWith(Paths.get("db", "migration"));
            assertThat(plan.getCommandNames()).containsExactly(REMOVE_ALL, INSERT);
            assertThat(plan.getIncludes()).containsExactly("countries.json", "products.json");
        });
    }

    @Test
    void shouldMigrateAllCountriesToDatabase() {
        assertThat(mongoTemplate.count(FIND_ALL, "countries")).isEqualTo(0);
        migration.migrate();
        assertThat(mongoTemplate.count(FIND_ALL, "countries")).isEqualTo(5);
    }

    @Test
    void shouldUpdateMigrationsLogs() {
        assertThat(mongoTemplate.count(FIND_ALL, "migrations")).isEqualTo(0);
        migration.migrate();
        Query query = query(where("fileName").is("countries.json"));
        assertThat(mongoTemplate.find(query, MigrationMetadata.class, MIGRATION_COLLECTION)).hasSize(2)
            .allSatisfy(migration -> {
                    assertThat(migration.getFileName()).isEqualTo("countries.json");
                    assertThat(migration.getPlanName()).isEqualTo("referential");
                    assertThat(migration.getStatus()).isEqualTo(SUCCESS);
                }
            ).map(MigrationMetadata::getCommand).containsExactly(REMOVE_ALL, INSERT);

        query = query(where("fileName").is("products.json"));
        assertThat(mongoTemplate.find(query, MigrationMetadata.class, MIGRATION_COLLECTION)).hasSize(2)
            .allSatisfy(migration -> {
                assertThat(migration.getFileName()).isEqualTo("products.json");
                assertThat(migration.getPlanName()).isEqualTo("referential");
                assertThat(migration.getStatus()).isEqualTo(SUCCESS);
            }).map(MigrationMetadata::getCommand).containsExactly(REMOVE_ALL, INSERT);
    }

    @Test
    void shouldCorrectlyApplyMigrationsWhenInvokedMultipleTimes() {
        assertThat(mongoTemplate.find(FIND_ALL, MigrationMetadata.class, MIGRATION_COLLECTION)).isEmpty();
        migration.migrate();
        assertThat(mongoTemplate.find(FIND_ALL, MigrationMetadata.class, MIGRATION_COLLECTION)).hasSize(4);
        migration.migrate();
        assertThat(mongoTemplate.find(FIND_ALL, MigrationMetadata.class, MIGRATION_COLLECTION)).hasSize(4);
    }

};
