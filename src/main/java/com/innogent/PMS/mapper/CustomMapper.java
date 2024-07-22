package com.innogent.PMS.mapper;

import com.innogent.PMS.dto.*;
import com.innogent.PMS.entities.*;
import com.innogent.PMS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.List;

@Component
public class CustomMapper {
    private ModelMapper modelMapper = new ModelMapper();
    @Autowired
    private UserRepository userRepository;

    //************ goal module mapper methods
    public Goal goalDtoToEntity(GoalDto goalDto){
        Goal goal = new Goal();
        goal.setGoalType(goalDto.getGoalType());
        goal.setDescription(goalDto.getDescription());
        goal.setGoalName(goalDto.getGoalName());
        goal.setMeasurable(goalDto.getMeasurable());
        goal.setGoalStatus(goalDto.getGoalStatus());
        goal.setEndDate(goalDto.getEndDate());
        return goal;
    }
    public GoalDto goalEntityToGoalDto(Goal goal){
        GoalDto goalDto = new GoalDto();
        goalDto.setUserId((goal.getUser() != null)?goal.getUser().getUserId():null);
        goalDto.setDescription(goal.getDescription());
        goalDto.setGoalType(goal.getGoalType());
        goalDto.setSetDate(goal.getSetDate());
        goalDto.setMeasurable(goal.getMeasurable());
        goalDto.setGoalName(goal.getGoalName());
        goalDto.setEndDate(goal.getEndDate());
        goalDto.setGoalId(goal.getGoalId());
        goalDto.setGoalStatus((goal.getGoalStatus()));
        goalDto.setSelfRating((goal.getSelfRating() != null)?goal.getSelfRating():null);
        goalDto.setManagerRating((goal.getManagerRating() != null)?goal.getManagerRating():null);
        goalDto.setSelfComments((goal.getSelfComments()!=null)?goal.getSelfComments():null);
        goalDto.setManagerComments((goal.getManagerComments()!=null)?goal.getManagerComments():null);
        goalDto.setSelfFeedbackDate((goal.getSelfFeedbackDate()!=null)?goal.getSelfFeedbackDate():null);
        goalDto.setManagerFeedbackDate((goal.getManagerFeedbackDate()!=null)?goal.getManagerFeedbackDate():null);
        goalDto.setPerformanceCycleId(goal.getPerformanceCycleId());
        return goalDto;
    }
    //to convert list of goals to list of goalDto
    public List<GoalDto> goalListToGoalDto(List<Goal> goalsList) {
        return goalsList.stream().map(this::goalEntityToGoalDto).toList();
    }
    //************ user module mapper methods
    public User userDtoToEntity (UserDto userDto){
        User user = modelMapper.map(userDto, User.class);
        user.setRole(userDto.getRole());
        return user;
    }
    public UserDto userEntityToDto (User user){
        UserDto userDto = modelMapper.map(user, UserDto.class);
        userDto.setRole(user.getRole());
        return userDto;
    }
    //progress tracking module mapper methods
    public ProgressTracking progressTrackingDtoToEntity(ProgressTrackingDto progressTrackingDto)
    {
        return modelMapper.map(progressTrackingDto, ProgressTracking.class);
    }

    public ProgressTrackingDto  progressEntityToProgressTrackingDto(ProgressTracking progressTracking)
    {
        ProgressTrackingDto dto = new ProgressTrackingDto();
        dto.setMeetingId(progressTracking.getMeetingId());
        if(progressTracking.getUser() != null) dto.setUserId(progressTracking.getUser().getUserId());
        //dto.setDate(progressTracking.getDate());
        dto.setDate(LocalDate.from(progressTracking.getDate()));
        dto.setTitle(progressTracking.getTitle());
        dto.setNotes(progressTracking.getNotes());
        dto.setRecording(progressTracking.getRecording());
        dto.setMonth(progressTracking.getMonth());
        dto.setYear((progressTracking.getYear()));
        if(progressTracking.getUser() != null) dto.setManagerId(progressTracking.getUser().getManagerId());
        return dto;
    }
    //convert list to dto
    public List<ProgressTrackingDto> convertListToDto(List<ProgressTracking> entityList)
    {
        return entityList.stream()
                .map(this::progressEntityToProgressTrackingDto)
                .toList();

    }

