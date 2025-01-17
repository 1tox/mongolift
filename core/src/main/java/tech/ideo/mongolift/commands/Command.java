package tech.ideo.mongolift.commands;

import org.bson.BsonDocument;
import tech.ideo.mongolift.MongoliftDataRepository;

import java.util.List;

public interface Command {
    void execute(List<BsonDocument> documents, MongoliftDataRepository mongoLiftDataRepository, CommandContext commandContext);
}