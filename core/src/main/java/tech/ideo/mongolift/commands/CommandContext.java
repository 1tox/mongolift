package tech.ideo.mongolift.commands;

import lombok.Value;
import tech.ideo.mongolift.MigrationPlan;
import tech.ideo.mongolift.MongoliftMetadataRepository;

import java.nio.file.Path;

@Value
public class CommandContext {
    Path filePath;
    String planName;

    public CommandContext(MigrationPlan plan, String file, MongoliftMetadataRepository mongoliftMetadataRepository) {
        this.filePath = plan.getPath().resolve(file);
        this.planName = plan.getName();
    }
}
