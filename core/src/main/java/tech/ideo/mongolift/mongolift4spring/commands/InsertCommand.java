package tech.ideo.mongolift.mongolift4spring.commands;

import org.bson.BsonDocument;
import org.bson.BsonObjectId;
import org.bson.types.ObjectId;
import tech.ideo.mongolift.mongolift4spring.MongoliftDataRepository;

import java.util.List;

import static tech.ideo.mongolift.mongolift4spring.commands.CommandUtils.resolveCollectionName;

public class InsertCommand implements Command {

    @Override
    public void execute(List<BsonDocument> documents, MongoliftDataRepository mongoLiftDataRepository, CommandContext context) {
        List<BsonDocument> bsonDocumentsWithIds = documents.stream()
            .map(bsonDocument -> {
                BsonDocument clone = bsonDocument.clone();
                clone.putIfAbsent("_id", new BsonObjectId(new ObjectId()));
                return clone;
            })
            .toList();
        mongoLiftDataRepository.insert(resolveCollectionName(context.getFilePath()), bsonDocumentsWithIds);
    }
}