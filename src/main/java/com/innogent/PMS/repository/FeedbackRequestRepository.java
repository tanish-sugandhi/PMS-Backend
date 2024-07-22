package com.innogent.PMS.repository;

import com.innogent.PMS.entities.FeedbackRequest;
import com.innogent.PMS.entities.User;
import com.innogent.PMS.enums.RequestStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FeedbackRequestRepository  extends JpaRepository<FeedbackRequest,Long> {
    public List<FeedbackRequest> findAllByFeedbackSeeker(User user);

    public List<FeedbackRequest> findAllByFeedbackProvider(User user);

    // to update request status
    @Modifying
    @Transactional
    @Query("UPDATE FeedbackRequest f SET f.requestStatus = 'PROVIDED', f.provideDate = :now WHERE f.requestId = :requestId")
    void updateRequestStatusToProvided(@Param("now") LocalDateTime now, @Param("requestId") Long requestId);
//    @Query(value = "UPDATE feedback_request SET request_status='PROVIDED', provide_date=:now WHERE request_id =:request_id", nativeQuery = true)
//    public void updateRequestStatusToProvided( @Param("now") LocalDateTime now, @Param("request_id") Long request_id);
}