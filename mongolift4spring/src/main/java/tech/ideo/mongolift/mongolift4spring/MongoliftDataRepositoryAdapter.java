package tech.ideo.mongolift.mongolift4spring;

import org.bson.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.springframework.data.domain.Sort.Direction.ASC;

@Component
public record MongoliftDataRepositoryAdapter(MongoTemplate mongoTemplate) implements MongoliftDataRepository {

    @Override
    public void insert(String collectionName, List<BsonDocument> bsonDocuments) {
        mongoTemplate.insert(bsonDocuments, collectionName);
    }

    @Override
    public void removeAll(String collectionName) {
        mongoTemplate.findAllAndRemove(new Query(), collectionName);
    }

    @Override
    public void execute(String collectionName, List<BsonDocument> bsonDocuments) {
        bsonDocuments
            .stream()
            .map(BsonDocument::toJson)
            .map(Document::parse)
            .forEach(mongoTemplate::executeCommand);
    }

    @Override
    public void updateIndexes(String collectionName, List<BsonDocument> bsonDocuments) {
        List<IndexInfo> existingIndexes = mongoTemplate.indexOps(collectionName)
            .getIndexInfo();
        List<BsonDocument> indexesToRecreate = bsonDocuments.stream()
            .filter(byDocumentWithin(existingIndexes))
            .filter(byIndexDefinitionChanged(existingIndexes))
            .toList();
        Set<String> indexesToDelete = existingIndexes
            .stream()
            .filter(not(byIndexNameEquals("_id_")))
            .filter(byIndexNotWithin(bsonDocuments))
            .map(IndexInfo::getName)
            .collect(toSet());
        indexesToDelete.addAll(indexesToRecreate
            .stream()
            .map(toName())
            .toList());
        List<BsonDocument> indexesToCreate = bsonDocuments.stream()
            .filter(byDocumentNotWithin(existingIndexes))
            .collect(toList());
        indexesToCreate.addAll(indexesToRecreate
            .stream()
            .toList());

        indexesToDelete.forEach(mongoTemplate.indexOps(collectionName)::dropIndex);
        BsonDocument createIndexCommand = indexesToCreate
            .stream()
            .collect(
                () -> new BsonDocument("createIndexes", new BsonString(collectionName)).append("indexes", new BsonArray()),
                (d1, d2) -> d1.getArray("indexes").add(d2),
                BsonDocument::putAll);
        mongoTemplate.executeCommand(createIndexCommand.toJson());

    }

    private static Predicate<IndexInfo> byIndexNameEquals(String indexName) {
        return indexInfo -> indexInfo.getName().equals(indexName);
    }

    private static Function<BsonDocument, String> toName() {
        return bsonDocument -> bsonDocument.get("name").asString().getValue();
    }

    private Predicate<BsonDocument> byIndexDefinitionChanged(List<IndexInfo> existingIndexes) {
        return bsonDocument -> existingIndexes
            .stream()
            .map(toBsonDocument())
            .anyMatch(doc -> !doc.equals(bsonDocument));
    }

    private static Function<IndexInfo, BsonDocument> toBsonDocument() {
        return MongoliftDataRepositoryAdapter::toBsonDocument;
    }

    private static Predicate<BsonDocument> byDocumentNotWithin(List<IndexInfo> existingIndexes) {
        return byDocumentWithin(existingIndexes).negate();
    }

    private static Predicate<IndexInfo> byIndexNotWithin(List<BsonDocument> bsonDocuments) {
        return byIndexWithin(bsonDocuments).negate();
    }

    private static Predicate<IndexInfo> byIndexWithin(List<BsonDocument> bsonDocuments) {
        return indexInfo -> bsonDocuments
            .stream()
            .anyMatch(bsonDocument -> indexInfo.getName().equals(bsonDocument.get("name").asString().getValue()));
    }

    private static BsonDocument toBsonDocument(IndexInfo indexInfo) {
        BsonDocument indexKeyAsBsonDocument = indexInfo.getIndexFields()
            .stream()
            .collect(
                BsonDocument::new,
                (doc, indexField) -> doc.append(indexField.getKey(), new BsonInt32(indexField.getDirection() == ASC ? 1 : -1)),
                BsonDocument::putAll
            );
        return new BsonDocument("name", new BsonString(indexInfo.getName()))
            .append("key", indexKeyAsBsonDocument);
    }

    private static Predicate<BsonDocument> byDocumentWithin(List<IndexInfo> indexes) {
        return bsonDocument -> indexes
            .stream()
            .anyMatch(index -> index.getName().equals(bsonDocument.get("name").asString().getValue()));
    }
}
