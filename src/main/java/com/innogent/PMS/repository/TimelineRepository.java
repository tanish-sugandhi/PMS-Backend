package com.innogent.PMS.repository;

import com.innogent.PMS.entities.Timeline;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TimelineRepository extends JpaRepository<Timeline, Integer> {
      List<Timeline> findByStartDateBeforeAndEndDateAfter(LocalDateTime currentDate, LocalDateTime currentDate2);
      List<Timeline> findByStageId(Long stageId);
      List<Timeline> findByTimelineCycle_TimelineCycleId(Integer timelineCycleId);
}
