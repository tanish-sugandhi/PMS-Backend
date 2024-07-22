package com.innogent.PMS.controller;

import com.innogent.PMS.dto.FeedbackRequestDto;
import com.innogent.PMS.exception.GenericException;
import com.innogent.PMS.exception.customException.NoSuchUserExistsException;
import com.innogent.PMS.repository.FeedbackRequestRepository;
import com.innogent.PMS.service.FeedbackRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedbackRequest")
@CrossOrigin
public class FeedbackRequestController {
    @Autowired
    private FeedbackRequestService feedbackRequestService;

    @PostMapping("/add")
    public ResponseEntity<?> addFeedbackRequest(@RequestBody FeedbackRequestDto feedbackRequestDto) throws GenericException {
        FeedbackRequestDto result = feedbackRequestService.addRequest(feedbackRequestDto);
        if(result==null) throw new GenericException("Internal Server Error!",HttpStatus.EXPECTATION_FAILED);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/get/{userId}")
    public ResponseEntity<?> getFeedbackRequests(@PathVariable Integer userId) throws NoSuchUserExistsException {
        return ResponseEntity.ok(feedbackRequestService.getData(userId));
    }

    @GetMapping("/get/provider/{providerId}")
    public ResponseEntity<?> getFeedbackRequestsForProvider(@PathVariable Integer providerId) throws NoSuchUserExistsException {
        return ResponseEntity.ok(feedbackRequestService.getProviderData(providerId));
    }

    @GetMapping("/updateStatus/{requestId}")
    public ResponseEntity<?> updateFeedbackRequestsStatus(@PathVariable Long requestId){
        return ResponseEntity.ok(feedbackRequestService.updateRequestStatusData(requestId));
    }
}
