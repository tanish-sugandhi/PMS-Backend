package com.innogent.PMS.service;

import com.innogent.PMS.dto.GoalDto;
import com.innogent.PMS.enums.GoalStatus;
import com.innogent.PMS.enums.StageName;
import com.innogent.PMS.exception.GenericException;
import com.innogent.PMS.exception.customException.NoSuchGoalExistsException;
import com.innogent.PMS.exception.customException.NoSuchUserExistsException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface GoalService {
    // To create a new personal goal
    public GoalDto addPersonalGoal(GoalDto goal, Integer userId) throws GenericException;
    // To create a new organisational goal
    public GoalDto addOrganisationalGoal(GoalDto goal, Integer managerId) throws GenericException;
    // Retrieve goal by goal id
    public GoalDto findGoalByGoalId(Long goalId);
    // To Update goal  by goal id
    public GoalDto editGoal(Long goalId, GoalDto goalDto) throws NoSuchGoalExistsException;
    // list all goals of an employee
    public List<GoalDto> listAllGoalsOfEmployee(Integer userId) throws NoSuchUserExistsException;
    // delete goal
    public ResponseEntity<?> deleteGoal(Long goalId) throws NoSuchGoalExistsException;
    // to add self feedback on individual goal
    public GoalDto addSelfFeedback(Long goalId, GoalDto goalDto) throws NoSuchGoalExistsException;
    // to add manager feedback on individual goal
    public GoalDto addManagerFeedback(long l, GoalDto goalDto) throws NoSuchGoalExistsException;

    public void updateGoalStatus(StageName timelineStage, GoalStatus status);

    public void updateGoalStatusToInProgress(StageName stageName, GoalStatus goalStatus);
}