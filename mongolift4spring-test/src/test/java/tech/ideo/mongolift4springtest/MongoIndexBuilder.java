package tech.ideo.mongolift4springtest;

import junit.framework.AssertionFailedError;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexField;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.query.Query;

import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.mongodb.core.query.Criteria.where;

public class MongoIndexBuilder {
    private MongoIndexBuilder() {
    }

    public static UsingStep with(MongoTemplate mongoTemplate) {
        return new BuilderSteps(mongoTemplate);
    }

    public interface UsingStep {
        CollectionStep checkThatCollection(String collectionName);
    }

    public interface CollectionStep {
        FieldStep hasIndex(String indexName);

        ProductStep has(int numberOfProducts);
    }

    public interface ProductStep {
        void countriesFromContinent(String continent);
    }

    public interface FieldStep {
        IndexStep withField(String fieldName);
    }

    public interface IndexStep {
        void andDirection(Sort.Direction direction);
    }

    private static class BuilderSteps implements UsingStep, CollectionStep, FieldStep, IndexStep, ProductStep {
        private final MongoTemplate mongoTemplate;
        private String collectionName;
        private String indexName;
        private int numberOfProducts;
        private String fieldName;
        private Sort.Direction direction;

        private BuilderSteps(MongoTemplate mongoTemplate) {
            this.mongoTemplate = mongoTemplate;
        }

        @Override
        public CollectionStep checkThatCollection(String collectionName) {
            this.collectionName = collectionName;
            return this;
        }

        @Override
        public ProductStep has(int numberOfProducts) {
            this.numberOfProducts = numberOfProducts;
            return this;
        }

        @Override
        public void countriesFromContinent(String continent) {
            Query continents = new Query(where("continent").is(continent));
            assertThat(mongoTemplate.count(continents, collectionName)).isEqualTo(numberOfProducts);
        }


        @Override
        public IndexStep withField(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }

        @Override
        public FieldStep hasIndex(String indexName) {
            this.indexName = indexName;
            return this;
        }

        @Override
        public void andDirection(Sort.Direction direction) {
            this.direction = direction;
            execute();
        }

        private void execute() {
            IndexOperations indexOps = mongoTemplate.indexOps(collectionName);
            IndexInfo foundIndex = indexOps.getIndexInfo()
                .stream()
                .filter(byIndexNameEquals(indexName))
                .findFirst().orElseThrow(AssertionFailedError::new);
            assertThat(foundIndex.getIndexFields()
                .stream()
                .filter(byFieldNameEquals(fieldName))
                .findFirst()
                .orElseThrow(AssertionFailedError::new)
                .getDirection())
                .isEqualTo(direction);
        }

        private @NotNull Predicate<IndexField> byFieldNameEquals(String fieldName) {
            return indexField -> indexField.getKey().equals(fieldName);
        }

        private @NotNull Predicate<IndexInfo> byIndexNameEquals(String indexName) {
            return indexInfo -> indexInfo.getName().equals(indexName);
        }
    }
}
