package com.innogent.PMS.controller;

import com.innogent.PMS.dto.ChangePasswordRequest;
import com.innogent.PMS.dto.UserDto;
import com.innogent.PMS.entities.User;
import com.innogent.PMS.exception.customException.NoSuchUserExistsException;
import com.innogent.PMS.service.UserService;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This Rest controller handles user module related api's
 */
@Log4j2
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:3000")
public class MainController {;
    @Autowired
    private UserService userService;

    @PreAuthorize("ADMIN")
    @GetMapping("/home")
    public String home() {

        return "Welcome to Our Performance Manager Application!!";
    }

    //  Register a new user
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDto userDto) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.register(userDto));
    }

    // fetch all users
    @GetMapping("/getAll")
    public ResponseEntity<List<User>> getAll(){
        return this.userService.getALL();
    }

    //fetch all users with managerEmail
    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return userService.getAllUsersWithManagerEmail();
    }
    //get particular user by id
    @GetMapping("/getByEmpId/{empId}")
    public ResponseEntity<User> getEmployeeById(@PathVariable String empId) throws NoSuchUserExistsException {
        return this.userService.getEmployeeById(Integer.parseInt(empId));
    }

    //update user by id
    @PutMapping("/updateUser/{userId}")
    public ResponseEntity<String> updateUser(@RequestBody UserDto userDto,@PathVariable Integer userId) {
        log.info("updating user details!");
        return  this.userService.updateUser(userDto,userId);
    }

    //login
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> signInUser(@RequestBody Map<String, String> request) {
        Optional<User> userOptional = userService.signIn(request.get("email"), request.get("password"));
        Map<String, Object> response = new HashMap<>();
        if (userOptional.isPresent()) {
            response.put("message", "Sign-in successful");
            response.put("user", userOptional.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Invalid email or password");
            return ResponseEntity.status(401).body(response);
        }
    }

    //forgot password
    @PostMapping("/password")
    public ResponseEntity<Map<String, Object>> updatePassword(@RequestBody Map<String, String> request) {
        Optional<User> userOptional = userService.updatePassword(request.get("email"), request.get("newPassword"));
        Map<String, Object> response = new HashMap<>();
        if (userOptional.isPresent()) {
            response.put("message", "Password updated successfully");
            response.put("user", userOptional.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Email not found");
            return ResponseEntity.badRequest().body(response);
        }
    }

    //user by email
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getByUserEmail(@PathVariable String email){
        return ResponseEntity.ok(userService.userByEmail(email));
    }

    //softDelete
    @DeleteMapping("softDelete/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer userId) {
        return userService.deleteUser(userId);
    }

    //to get employees under manager
    @GetMapping("/managerEmployee/{userId}")
    public ResponseEntity<?> getByEmployeesUnderMe(@PathVariable String userId){
        return ResponseEntity.ok(userService.getAllEmployeesOfManager(Integer.parseInt(userId)));
    }
    //by email set aboutME
    @PostMapping("/about/{email}")
    public User updateAboutMe(@PathVariable String email, @RequestBody AboutMeRequest aboutMeRequest) {
        return userService.updateUserAboutMe(email, aboutMeRequest.getAboutMe());
    }

    static class AboutMeRequest {
        private String aboutMe;

        public String getAboutMe() {
            return aboutMe;
        }

        public void setAboutMe(String aboutMe) {
            this.aboutMe = aboutMe;
        }
    }

    //by email change old password
    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePassword(@PathVariable String email, @RequestBody ChangePasswordRequest request) {
        userService.changePassword(email, request);
        return ResponseEntity.status(HttpStatus.OK).body("Password changed successfully");
    }

    //get all the emails of user
    @GetMapping("/active-emails")
    public List<String> getAllActiveUserEmails() {
        return userService.getAllActiveUserEmails();
    }

}