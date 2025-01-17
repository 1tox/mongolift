package tech.ideo.mongolift.mongolift4spring;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;

import static java.lang.System.lineSeparator;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static tech.ideo.mongolift.mongolift4spring.MigrationStatus.SUCCESS;
import static tech.ideo.mongolift.mongolift4spring.PrettyPrinter.printableMigrations;
import static tech.ideo.mongolift.mongolift4spring.commands.CommandName.REMOVE_ALL;

class PrettyPrinterTest {
    @Test
    void testPrint() {
        String expected = printableMigrations(asList(createMigrationMetadataEntity(), createMigrationMetadataEntity()));
        assertThat(toLinuxStyle(expected))
            .isEqualTo("""
                
                +-----------------------+-----------------------+-----------------------+-----------------------+-----------------------+-----------------------+
                | Plan name             | File                  | Command               | Execution date        | Execution time        | Status                |
                +-----------------------+-----------------------+-----------------------+-----------------------+-----------------------+-----------------------+
                | referential           | country.json          | REMOVE_ALL            | 20230101 01:00:00.000 | 0 ms                  | SUCCESS               |
                | referential           | country.json          | REMOVE_ALL            | 20230101 01:00:00.000 | 0 ms                  | SUCCESS               |
                +-----------------------+-----------------------+-----------------------+-----------------------+-----------------------+-----------------------+""");
    }

    private static String toLinuxStyle(String expected) {
        return expected
            .replace(lineSeparator(), "\n");
    }

    private static MigrationMetadataEntity createMigrationMetadataEntity() {
        MigrationMetadataEntity migrationMetadataEntity = new MigrationMetadataEntity();

        migrationMetadataEntity.setPlanName("referential");
        migrationMetadataEntity.setFileName("country.json");
        migrationMetadataEntity.setCommand(REMOVE_ALL);
        migrationMetadataEntity.setExecutedAt((Date.from(Instant.parse("2023-01-01T00:00:00Z"))));
        migrationMetadataEntity.setStatus(SUCCESS);
        return migrationMetadataEntity;
    }
}