package tech.ideo.mongolift;

import tech.ideo.mongolift.commands.CommandName;

import java.util.Optional;

public interface MongoliftMetadataRepository {
    Optional<MigrationMetadata> findFirstByPlanNameAndFileNameAndCommandOrderByIdDesc(String planName, String fileName, CommandName command);
}
