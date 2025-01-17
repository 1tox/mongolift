package tech.ideo.mongolift;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoliftMetadataRepositoryAdapter extends MongoliftMetadataRepository, MongoRepository<MigrationMetadataEntity, String> {

}
