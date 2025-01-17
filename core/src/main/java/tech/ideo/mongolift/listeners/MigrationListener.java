package tech.ideo.mongolift.listeners;

import tech.ideo.mongolift.Migration;

public interface MigrationListener {
    default void onMigrationStart(Migration migration) {

    }

    default void onMigrationCompleted(Migration migration) {

    }

    default void onMigrationError(Exception e) {
    }

    default void onMigrationSuccess(Exception e) {
    }

}
