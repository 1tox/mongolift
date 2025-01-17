package tech.ideo.mongolift.mongolift4spring.commands;

import org.bson.BsonDocument;
import tech.ideo.mongolift.mongolift4spring.MongoliftDataRepository;

import java.util.List;

import static tech.ideo.mongolift.mongolift4spring.commands.CommandUtils.resolveCollectionName;

public class UpdateIndexesCommand implements Command {
    @Override
    public void execute(List<BsonDocument> documents, MongoliftDataRepository mongoLiftDataRepository, CommandContext commandContext) {
        mongoLiftDataRepository.updateIndexes(resolveCollectionName(commandContext.getFilePath()), documents);
    }
}