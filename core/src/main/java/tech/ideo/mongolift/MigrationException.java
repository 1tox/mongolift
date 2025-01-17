package tech.ideo.mongolift;

import tech.ideo.mongolift.commands.CommandContext;
import tech.ideo.mongolift.commands.CommandName;

import java.text.MessageFormat;

public class MigrationException extends RuntimeException {
    public MigrationException(Throwable cause, CommandName commandName, CommandContext context) {
        super(MessageFormat.format("Error while applying migration plan {0} and command {1}", context.getPlanName(), commandName), cause);
    }
}
