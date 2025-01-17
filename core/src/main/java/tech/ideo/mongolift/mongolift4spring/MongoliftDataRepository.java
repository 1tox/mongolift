package tech.ideo.mongolift.mongolift4spring;

import org.bson.BsonDocument;

import java.util.List;

public interface MongoliftDataRepository {

    void insert(String collectionName, List<BsonDocument> bsonDocuments);

    void removeAll(String collectionName);

    void execute(String collectionName, List<BsonDocument> bsonDocuments);

    void updateIndexes(String collectionName, List<BsonDocument> bsonDocuments);

}
