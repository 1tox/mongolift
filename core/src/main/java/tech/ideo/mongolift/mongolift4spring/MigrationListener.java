package tech.ideo.mongolift.mongolift4spring;

import tech.ideo.mongolift.mongolift4spring.commands.CommandContext;
import tech.ideo.mongolift.mongolift4spring.commands.CommandName;

public interface MigrationListener {
    default void onMigrationStarted(Migration migration) {

    }

    default void onMigrationCompleted(Migration migration) {

    }

    default void onExecutionPlanStarted(MigrationPlan migrationPlan) {

    }

    default void onExecutionPlanCompleted(MigrationPlan migrationPlan) {

    }

    default void onMigrationCommandStarted(Exception e, CommandContext context) {
    }

    default void onMigrationCommandError(Exception e, CommandName commandName, CommandContext context) {
    }

    default void onMigrationCommandSuccess(CommandName commandName, CommandContext context) {
    }

}
