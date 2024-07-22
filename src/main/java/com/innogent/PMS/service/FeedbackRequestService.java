package com.innogent.PMS.service;

import com.innogent.PMS.dto.FeedbackRequestDto;
import com.innogent.PMS.exception.customException.NoSuchUserExistsException;

import java.util.List;

public interface FeedbackRequestService {
    public FeedbackRequestDto addRequest(FeedbackRequestDto feedbackRequestDto);
    public List<FeedbackRequestDto> getData(Integer seekerId) throws NoSuchUserExistsException;
    public List<FeedbackRequestDto> getProviderData(Integer providerId) throws NoSuchUserExistsException;
    public String updateRequestStatusData(Long requestId);
}
