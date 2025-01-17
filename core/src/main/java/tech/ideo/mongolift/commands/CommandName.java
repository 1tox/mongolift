package tech.ideo.mongolift.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CommandName {
    INSERT(new InsertCommand()),
    REMOVE_ALL(new RemoveAllCommand()),
    UPDATE(null),
    UPSERT(null),
    SCRIPT(null);

    final Command command;
}