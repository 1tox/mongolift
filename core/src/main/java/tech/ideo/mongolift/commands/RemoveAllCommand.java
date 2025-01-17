package tech.ideo.mongolift.commands;

import org.bson.BsonDocument;
import tech.ideo.mongolift.MongoliftDataRepository;

import java.util.List;

import static tech.ideo.mongolift.commands.CommandUtils.resolveCollectionName;

public class RemoveAllCommand implements Command {

    @Override
    public void execute(List<BsonDocument> documents, MongoliftDataRepository mongoLiftDataRepository, CommandContext context) {
        mongoLiftDataRepository.removeAll(resolveCollectionName(context.getFilePath()));
    }
}