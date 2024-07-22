package com.innogent.PMS.controller;

import com.innogent.PMS.dto.FeedbackDto;
import com.innogent.PMS.entities.Feedback;
import com.innogent.PMS.enums.EvaluationType;
import com.innogent.PMS.exception.customException.NoSuchFeedbackExistsException;
import com.innogent.PMS.exception.customException.NoSuchUserExistsException;
import com.innogent.PMS.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * All Feedback module related api's
 */
@RestController
@RequestMapping("/api/feedback")
@CrossOrigin
public class  FeedbackController {
    @Autowired
    private FeedbackService feedbackService;
    /**
     * Save Feedback
     * @param feedbackDto
     * @return feedbackDto if saved successfully else user defined message
     */
    @PostMapping("/save")
    public ResponseEntity<?> createFeedback(@RequestBody FeedbackDto feedbackDto) throws NoSuchUserExistsException {
        FeedbackDto savedFeedback = feedbackService.saveFeedback(feedbackDto);
        if(savedFeedback == null) return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Some Internal issue while adding feedback!");
        return ResponseEntity.ok(savedFeedback);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getFeedbackByUserId(@PathVariable Integer userId) throws NoSuchUserExistsException {
            List<FeedbackDto> feedbackList = feedbackService.getFeedbackByUserId(userId);
        if (feedbackList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new NoSuchFeedbackExistsException("No Feedback present for requested user!", HttpStatus.NOT_FOUND).getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body(feedbackList);
    }
}


