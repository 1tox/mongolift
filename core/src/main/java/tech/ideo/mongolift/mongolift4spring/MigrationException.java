package tech.ideo.mongolift.mongolift4spring;

import tech.ideo.mongolift.mongolift4spring.commands.CommandContext;
import tech.ideo.mongolift.mongolift4spring.commands.CommandName;

import java.text.MessageFormat;

public class MigrationException extends RuntimeException {
    public MigrationException(Throwable cause, CommandName commandName, CommandContext context) {
        super(MessageFormat.format("Error while applying migration plan {0} and command {1}", context.getPlanName(), commandName), cause);
    }
}
