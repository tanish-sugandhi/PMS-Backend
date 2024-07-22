package com.innogent.PMS.config;

import com.innogent.PMS.service.EmailService;
import com.innogent.PMS.service.ProgressTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EmailScheduler {
    @Autowired
    private EmailService emailService;

    private ProgressTrackingService progressTrackingService;

    private static final Long MANAGER_ID = 3L; // Replace with actual manager ID

    @Scheduled(cron="0 0 9 L * ?")
    public void sendMonthlyEmail(){
        if(progressTrackingService.areNotesUploadedForLastMonth(MANAGER_ID)){
            String subject="Meeting Notes And Application Reminder";
            String text= "Dear Manager,\n\n" +
                    "This is a reminder that the notes from last month's meeting are not yet uploaded. ";
            String to="tanish.sugandhi@gmail.com";
            emailService.sendEmail(to, subject, text);
        }
    }
}
