package com.innogent.PMS.repository;

import com.innogent.PMS.entities.Feedback;
import com.innogent.PMS.entities.User;
import com.innogent.PMS.enums.EvaluationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback,Integer> {
//    List<Feedback> findByUserUserId(Integer userId);
//    List<Feedback> findByFeedbackTypeAndUserUserId(EvaluationType feedbackType, Integer userId);

    public List<Feedback> findByUser(User user);
}
