package com.innogent.PMS.service.Impl;

import com.innogent.PMS.dto.ChangePasswordRequest;
import com.innogent.PMS.dto.UserDto;
import com.innogent.PMS.entities.Role;
import com.innogent.PMS.entities.User;
import com.innogent.PMS.exception.customException.InvalidPasswordException;
import com.innogent.PMS.exception.customException.NoSuchUserExistsException;
import com.innogent.PMS.exception.customException.UserAlreadyExistsException;
import com.innogent.PMS.mapper.CustomMapper;
import com.innogent.PMS.repository.RoleRepository;
import com.innogent.PMS.repository.UserRepository;
import com.innogent.PMS.service.EmailService;
import com.innogent.PMS.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private CustomMapper customMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;

    public UserDto register(UserDto userDto){
        System.out.println(userDto);
        Optional<User> existingUser = userRepository.findByEmail(userDto.getEmail());
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("User Already Exist With Provided Email!");
//            System.out.println("email already");
//            return null;
        }

        Role existingRole = roleRepository.findByName(userDto.getRole().getName());
        if (existingRole != null) {
            userDto.setRole(existingRole);
        } else {
            roleRepository.save(userDto.getRole());
        }
        Optional<User> manager = userRepository.findByEmail(userDto.getManagerEmail());
        if (userDto.getRole().getName().toString().equals("USER") && manager.isEmpty()) {
            System.out.println("manager email not present");
            throw new RuntimeException("manager email not present");
        }
        User user = customMapper.userDtoToEntity(userDto);
        if(userDto.getRole().getName().toString().equals("USER")){
            user.setManagerId(manager.get().getUserId());
        }
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        sendEmail(userDto.getEmail(),userDto.getPassword(),userDto.getManagerEmail());
        System.out.println("mail send");
        return customMapper.userEntityToDto(userRepository.save(user));
    }
    public void sendEmail(String email,String password,String managerEmail){
        String subject = "Your PMS Account Password";
        String text = "Dear user,\nYou are Successfully added in PMS Application of Innogent team.\nEmail : "+email+"\nManager Email :"+managerEmail+"\nPassword : "+password+"\nPlease change your Password after logging in PMS application.";
        emailService.sendEmail(email,subject,text);
    }


//    public ResponseEntity<List<User>> getALL(){
//        List<User> user = userRepository.findAll();
//        return ResponseEntity.status(HttpStatus.OK).body(user);
//    }
public ResponseEntity<List<User>> getALL() {
    List<User> users = userRepository.findAll();
    // Filter out users with isDeleted true
    List<User> activeUsers = users.stream()
            .filter(user -> !user.isDeleted())
            .collect(Collectors.toList());
    return ResponseEntity.status(HttpStatus.OK).body(activeUsers);
}

    public ResponseEntity<User> getEmployeeById(Integer empId) throws NoSuchUserExistsException {
        return ResponseEntity.ok(userRepository.findById(empId).orElseThrow(()-> new NoSuchUserExistsException("Employee Not Present With Employee Id : "+empId, HttpStatus.NOT_FOUND)));

    }

    public ResponseEntity<String> updateUser(UserDto userDto, Integer userId) {
        Optional<User> existingUserOptional = userRepository.findById(userId);
        if (existingUserOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        Optional<User> userWithSameEmail = userRepository.findByEmail(userDto.getEmail());
        if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }
        Role existingRole = roleRepository.findByName(userDto.getRole().getName());
        if (existingRole != null) {
            userDto.setRole(existingRole);
        } else {
            userDto.setRole(roleRepository.save(userDto.getRole()));
        }
        Optional<User> manager = userRepository.findByEmail(userDto.getManagerEmail());
        if (userDto.getRole().getName().toString().equals("USER") && manager.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Manager email not present");
        }
        User user = existingUserOptional.get();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setContact(userDto.getContact());
        user.setJob(userDto.getJob());
        user.setHiredDate(userDto.getHiredDate());
        user.setRole(userDto.getRole());
        if (userDto.getRole().getName().toString().equals("USER") && manager.isPresent()) {
            user.setManagerId(manager.get().getUserId());
        } else {
            user.setManagerId(null);
        }
        userRepository.save(user);
        return ResponseEntity.ok("User updated successfully");
    }

    public Optional<User>
    signIn(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent() && userOptional.get().getPassword().equals(password)) {
            return userOptional;
        }
        return Optional.empty();
    }

    public Optional<User> updatePassword(String email, String newPassword) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(newPassword);
            userRepository.save(user);
            return Optional.of(user);
        }
        return Optional.empty();
    }

    @Override
    public User userByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new RuntimeException("User not found with email: " + email);
        }
    }

    @Override
    public ResponseEntity<String> deleteUser(Integer userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setDeleted(true);
            userRepository.save(user);
            return ResponseEntity.ok("User deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    public User updateUserAboutMe(String email, String aboutMe) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setAboutMe(aboutMe);
            return userRepository.save(user);
        } else {
            throw new RuntimeException("User with email " + email + " not found");
        }
    }
    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Old password is incorrect");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("New password and confirm password do not match");
        }

        String encryptedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encryptedPassword);
        userRepository.save(user);
    }

    @Override
    public ResponseEntity<?>  getAllEmployeesOfManager(Integer userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            List<User> users = userRepository.findAll();
            // Filter out users with isDeleted true
            List<User> activeUsers = users.stream()
                    .filter(u -> !u.isDeleted())
                    .collect(Collectors.toList());
            assert users != null;
//          System.out.println(users);
            Set<User> result = activeUsers.stream().filter(o -> o.getManagerId()!=null).filter(u -> u.getManagerId().equals(userId)).collect(Collectors.toSet());
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }
    public List<String> getAllActiveUserEmails() {
        return userRepository.findEmailByIsDeletedFalse();
    }

    //get all users by its managerEmail
    public ResponseEntity<List<UserDto>> getAllUsersWithManagerEmail() {
        List<User> users = userRepository.findAll();

        // Filter out users with isDeleted true
        List<User> activeUsers = users.stream()
                .filter(user -> !user.isDeleted())
                .collect(Collectors.toList());

        List<UserDto> userDtos = activeUsers.stream().map(user -> {
            UserDto userDto = mapToUserDto(user);
            if (user.getManagerId() != null) {
                userDto.setManagerEmail(fetchManagerEmail(user.getManagerId()));
            }
            return userDto;
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(userDtos);
    }

    private String fetchManagerEmail(Integer managerId) {
        Optional<User> managerOptional = userRepository.findById(managerId);
        if (managerOptional.isPresent()) {
            return managerOptional.get().getEmail();
        }
        return null; // Or handle as per your business logic
    }

    private UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setUserId(user.getUserId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setPassword(user.getPassword());
        userDto.setContact(user.getContact());
        userDto.setJob(user.getJob());
        userDto.setHiredDate(user.getHiredDate());
        userDto.setManagerId(user.getManagerId());
        userDto.setRole(user.getRole());
        return userDto;
    }
}
