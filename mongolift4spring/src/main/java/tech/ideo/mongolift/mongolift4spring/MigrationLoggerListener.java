package tech.ideo.mongolift.mongolift4spring;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tech.ideo.mongolift.mongolift4spring.commands.CommandContext;
import tech.ideo.mongolift.mongolift4spring.commands.CommandName;

import java.util.List;

import static tech.ideo.mongolift.mongolift4spring.PrettyPrinter.printableMigrations;

@RequiredArgsConstructor
@Component
public class MigrationLoggerListener implements MigrationListener {
    private final static Logger LOGGER = LoggerFactory.getLogger(MigrationLoggerListener.class);

    @NonNull
    private MongoliftMetadataRepositoryAdapter mongoliftMetadataRepository;

    private ThreadLocal<List<MigrationMetadataEntity>> migrationsAlreadyApplied = new ThreadLocal<>();

    @Override
    public void onMigrationStarted(Migration migration) {
        LOGGER.info("Starting migration process from path {}", migration.getPath());
        migrationsAlreadyApplied.set(mongoliftMetadataRepository.findAll());
    }

    @Override
    public void onMigrationCompleted(Migration migration) {
        List<MigrationMetadataEntity> migrations = mongoliftMetadataRepository.findAll();
        migrations.removeAll(migrationsAlreadyApplied.get());
        LOGGER.info("Migration process ended. Following migrations were applied: {}", printableMigrations(migrations));
    }

    @Override
    public void onMigrationCommandError(Exception e, CommandName commandName, CommandContext context) {
        LOGGER.error("Migration command {} failed for file {} and plan {}", commandName, context.getFilePath(), context.getPlanName());
    }

    @Override
    public void onMigrationCommandSuccess(CommandName commandName, CommandContext context) {
        MigrationListener.super.onMigrationCommandSuccess(commandName, context);
    }
}
