package tech.ideo.mongolift;

import org.bson.BsonDocument;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public record MongoliftDataRepositoryAdapter(MongoTemplate mongoTemplate) implements MongoliftDataRepository {

    @Override
    public void insert(List<BsonDocument> bsonDocuments, String collectionName) {
        mongoTemplate.insert(bsonDocuments, collectionName);
    }

    @Override
    public void removeAll(String collectionName) {
        mongoTemplate.findAllAndRemove(new Query(), collectionName);
    }
}
