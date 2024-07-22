package com.innogent.PMS.service.Impl;

import com.innogent.PMS.dto.FeedbackRequestDto;
import com.innogent.PMS.entities.FeedbackRequest;
import com.innogent.PMS.entities.User;
import com.innogent.PMS.enums.RequestStatus;
import com.innogent.PMS.exception.customException.NoSuchUserExistsException;
import com.innogent.PMS.mapper.CustomMapper;
import com.innogent.PMS.repository.FeedbackRequestRepository;
import com.innogent.PMS.repository.UserRepository;
import com.innogent.PMS.service.EmailService;
import com.innogent.PMS.service.FeedbackRequestService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Feedback Service implementation class
 */
@Log4j2
@Service
public class FeedbackRequestServiceImpl implements FeedbackRequestService {
    @Autowired
    private FeedbackRequestRepository feedbackRequestRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private CustomMapper customMapper;
    @Autowired
    private UserRepository userRepository;
    @Override
    public FeedbackRequestDto addRequest(FeedbackRequestDto feedbackRequestDto) {
        feedbackRequestDto.setRequestStatus(RequestStatus.REQUESTED);
        FeedbackRequest entity = customMapper.feedbackRequestDtoToEntity(feedbackRequestDto);
        FeedbackRequest result = feedbackRequestRepository.save(entity);
        emailService.sendFeedbackRequestMail(
                result.getFeedbackProvider().getEmail(),"Feedback Request",
                "Mail was sent by "+result.getFeedbackSeeker().getEmail()+
                        "\n\nMessage : "+result.getMessage()
        +"\nClick on the link to give feedback! \n\nLink : http://localhost:3000/feedback/request");
        return customMapper.feedbackRequestEntityToDto(result);
    }
    public List<FeedbackRequestDto> getData(Integer seekerId) throws NoSuchUserExistsException {
        User user =userRepository.findById(seekerId).orElseThrow(()->new NoSuchUserExistsException("User Id is invalid!", HttpStatus.NOT_FOUND));
        List<FeedbackRequest> list = feedbackRequestRepository.findAllByFeedbackSeeker(user);
        return customMapper.feedbackRequestListEntityToDto(list);
    }

    @Override
    public List<FeedbackRequestDto> getProviderData(Integer providerId) throws NoSuchUserExistsException {
        User user = userRepository.findById(providerId).orElseThrow(()->new NoSuchUserExistsException("User Id is invalid!", HttpStatus.NOT_FOUND));
        List<FeedbackRequest> list = feedbackRequestRepository.findAllByFeedbackProvider(user);
        return customMapper.feedbackRequestListEntityToDto(list);
    }

    @Override
    public String updateRequestStatusData(Long requestId) {
        feedbackRequestRepository.updateRequestStatusToProvided( LocalDateTime.now(), requestId);
        return "Updated";
    }
}