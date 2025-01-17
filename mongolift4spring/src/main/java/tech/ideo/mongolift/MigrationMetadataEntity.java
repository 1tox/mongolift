package tech.ideo.mongolift;

import org.springframework.data.mongodb.core.mapping.Document;

@Document("migrations")
public class MigrationMetadataEntity extends MigrationMetadata {
}
