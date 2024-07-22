package com.innogent.PMS.repository;

import com.innogent.PMS.entities.Stage;
import com.innogent.PMS.enums.StageName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StageRepository extends JpaRepository<Stage, Long> {
    Optional<Stage> findByStageName(StageName stageName);
}
