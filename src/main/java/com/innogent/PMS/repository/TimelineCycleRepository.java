package com.innogent.PMS.repository;

import com.innogent.PMS.entities.TimelineCycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface TimelineCycleRepository extends JpaRepository<TimelineCycle, Integer> {
    Optional<TimelineCycle> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate startDate, LocalDate endDate);
}