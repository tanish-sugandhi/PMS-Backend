package com.innogent.PMS.service.Impl;

import com.innogent.PMS.dto.StageTimeLineDto;
import com.innogent.PMS.entities.Stage;
import com.innogent.PMS.entities.Timeline;
import com.innogent.PMS.entities.TimelineCycle;
import com.innogent.PMS.enums.StageName;
import com.innogent.PMS.exception.GenericException;
import com.innogent.PMS.repository.StageRepository;
import com.innogent.PMS.repository.TimelineCycleRepository;
import com.innogent.PMS.repository.TimelineRepository;
import com.innogent.PMS.service.StageTimeLineService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class StageTimeLineServiceImpl implements StageTimeLineService {
    private static final Logger logger = Logger.getLogger(StageTimeLineServiceImpl.class.getName());
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(StageTimeLineServiceImpl.class);

    @Autowired
    private StageRepository stageRepository;

    @Autowired
    private TimelineRepository timelineRepository;

    @Autowired
    private TimelineCycleRepository timelineCycleRepository;

    @Override
    public Stage createStage(StageTimeLineDto stageTimeLineDto) {
        // Find or create the Stage
        Stage stage;
        Optional<Stage> existingStage = stageRepository.findByStageName(StageName.valueOf(stageTimeLineDto.getStageName()));
        if (existingStage.isPresent()) {
            stage = existingStage.get();
        } else {
            stage = new Stage();
            stage.setStageName(StageName.valueOf(stageTimeLineDto.getStageName()));
            stage = stageRepository.save(stage);
        }

        LocalDate startDate = stageTimeLineDto.getStartDate().toLocalDate();
        LocalDate endDate = stageTimeLineDto.getEndDate().toLocalDate();

        // Find or create the TimelineCycle based on the given date range
        TimelineCycle timelineCycle = timelineCycleRepository
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqual(startDate, endDate)
                .orElseGet(() -> {
                    TimelineCycle newTimelineCycle = new TimelineCycle();
                    newTimelineCycle.setStartDate(startDate);
                    newTimelineCycle.setEndDate(endDate);
                    return timelineCycleRepository.save(newTimelineCycle);
                });

        // Create and save the Timeline
        Timeline timeline = new Timeline();
        timeline.setStartDate(stageTimeLineDto.getStartDate());
        timeline.setEndDate(stageTimeLineDto.getEndDate());
        timeline.setStageId(stage.getStageId());
        timeline.setDescription(stageTimeLineDto.getDescription());
        timeline.setIsActive(stageTimeLineDto.getIsActive());
        timeline.setTimelineCycle(timelineCycle);

        timelineRepository.save(timeline);

        return stage;
    }

    @Override
    public Stage updateStage(Integer timelineId, StageTimeLineDto stageTimeLineDto) {
        logger.info("Updating Stage with StageTimeLineDto: " + stageTimeLineDto.toString());
        Timeline timeline = timelineRepository.findById(timelineId)
                .orElseThrow(() -> new IllegalArgumentException("Timeline with id " + timelineId + " not found"));
        Stage stage = stageRepository.findById(timeline.getStageId())
                .orElseThrow(() -> new IllegalArgumentException("Stage with id " + timeline.getStageId() + " not found"));
        timeline.setStartDate(stageTimeLineDto.getStartDate());
        timeline.setEndDate(stageTimeLineDto.getEndDate());
        timeline.setDescription(stageTimeLineDto.getDescription());
        timeline.setIsActive(stageTimeLineDto.getIsActive());
        if (StageName.PERFORMANCE_CYCLE.name().equals(stageTimeLineDto.getStageName())) {
            TimelineCycle timelineCycle = timeline.getTimelineCycle();
            if (timelineCycle != null) {
                timelineCycle.setStartDate(stageTimeLineDto.getStartDate().toLocalDate());
                timelineCycle.setEndDate(stageTimeLineDto.getEndDate().toLocalDate());
                timelineCycleRepository.save(timelineCycle);
            }
        }
        timelineRepository.save(timeline);
        return stageRepository.save(stage);
    }

    @Override
    public Stage getStageByName(String stageName) {
        return stageRepository.findByStageName(StageName.valueOf(stageName))
                .orElseThrow(() -> new IllegalArgumentException("Stage with name " + stageName + " not found"));
    }

    @Override
    public List<Stage> getAllStages() {
        return stageRepository.findAll();
    }

    @Override
    public List<StageTimeLineDto> getTimelinesByStageName(String stageName) {
        Stage stage = stageRepository.findByStageName(StageName.valueOf(stageName.toUpperCase()))
                .orElseThrow(() -> new RuntimeException("Stage not found"));
        List<Timeline> timelines = timelineRepository.findByStageId(stage.getStageId());
        return timelines.stream()
                .map(timeline -> {
                    StageTimeLineDto dto = new StageTimeLineDto();
                    dto.setStageId(timeline.getStageId());
                    dto.setTimelineId(timeline.getTimelineId());
                    dto.setStageName(stage.getStageName().name());
                    dto.setDescription(timeline.getDescription());
                    dto.setStartDate(timeline.getStartDate());
                    dto.setEndDate(timeline.getEndDate());
                    dto.setIsActive(timeline.getIsActive());
                    dto.setTimelineCycleId(timeline.getTimelineCycle().getTimelineCycleId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<StageTimeLineDto> getAllTimelinesWithStages() {
        List<Timeline> timelines = timelineRepository.findAll();
        return timelines.stream()
                .map(timeline -> {
                    StageTimeLineDto dto = new StageTimeLineDto();
                    dto.setTimelineId(timeline.getTimelineId());
                    dto.setStartDate(timeline.getStartDate());
                    dto.setEndDate(timeline.getEndDate());
                    dto.setDescription(timeline.getDescription());
                    dto.setIsActive(timeline.getIsActive());
                    dto.setStageId(timeline.getStageId());
                    dto.setTimelineCycleId(timeline.getTimelineCycle().getTimelineCycleId());
                    stageRepository.findById(timeline.getStageId())
                            .ifPresent(stage -> dto.setStageName(stage.getStageName().getDisplayName()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<StageTimeLineDto> getActiveTimelines(LocalDateTime currentDate) {
        List<Timeline> timelines = timelineRepository.findByStartDateBeforeAndEndDateAfter(currentDate, currentDate);
        return timelines.stream().map(timeline -> {
            StageTimeLineDto dto = new StageTimeLineDto();
            dto.setStageId(timeline.getStageId());
            dto.setStageName(stageRepository.findById(timeline.getStageId()).orElse(new Stage()).getStageName().name());
            dto.setTimelineId(timeline.getTimelineId());
            dto.setDescription(timeline.getDescription());
            dto.setStartDate(timeline.getStartDate());
            dto.setEndDate(timeline.getEndDate());
            dto.setTimelineCycleId(timeline.getTimelineCycle().getTimelineCycleId());
            dto.setIsActive(timeline.getIsActive());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<StageTimeLineDto> getTimelinesByTimelineCycleId(Integer timelineCycleId) {
        List<Timeline> timelines = timelineRepository.findByTimelineCycle_TimelineCycleId(timelineCycleId);
        return timelines.stream().map(this::mapToStageTimeLineDto).collect(Collectors.toList());
    }

    @Override
    public Integer getCurrentCycleId() {
        log.info("Current Cycle Id :");
        LocalDateTime currentDate = LocalDateTime.now();
        List<StageTimeLineDto> list = getActiveTimelines(currentDate);
        if(!list.isEmpty()){
            StageTimeLineDto dto = list.stream()
                    .filter(item-> item.getStageName().equalsIgnoreCase(StageName.PERFORMANCE_CYCLE.name()))
                    .findFirst().orElseThrow(()-> new GenericException("Required Performance Cycle Timeline is missing", HttpStatus.NOT_FOUND));
            return dto.getTimelineCycleId();
        }
        return -1;
    }

    @Override
    public List<TimelineCycle> getAllTimelineCycleData() {
        return timelineCycleRepository.findAll();
    }

    private StageTimeLineDto mapToStageTimeLineDto(Timeline timeline) {
        StageTimeLineDto dto = new StageTimeLineDto();
        dto.setStageId(timeline.getStageId());
        dto.setTimelineId(timeline.getTimelineId());
        dto.setStageName(stageRepository.findById(timeline.getStageId()).orElse(new Stage()).getStageName().name());
        dto.setDescription(timeline.getDescription());
        dto.setStartDate(timeline.getStartDate());
        dto.setEndDate(timeline.getEndDate());
        dto.setTimelineCycleId(timeline.getTimelineCycle().getTimelineCycleId());
        dto.setIsActive(timeline.getIsActive());
        return dto;
    }
}
