package com.innogent.PMS.service;

import com.innogent.PMS.dto.StageTimeLineDto;
import com.innogent.PMS.entities.Stage;
import com.innogent.PMS.entities.Timeline;
import com.innogent.PMS.entities.TimelineCycle;
import com.innogent.PMS.exception.GenericException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface StageTimeLineService {
    public Stage createStage(StageTimeLineDto stageTimeLineDto);
    Stage updateStage(Integer timelineId,StageTimeLineDto stageTimeLineDto);
    Stage getStageByName(String stageName);
    List<Stage> getAllStages();
    public List<StageTimeLineDto> getTimelinesByStageName(String stageName);
    List<StageTimeLineDto> getAllTimelinesWithStages();
    public List<StageTimeLineDto> getActiveTimelines(LocalDateTime currentDate);
    List<StageTimeLineDto> getTimelinesByTimelineCycleId(Integer timelineCycleId);
    //to get the id of the performance cycle timeline using current date
    public Integer getCurrentCycleId();
    // all data of timeline cycle
    public List<TimelineCycle> getAllTimelineCycleData();
}
