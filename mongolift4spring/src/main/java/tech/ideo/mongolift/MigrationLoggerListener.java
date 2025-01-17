package tech.ideo.mongolift;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tech.ideo.mongolift.listeners.MigrationListener;

@Component
public class MigrationLoggerListener implements MigrationListener {
    private final static Logger LOGGER = LoggerFactory.getLogger(MigrationLoggerListener.class);

    @Override
    public void onMigrationStart(Migration migration) {
        LOGGER.info("Starting migration {}", migration.getPlans());
    }

    @Override
    public void onMigrationCompleted(Migration migration) {
        LOGGER.info("""
            
            """);
    }

    @Override
    public void onMigrationError(Exception e) {
        MigrationListener.super.onMigrationError(e);
    }

    @Override
    public void onMigrationSuccess(Exception e) {
        MigrationListener.super.onMigrationSuccess(e);
    }
}
