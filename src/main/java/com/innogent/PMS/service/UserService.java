package com.innogent.PMS.service;

import com.innogent.PMS.dto.ChangePasswordRequest;
import com.innogent.PMS.dto.UserDto;
import com.innogent.PMS.entities.User;
import com.innogent.PMS.exception.customException.NoSuchUserExistsException;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface UserService {
    //To register new user
    public UserDto register(UserDto userDto);
    //get all users
    public ResponseEntity<List<User>> getALL();
    //get user by userId
    public ResponseEntity<User> getEmployeeById(Integer empId) throws NoSuchUserExistsException;
    //update particular user
    public ResponseEntity<String> updateUser(UserDto userDto,Integer userId);
    //login
    public Optional<User> signIn(String email, String password);
    //forgot password
    public Optional<User> updatePassword(String email, String newPassword);

    public User userByEmail(String email);
    //soft delete
    public ResponseEntity<String> deleteUser(Integer userId);

    //by email set aboutMe
    public User updateUserAboutMe(String email, String aboutMe);

    //by email and oldPassword change the password
    void changePassword(String email, ChangePasswordRequest request);

    public ResponseEntity<?> getAllEmployeesOfManager(Integer userId);

    public List<String> getAllActiveUserEmails();

    //get all user with managerEmail
    public ResponseEntity<List<UserDto>> getAllUsersWithManagerEmail();
}
