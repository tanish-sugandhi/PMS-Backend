package com.innogent.PMS.controller;

import com.innogent.PMS.auth.AuthenticationService;
import com.innogent.PMS.dto.UserDto;
import com.innogent.PMS.entities.User;
import com.innogent.PMS.exception.GenericException;
import com.innogent.PMS.exception.customException.NoSuchUserExistsException;
import com.innogent.PMS.request.AuthenticationRequest;
import com.innogent.PMS.response.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthenticationController {

    private final AuthenticationService service;

//    @PostMapping("/authenticate")
//    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) throws NoSuchUserExistsException {
//        return ResponseEntity.ok(service.authenticate(request));
//    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> signInUser(@RequestBody AuthenticationRequest request) throws GenericException {
        return ResponseEntity.ok(service.signInUser(request));
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody UserDto request) throws GenericException {
        return ResponseEntity.ok(service.register(request));
    }
}
