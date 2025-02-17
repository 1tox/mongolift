package tech.ideo.mongolift.mongolift4spring;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoliftMetadataRepositoryAdapter extends MongoliftMetadataRepository, MongoRepository<MigrationMetadataEntity, String> {

}