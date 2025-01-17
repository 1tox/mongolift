package tech.ideo.mongolift.mongolift4spring;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.Value;
import tech.ideo.mongolift.mongolift4spring.commands.CommandName;

import java.nio.file.Path;
import java.util.List;

@Value
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MigrationPlan {
    @EqualsAndHashCode.Include
    String name;
    boolean enabled;
    Path path;
    @Singular
    List<CommandName> commandNames;
    @Singular
    List<String> includes;
}