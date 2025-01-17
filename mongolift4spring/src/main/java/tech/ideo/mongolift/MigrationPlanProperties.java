package tech.ideo.mongolift;

import lombok.Singular;
import tech.ideo.mongolift.commands.CommandName;

import java.nio.file.Path;
import java.util.List;

public record MigrationPlanProperties(
    boolean enabled,
    Path path,
    @Singular List<CommandName> commands,
    @Singular List<String> includes) {

    public MigrationPlanProperties(boolean enabled, Path path, @Singular List<CommandName> commands, @Singular List<String> includes) {
        this.enabled = enabled;
        this.path = path == null ? Path.of("") : path;
        this.commands = commands;
        this.includes = includes;
    }
}
