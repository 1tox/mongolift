package tech.ideo.mongolift.mongolift4spring;

import org.springframework.data.mongodb.core.mapping.Document;

@Document("migrations")
public class MigrationMetadataEntity extends MigrationMetadata {
}
