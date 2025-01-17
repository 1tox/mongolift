package tech.ideo.mongolift.mongolift4spring;

import tech.ideo.mongolift.mongolift4spring.commands.CommandName;

import java.util.Optional;

public interface MongoliftMetadataRepository {
    Optional<MigrationMetadata> findFirstByPlanNameAndFileNameAndCommandOrderByIdDesc(String planName, String fileName, CommandName command);
}