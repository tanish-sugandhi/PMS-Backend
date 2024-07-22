package com.innogent.PMS.service.Impl;

import com.innogent.PMS.dto.GoalDto;
import com.innogent.PMS.dto.StageTimeLineDto;
import com.innogent.PMS.entities.Goal;
import com.innogent.PMS.entities.User;
import com.innogent.PMS.enums.GoalStatus;
import com.innogent.PMS.enums.StageName;
import com.innogent.PMS.exception.GenericException;
import com.innogent.PMS.exception.customException.NoSuchGoalExistsException;
import com.innogent.PMS.exception.customException.NoSuchUserExistsException;
import com.innogent.PMS.mapper.CustomMapper;
import com.innogent.PMS.repository.GoalRepository;
import com.innogent.PMS.repository.UserRepository;
import com.innogent.PMS.service.EmailService;
import com.innogent.PMS.service.GoalService;
import com.innogent.PMS.service.StageService;
import com.innogent.PMS.service.StageTimeLineService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {
    private static final Logger log = LoggerFactory.getLogger(GoalServiceImpl.class);
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final CustomMapper customMapper;
    private final StageService stageService;
    private final EmailService emailService;
    private final StageTimeLineService stageTimeLineService;

    @Override
    public GoalDto addPersonalGoal(GoalDto goalDto, Integer userId) throws GenericException {
        log.info("Performing personal goal addition!");
        User user = userRepository.findById(goalDto.getUserId())
                .orElseThrow(() -> new NoSuchUserExistsException("No User Present With Id: " + goalDto.getUserId(), HttpStatus.NOT_FOUND));
        Goal goal = customMapper.goalDtoToEntity(goalDto);
        goal.setUser(user);
        goal.setGoalStatus(GoalStatus.CREATED);
        goal.setPerformanceCycleId(stageTimeLineService.getCurrentCycleId());
        if (goal.getGoalId() == null) {
            Goal result = goalRepository.save(goal);
            return customMapper.goalEntityToGoalDto(result);
        }
        throw new RuntimeException("rewriting data may be caused!");
    }

    @Override
    public GoalDto addOrganisationalGoal(GoalDto goalDto, Integer managerId) {
        log.info("Performing Organisational goal addition!");
        User user = userRepository.findById(goalDto.getUserId()).orElseThrow(() -> new NoSuchUserExistsException("No User Present With Id : " + goalDto.getUserId(), HttpStatus.NOT_FOUND));
        if (!user.getManagerId().equals(managerId) || !goalDto.getGoalType().name().equalsIgnoreCase("ORGANISATIONAL")) {
            throw new NoSuchUserExistsException("Only Manager Can Set Organisational Goal, Please manager id of user with id : "+user.getManagerId(), HttpStatus.PROXY_AUTHENTICATION_REQUIRED);
        }
        Goal goal = customMapper.goalDtoToEntity(goalDto);
        goal.setUser(user);
        goal.setGoalStatus(GoalStatus.CREATED);
        goal.setPerformanceCycleId(stageTimeLineService.getCurrentCycleId());
        Goal result = goalRepository.save(goal);
        return customMapper.goalEntityToGoalDto(result);
    }

    @Override
    public GoalDto findGoalByGoalId(Long goalId) {
        Optional<Goal> optional = goalRepository.findById(goalId);
        if(optional.isEmpty()){
            throw new NoSuchGoalExistsException("No Goal Data Present With goal id : "+goalId, HttpStatus.NOT_FOUND);
        }
        return optional.map(customMapper::goalEntityToGoalDto).get();
    }

    @Override
    public GoalDto editGoal(Long goalId, GoalDto goalDto) {
        Optional<Goal> optional = goalRepository.findById(goalId);
        if (optional.isEmpty())
            throw new NoSuchGoalExistsException("No Goal Present With Id : " + goalId, HttpStatus.NOT_FOUND);
        Goal goal = customMapper.goalDtoToEntity(goalDto);
        goal.setGoalId(goalId);
        goal.setUser(optional.get().getUser());
        goal.setSetDate(optional.get().getSetDate());
        goal.setPerformanceCycleId(optional.get().getPerformanceCycleId());
        log.info("Sending email to hr on employee goal update!");
        GoalDto result = customMapper.goalEntityToGoalDto(goalRepository.save(goal));
        List<User> hrList = userRepository.findAllByJob("HR");
        for (User hr : hrList) {
            emailService.sendEmail(hr.getEmail(), "Employee Goals Updated\n",
                    "Manager has updated " + goal.getUser().getEmail() + " goal data!" + "\n\nPrevious data : " + optional.get()
                            + "\n\nUpdated Data : " + result);
        }
        return result;
    }

    @Override
    public List<GoalDto> listAllGoalsOfEmployee(Integer userId) throws NoSuchUserExistsException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NoSuchUserExistsException("User Not exist with provided id : " + userId, HttpStatus.NOT_FOUND);
        }
        // Filter out goal with isDeleted true
        List<Goal> goalsList = goalRepository.findAllByUser(user.get());
        List<Goal> activeGoals = goalsList.stream()
                .filter(goal -> !goal.isDeleted())
                .toList();
        return customMapper.goalListToGoalDto(activeGoals);
    }

    @Override
    public ResponseEntity<?> deleteGoal(Long goalId) throws NoSuchGoalExistsException {
        Optional<Goal> optionalGoal = goalRepository.findById(goalId);
        if (optionalGoal.isPresent()) {
            Goal goal = optionalGoal.get();
            goal.setDeleted(true);
            goalRepository.save(goal);
            return ResponseEntity.ok("Goal deleted successfully");
        } else {
            throw new NoSuchGoalExistsException("No Goal Present or Id is invalid : " + goalId, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public GoalDto addSelfFeedback(Long goalId, GoalDto goalDto) throws NoSuchGoalExistsException {
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> new NoSuchGoalExistsException("User Not exist with provided id : " + goalId, HttpStatus.NOT_FOUND));
        goal.setSelfRating(goalDto.getSelfRating());
        goal.setSelfComments(goalDto.getSelfComments());
        goal.setSelfFeedbackDate(LocalDateTime.now());
        Goal result = goalRepository.save(goal);
        GoalDto mapped = customMapper.goalEntityToGoalDto(result);
        return mapped;
    }

    @Override
    public GoalDto addManagerFeedback(long goalId, GoalDto goalDto) throws NoSuchGoalExistsException {
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> new NoSuchGoalExistsException("User Not exist with provided id : " + goalId, HttpStatus.NOT_FOUND));
        goal.setManagerRating(goalDto.getManagerRating());
        goal.setManagerComments(goalDto.getManagerComments());
        goal.setManagerFeedbackDate(LocalDateTime.now());
        Goal result = goalRepository.save(goal);
        GoalDto mapped = customMapper.goalEntityToGoalDto(result);
        return mapped;
    }

    @Override
    public void updateGoalStatus(StageName timelineStage, GoalStatus status) {
        log.info("Updating Goal Status");
        LocalDateTime date = LocalDateTime.now();

        List<StageTimeLineDto> timelines = stageTimeLineService.getActiveTimelines(date);
        if (timelines == null || timelines.isEmpty()) {
            log.warn("No active timelines found.");
            return;
        }
        // Process each timeline
        for (StageTimeLineDto timeline : timelines) {
            if (timelineStage.name().equals(timeline.getStageName())) {
                List<Goal> allGoals = goalRepository.findAll();
                // updating the goal's status
                for (Goal goal : allGoals) {
                    if (goal.getGoalStatus().equals(GoalStatus.CREATED)) {
                        goal.setGoalStatus(GoalStatus.APPROVED);
                        goalRepository.save(goal); // Save updated goal back to repository
                        log.info("Updated goal id {} to status {}", goal.getGoalId(), status);
                    }
                }
            } else {
                log.info("Timeline is not set in GOAL_APPROVAL stage, skipping.");
            }
        }
    }

    public void updateGoalStatusToInProgress(StageName timelineStage, GoalStatus status) {
        LocalDateTime date = LocalDateTime.now();
        List<StageTimeLineDto> timelines = stageTimeLineService.getActiveTimelines(date);
        if (timelines == null || timelines.isEmpty()) {
            log.warn("No active timelines found.");
            return;
        }
        // Process each timeline
        for (StageTimeLineDto timeline : timelines) {
            if (timelineStage.name().equals(timeline.getStageName())) {
                List<Goal> allGoals = goalRepository.findAll();
                // updating the goal's status
                for (Goal goal : allGoals) {
                    if (goal.getGoalStatus().equals(GoalStatus.APPROVED)) {
                        goal.setGoalStatus(GoalStatus.IN_PROGRESS);
                        goalRepository.save(goal); // Save updated goal back to repository
                        log.info("Updated goal id {} to status {}", goal.getGoalId(), status);
                    }
                }
            }
        }
    }
}