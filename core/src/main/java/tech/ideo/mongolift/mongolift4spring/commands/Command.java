package tech.ideo.mongolift.mongolift4spring.commands;

import org.bson.BsonDocument;
import tech.ideo.mongolift.mongolift4spring.MongoliftDataRepository;

import java.util.List;

public interface Command {
    void execute(List<BsonDocument> documents, MongoliftDataRepository mongoLiftDataRepository, CommandContext commandContext);
}