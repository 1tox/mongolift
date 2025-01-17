package tech.ideo.mongolift;

import org.bson.BsonDocument;

import java.util.List;

public interface MongoliftDataRepository {

    void insert(List<BsonDocument> bsonDocuments, String collectionName);

    void removeAll(String collectionName);

}