    /**
     * @param dto to entity mapping
     * @return feedback if saved else null
     */
    public Feedback feedbackDtoToEntity(FeedbackDto dto){
        Feedback feedback = new Feedback();
        feedback.setFeedbackDate(dto.getFeedbackDate());
        feedback.setFeedbackType(dto.getFeedbackType());
        feedback.setComments(dto.getComments());
        feedback.setRating(dto.getRating());
        return feedback;
    }

    public FeedbackDto feedbackEntityToDto(Feedback feedback) {
        FeedbackDto feedbackDto = new  FeedbackDto();
        feedbackDto.setFeedbackId((feedback.getFeedbackId()!=null)?feedback.getFeedbackId():null);
        feedbackDto.setUserId((feedback.getUser().getUserId()!=null)?feedback.getUser().getUserId():null);
        feedbackDto.setProviderId((feedback.getProvider().getUserId()!=null)?feedback.getProvider().getUserId():null);
        User provider = (feedbackDto.getProviderId()!=null)?userRepository.findById(feedbackDto.getProviderId()).get():null;
        feedbackDto.setProviderName((provider!=null)?provider.getFirstName()+" "+provider.getLastName():null);
        feedbackDto.setFeedbackType(feedback.getFeedbackType());
        feedbackDto.setFeedbackDate(feedback.getFeedbackDate());
        feedbackDto.setComments(feedback.getComments());
        feedbackDto.setRating(feedback.getRating());
        return feedbackDto;
    }

    public List<FeedbackDto> feedbackListEntityToDto(List<Feedback> list) {
        return list.stream().map(this::feedbackEntityToDto).toList();
    }

    public FeedbackRequest feedbackRequestDtoToEntity(FeedbackRequestDto dto){
        FeedbackRequest entity = new FeedbackRequest();
        entity.setMessage(dto.getMessage());
        entity.setRequestDate(dto.getRequestDate());
        entity.setProvideDate(dto.getProvideDate());
        entity.setRequestStatus(dto.getRequestStatus());
        User seeker = (dto.getFeedbackSeekerId()!=null)?userRepository.findById(dto.getFeedbackSeekerId()).get():null;
        entity.setFeedbackSeeker(seeker);
        User provider = (dto.getFeedbackProviderEmail()!=null)?userRepository.findByEmail(dto.getFeedbackProviderEmail()).get():null;
        entity.setFeedbackProvider(provider);
        return entity;
    }
    public FeedbackRequestDto feedbackRequestEntityToDto(FeedbackRequest entity){
        FeedbackRequestDto dto = new FeedbackRequestDto();
        dto.setRequestId(entity.getRequestId());
        dto.setMessage(entity.getMessage());
        dto.setRequestDate(entity.getRequestDate());
        dto.setProvideDate(entity.getProvideDate());
        dto.setRequestStatus(entity.getRequestStatus());
        User seeker = (entity.getFeedbackSeeker()!=null)?userRepository.findById(entity.getFeedbackSeeker().getUserId()).get():null;
        if(seeker != null){
            dto.setFeedbackSeekerId(seeker.getUserId());
            dto.setFeedbackSeekerName(seeker.getFirstName()+" "+seeker.getLastName());
        }
        User provider = (entity.getFeedbackProvider()!=null)?userRepository.findById(entity.getFeedbackProvider().getUserId()).get():null;
        if(provider != null){
            dto.setFeedbackProviderId(provider.getUserId());
            dto.setFeedbackProviderName(provider.getFirstName()+" "+provider.getLastName());
        }
        return dto;
    }

    public List<FeedbackRequestDto> feedbackRequestListEntityToDto(List<FeedbackRequest> list) {
        return list.stream().map(this::feedbackRequestEntityToDto).toList();
    }
}
