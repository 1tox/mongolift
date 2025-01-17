package tech.ideo.mongolift.mongolift4spring.commands;

import lombok.Value;
import tech.ideo.mongolift.mongolift4spring.MigrationPlan;
import tech.ideo.mongolift.mongolift4spring.MongoliftMetadataRepository;

import java.nio.file.Path;

@Value
public class CommandContext {
    Path filePath;
    String planName;

    public CommandContext(MigrationPlan plan, String file) {
        this.filePath = plan.getPath().resolve(file);
        this.planName = plan.getName();
    }
}
