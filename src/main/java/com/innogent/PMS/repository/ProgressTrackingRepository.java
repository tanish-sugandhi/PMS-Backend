package com.innogent.PMS.repository;

import com.innogent.PMS.entities.ProgressTracking;
import com.innogent.PMS.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProgressTrackingRepository extends JpaRepository<ProgressTracking,Long> {
    public List<ProgressTracking> findAllByUser(User user);
    Optional<ProgressTracking> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);

    //Optional<ProgressTracking> findByUser_UserIdAndMonthAndYear(Integer userId,String month,String year);


    @Query("SELECT pt FROM ProgressTracking pt WHERE pt.user.userId = :userId AND pt.month = :month AND pt.year = :year")
    Optional<ProgressTracking> findByUserIdAndMonthAndYearJPQL(
            @Param("userId") Integer userId,
            @Param("month") String month,
            @Param("year") String year
    );
}
