package com.innogent.PMS.config;

import com.innogent.PMS.enums.GoalStatus;
import com.innogent.PMS.enums.StageName;
import com.innogent.PMS.service.GoalService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
@Log4j2
public class GoalScheduler {
    @Autowired
    private GoalService goalService;

    // Scheduler method
    @Scheduled(cron = "0 0 0 * * ?") //run once a day at midnight
//    @Scheduled(cron = "*/1 * * * * ?") //for each sec for testing
    public void scheduledUpdateGoalStatuses() {
        log.info("Scheduled update of Goal Statuses started");
        goalService.updateGoalStatus(StageName.GOAL_APPROVAL, GoalStatus.APPROVED); // Pass the appropriate status here
        goalService.updateGoalStatusToInProgress(StageName.GOAL_EXECUTION, GoalStatus.IN_PROGRESS); // Pass the appropriate status here
    }
}
