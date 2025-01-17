package tech.ideo.mongolift.mongolift4spring;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import org.apache.commons.io.IOUtils;
import org.bson.*;
import org.bson.types.ObjectId;
import tech.ideo.mongolift.mongolift4spring.commands.CommandContext;
import tech.ideo.mongolift.mongolift4spring.commands.CommandName;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Collections.singletonList;
import static tech.ideo.mongolift.mongolift4spring.MigrationStatus.FAILED;
import static tech.ideo.mongolift.mongolift4spring.MigrationStatus.SUCCESS;

@Value
@Builder
public class Migration {
    public static final String CHECKSUM_ALGORITHM = "SHA-256";
    public static final String MIGRATION_COLLECTION = "migrations";
    boolean enabled;
    Path path;
    @Singular
    Set<MigrationPlan> plans;
    @Singular
    List<MigrationListener> listeners;
    MongoliftDataRepository mongoliftDataRepository;
    MongoliftMetadataRepository mongoliftMetadataRepository;

    public Migration migrate() {
        getListeners().forEach(migrationListener -> migrationListener.onMigrationStarted(this));
        plans
            .stream()
            .filter(byEnabled())
            .forEachOrdered(executeMigrationPlan());
        getListeners().forEach(migrationListener -> migrationListener.onMigrationCompleted(this));
        return this;
    }

    private Consumer<MigrationPlan> executeMigrationPlan() {
        return migrationPlan -> {
            getListeners().forEach(migrationListener -> migrationListener.onExecutionPlanStarted(migrationPlan));
            migrationPlan.getIncludes()
                .stream()
                .map(toCommandContext(migrationPlan))
                .forEachOrdered(commandContext -> migrationPlan.getCommandNames()
                    .stream()
                    .filter(byMigrationNotAlreadyApplied(commandContext))
                    .forEach(execute(commandContext)));
            getListeners().forEach(migrationListener -> migrationListener.onExecutionPlanCompleted(migrationPlan));
        };
    }

    private Predicate<CommandName> byMigrationNotAlreadyApplied(CommandContext context) {
        return commandName -> {
            String file = context.getFilePath().getFileName().toString();
            String planName = context.getPlanName();
            Optional<MigrationMetadata> latestMigration = mongoliftMetadataRepository.findFirstByPlanNameAndFileNameAndCommandOrderByIdDesc(planName, file, commandName);
            String lastAppliedChecksum = latestMigration
                .orElse(new MigrationMetadata())
                .getChecksum();
            String currentChecksum = computeChecksum(commandName, context);
            return !currentChecksum.equals(lastAppliedChecksum);
        };
    }

    private List<MigrationListener> getListeners() {
        return listeners;
    }

    private Predicate<MigrationPlan> byEnabled() {
        return MigrationPlan::isEnabled;
    }

    private Function<String, CommandContext> toCommandContext(MigrationPlan plan) {
        return file -> new CommandContext(plan, file);
    }

    private Consumer<CommandName> execute(CommandContext commandContext) {
        return commandName -> {
            long startTime = System.currentTimeMillis();
            long endTime;
            try {
                commandName.getCommand().execute(toBsonValue(commandContext), mongoliftDataRepository, commandContext);
                endTime = System.currentTimeMillis();
                updateAuditCollection(commandContext, commandName, SUCCESS, endTime - startTime);
                getListeners().forEach(migrationListener -> migrationListener.onMigrationCommandSuccess(commandName, commandContext));
            } catch (Exception e) {
                endTime = System.currentTimeMillis();
                updateAuditCollection(commandContext, commandName, FAILED, endTime - startTime);
                getListeners().forEach(migrationListener -> migrationListener.onMigrationCommandError(e, commandName, commandContext));
            }
        };
    }

    private void updateAuditCollection(CommandContext commandContext, CommandName commandName, MigrationStatus status, long executionTime) {
        var migrationMetadata = new MigrationMetadata();
        migrationMetadata.setId(new BsonObjectId(new ObjectId()));
        migrationMetadata.setExecutedAt(new Date());
        migrationMetadata.setChecksum(computeChecksum(commandName, commandContext));
        migrationMetadata.setCommand(commandName);
        migrationMetadata.setFileName(commandContext.getFilePath().getFileName().toString());
        migrationMetadata.setPlanName(commandContext.getPlanName());
        migrationMetadata.setStatus(status);
        migrationMetadata.setExecutionTime(executionTime);
        mongoliftDataRepository.insert(MIGRATION_COLLECTION, singletonList(convertToBsonDocument(migrationMetadata)));
    }

    private BsonDocument convertToBsonDocument(MigrationMetadata migrationMetadata) {
        Document document = new Document()
            .append("_id", migrationMetadata.getId())
            .append("executedAt", migrationMetadata.getExecutedAt())
            .append("executionTime", migrationMetadata.getExecutionTime())
            .append("checksum", migrationMetadata.getChecksum())
            .append("command", migrationMetadata.getCommand().toString())
            .append("fileName", migrationMetadata.getFileName())
            .append("planName", migrationMetadata.getPlanName())
            .append("status", migrationMetadata.getStatus().toString());
        ;

        return document.toBsonDocument();
    }

    private String computeChecksum(CommandName commandName, CommandContext commandContext) {
        try {
            var bytes = Files.readAllBytes(commandContext.getFilePath());
            return convertChecksumToString(MessageDigest.getInstance(CHECKSUM_ALGORITHM).digest(bytes));
        } catch (Exception e) {
            throw new MigrationException(e, commandName, commandContext);
        }
    }

    private String convertChecksumToString(byte[] checksumBytes) {
        var checksumHex = new StringBuilder();
        for (byte b : checksumBytes) {
            checksumHex.append(String.format("%02x", b));
        }
        return checksumHex.toString();
    }


    private List<BsonDocument> toBsonValue(CommandContext commandContext) {
        try {
            String json = IOUtils.toString(commandContext.getFilePath().toUri(), StandardCharsets.UTF_8);
            try {
                return BsonArray.parse(json)
                    .stream()
                    .map(BsonValue::asDocument)
                    .toList();
            } catch (BsonInvalidOperationException e) {
                return singletonList(BsonDocument.parse(json));
            }
        } catch (Exception e) {
            throw new IllegalStateException(MessageFormat.format("Error during migration process executing file {0}", commandContext), e);
        }
    }
}