package tech.ideo.mongolift;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import tech.ideo.mongolift.commands.CommandName;

import java.nio.file.Path;
import java.util.List;

@Value
@Builder
public class MigrationPlan {
    String name;
    boolean enabled;
    Path path;
    @Singular
    List<CommandName> commandNames;
    @Singular
    List<String> includes;
}