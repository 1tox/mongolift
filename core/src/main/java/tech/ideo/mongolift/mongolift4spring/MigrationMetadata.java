package tech.ideo.mongolift.mongolift4spring;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.BsonObjectId;
import tech.ideo.mongolift.mongolift4spring.commands.CommandName;

import java.util.Date;

@Data
@NoArgsConstructor
public class MigrationMetadata {
    private BsonObjectId id;

    private Date executedAt;

    private String executedBy;

    private long executionTime;

    private String planName;

    private String checksum;

    private String fileName;

    private CommandName command;

    private MigrationStatus status;
}