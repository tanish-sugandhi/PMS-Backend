package com.innogent.PMS.service;

import com.innogent.PMS.dto.FeedbackDto;
import com.innogent.PMS.entities.Feedback;
import com.innogent.PMS.entities.User;
import com.innogent.PMS.enums.EvaluationType;
import com.innogent.PMS.exception.customException.NoSuchUserExistsException;

import java.util.List;

public interface FeedbackService {
    public FeedbackDto saveFeedback(FeedbackDto feedbackDto) throws NoSuchUserExistsException;
    public List<FeedbackDto> getFeedbackByUserId(Integer userId) throws NoSuchUserExistsException;
}
