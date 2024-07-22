package com.innogent.PMS.service.Impl;

import com.innogent.PMS.dto.FeedbackDto;
import com.innogent.PMS.entities.Feedback;
import com.innogent.PMS.entities.User;
import com.innogent.PMS.exception.customException.NoSuchUserExistsException;
import com.innogent.PMS.mapper.CustomMapper;
import com.innogent.PMS.repository.FeedbackRepository;
import com.innogent.PMS.repository.GoalRepository;
import com.innogent.PMS.repository.UserRepository;
import com.innogent.PMS.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FeedbackServiceImpl implements FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GoalRepository goalRepository;
    @Autowired
    private CustomMapper customMapper;

    @Override
        public FeedbackDto saveFeedback(FeedbackDto feedbackDto) throws NoSuchUserExistsException {
        User user =userRepository.findById(feedbackDto.getUserId()).orElseThrow(()->new NoSuchUserExistsException("User Id is invalid!", HttpStatus.NOT_FOUND));
        User provider=userRepository.findById(feedbackDto.getProviderId()).orElseThrow(()->new NoSuchUserExistsException("Provider Id is invalid!", HttpStatus.NOT_FOUND));
        Feedback feedback = customMapper.feedbackDtoToEntity(feedbackDto);
        feedback.setUser(user);
        feedback.setProvider(provider);
        feedback.setFeedbackDate(LocalDateTime.now());
        return customMapper.feedbackEntityToDto(feedbackRepository.save(feedback));
    }

    @Override
    public List<FeedbackDto> getFeedbackByUserId(Integer userId) throws NoSuchUserExistsException {
        User user = userRepository.findById(userId).orElseThrow(()->new NoSuchUserExistsException("User doesn't exist with given user id : "+userId, HttpStatus.NOT_FOUND));
        List<Feedback> list = feedbackRepository.findByUser(user);
        if(list.isEmpty()){
            return null;
        }
        return customMapper.feedbackListEntityToDto(list);
    }
}