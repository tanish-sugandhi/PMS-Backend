package com.innogent.PMS.auth;

import com.innogent.PMS.dto.UserDto;
import com.innogent.PMS.entities.User;
import com.innogent.PMS.exception.GenericException;
import com.innogent.PMS.exception.customException.NoSuchUserExistsException;
import com.innogent.PMS.mapper.CustomMapper;
import com.innogent.PMS.repository.RoleRepository;
import com.innogent.PMS.repository.UserRepository;
import com.innogent.PMS.request.AuthenticationRequest;
import com.innogent.PMS.response.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final CustomMapper customMapper;
    private final RoleRepository roleRepository;

    public UserDto register(UserDto userDto){
        var user = customMapper.userDtoToEntity(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(roleRepository.findByName(userDto.getRole().getName()));
        return customMapper.userEntityToDto(repository.save(user));
    }
    public AuthenticationResponse signInUser(AuthenticationRequest request) throws GenericException {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new GenericException("Incorrect username or password", e, HttpStatus.UNAUTHORIZED);
        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
//        User Match, Authentication Successful
        final String jwtToken = jwtService.generateToken(userDetails);
//        Token Generated
        return AuthenticationResponse.builder().token(jwtToken)
                .userId(""+repository.findByEmail(request.getEmail())
                .orElseThrow(
                ()
                        -> new NoSuchUserExistsException(
                        "NO USER PRESENT WITH EMAIL = "+request.getEmail(), HttpStatus.NOT_FOUND)).getUserId()).build();
    }
}
