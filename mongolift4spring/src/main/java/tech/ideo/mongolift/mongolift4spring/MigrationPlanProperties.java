package tech.ideo.mongolift.mongolift4spring;

import lombok.Singular;
import tech.ideo.mongolift.mongolift4spring.commands.CommandName;

import java.nio.file.Path;
import java.util.List;

import static java.util.Optional.ofNullable;

public record MigrationPlanProperties(
    boolean enabled,
    Path path,
    @Singular List<CommandName> commands,
    @Singular List<String> includes) {

    public MigrationPlanProperties(boolean enabled, Path path, @Singular List<CommandName> commands, @Singular List<String> includes) {
        this.enabled = enabled;
        this.path = ofNullable(path).orElse(Path.of(""));
        this.commands = commands;
        this.includes = includes;
    }
}