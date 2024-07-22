package com.innogent.PMS.repository;

import com.innogent.PMS.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    public Optional<User> findByEmail(String email);

    @Query("SELECT u.email AS email FROM User u WHERE u.isDeleted = false")
    List<String> findEmailByIsDeletedFalse();

    public List<User> findAllByJob(String job);
}