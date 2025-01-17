package tech.ideo.mongolift.mongolift4spring.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CommandName {
    INSERT(new InsertCommand()),
    REMOVE_ALL(new RemoveAllCommand()),
    UPDATE(null),
    UPSERT(null),
    UPDATE_INDEXES(new UpdateIndexesCommand()),
    SCRIPT(new ScriptCommand());
    final Command command;
}