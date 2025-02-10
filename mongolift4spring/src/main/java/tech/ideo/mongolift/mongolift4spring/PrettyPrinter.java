package tech.ideo.mongolift.mongolift4spring;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.stream.IntStream;

public class PrettyPrinter {
    public static final int COLUMN_LENGTH = 23;
    public static final int NB_COLUMNS = 6;
    public static final String TABLE_INDENT = " ";
    public static final String ABBREVIATE = "...";

    static String printableMigrations(List<MigrationMetadataEntity> migrations) {
        return MessageFormat.format("{0}{1}{2}|{3}|{4}|{5}|{6}|{7}|{8}|{9}{10}{11}{12}{13}",
            System.lineSeparator(),
            horizontalRule(),
            System.lineSeparator(),
            alignLeft("Plan name"),
            alignLeft("File"),
            alignLeft("Command"),
            alignLeft("Execution date"),
            alignLeft("Execution time"),
            alignLeft("Status"),
            System.lineSeparator(),
            horizontalRule(),
            System.lineSeparator(),
            migrations
                .stream()
                .map(toPrettyPrint())
                .reduce(String::concat).orElse(""),
            horizontalRule()
        );
    }


    private static String horizontalRule() {
        return IntStream.rangeClosed(1, NB_COLUMNS)
            .mapToObj(i -> "+" + "-".repeat(COLUMN_LENGTH))
            .reduce(String::concat)
            .orElseThrow()
            .concat("+");
    }

    private static String alignLeft(String input) {
        if (input.length() >= COLUMN_LENGTH) {
            return TABLE_INDENT + input.substring(0, COLUMN_LENGTH - ABBREVIATE.length()) + ABBREVIATE;
        }
        return String.format(TABLE_INDENT + "%-" + (COLUMN_LENGTH - 1) + "s", input);
    }

    private static Function<MigrationMetadataEntity, String> toPrettyPrint() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return migrationMetadataEntity ->
            MessageFormat.format("|{0}|{1}|{2}|{3}|{4}|{5}|{6}",
                alignLeft(migrationMetadataEntity.getPlanName()),
                alignLeft(migrationMetadataEntity.getFileName()),
                alignLeft(migrationMetadataEntity.getCommand().name()),
                alignLeft(simpleDateFormat.format(migrationMetadataEntity.getExecutedAt())),
                alignLeft(migrationMetadataEntity.getExecutionTime() + " ms"),
                alignLeft(migrationMetadataEntity.getStatus().name()),
                System.lineSeparator());
    }
}
