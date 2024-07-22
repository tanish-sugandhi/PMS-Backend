package com.innogent.PMS.controller;

import com.innogent.PMS.dto.StageTimeLineDto;
import com.innogent.PMS.entities.Stage;
import com.innogent.PMS.entities.TimelineCycle;
import com.innogent.PMS.exception.GenericException;
import com.innogent.PMS.service.StageTimeLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("api/stages")
@CrossOrigin(origins = "http://localhost:3000")
public class StageTimeLineController {
    @Autowired
    private StageTimeLineService stageTimeLineService;

    @PostMapping("/create")
    public ResponseEntity<Stage> createStage(@RequestBody StageTimeLineDto stageTimeLineDto) {
        Stage stage = stageTimeLineService.createStage(stageTimeLineDto);
        return new ResponseEntity<>(stage, HttpStatus.CREATED);
    }

    @PutMapping("/update/{timelineId}")
    public ResponseEntity<Stage> updateStage(@PathVariable Integer timelineId,@RequestBody StageTimeLineDto stageTimeLineDto) {
        Stage updatedStage = stageTimeLineService.updateStage(timelineId,stageTimeLineDto);

        return new ResponseEntity<>(updatedStage, HttpStatus.OK);
    }

    @GetMapping("/{stageName}")
    public ResponseEntity<Stage> getStageByName(@PathVariable String stageName) {
        Stage stage = stageTimeLineService.getStageByName(stageName);
        return new ResponseEntity<>(stage, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Stage>> getAllStages() {
        List<Stage> stages = stageTimeLineService.getAllStages();
        return new ResponseEntity<>(stages, HttpStatus.OK);
    }

@GetMapping("/timelines/{stageName}")
public List<StageTimeLineDto> getTimelinesByStageName(@PathVariable String stageName) {
    return stageTimeLineService.getTimelinesByStageName(stageName);
}

    @GetMapping("/timeline/all")
    public List<StageTimeLineDto> getAllTimelinesWithStages() {
        return stageTimeLineService.getAllTimelinesWithStages();
    }

    @GetMapping("/byDate")
    public List<StageTimeLineDto> getTimelines(@RequestParam String currentDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime currentDate = LocalDateTime.parse(currentDateTime, formatter);
        List<StageTimeLineDto> activeTimelines = stageTimeLineService.getActiveTimelines(currentDate);
        if(activeTimelines.isEmpty()) {
            throw new RuntimeException("Time finish for all stages");
        }
        return activeTimelines;
    }

    @GetMapping("/timelineCycle/{timelineCycleId}")
    public ResponseEntity<List<StageTimeLineDto>> getTimelinesByTimelineCycleId(@PathVariable Integer timelineCycleId) {
        List<StageTimeLineDto> timelines = stageTimeLineService.getTimelinesByTimelineCycleId(timelineCycleId);
        return new ResponseEntity<>(timelines, HttpStatus.OK);
    }

    // To get current cycle id
    @GetMapping("/timelineCycle/fetchId")
    public ResponseEntity<?> fetchCurrentTimelineCycleId() throws GenericException {
        Integer id = stageTimeLineService.getCurrentCycleId();
        if(id == -1){
            return new ResponseEntity<>("TimeLineCycle is not set for current year", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(id,HttpStatus.OK);
    }

    @GetMapping("/timelineCycle/all")
    public ResponseEntity<List<TimelineCycle>> getAllTimelineCycle() {
        List<TimelineCycle> cycle = stageTimeLineService.getAllTimelineCycleData();
        return new ResponseEntity<>(cycle, HttpStatus.OK);
    }
}
