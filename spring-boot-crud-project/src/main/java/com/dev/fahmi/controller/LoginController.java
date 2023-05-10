package com.dev.fahmi.controller;

import com.dev.fahmi.common.JwtUtil;
import com.dev.fahmi.domain.User;
import com.dev.fahmi.dto.LoginRequest;
import com.dev.fahmi.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
public class LoginController {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // load the user details from the database
            Optional<User> user = userDetailsService.findByUsername(loginRequest.getUsername());

            if(user.isEmpty()){
                throw new NoSuchElementException("User not found with username: " + loginRequest.getUsername());
            }

            User userDetails = user.get();
            // check if the username and password are valid
            if (!userDetails.getPassword().equals(loginRequest.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            String getToken = jwtUtil.getToken(userDetails.getUsername());
            var token = Map.of("token", getToken);
            return ResponseEntity.ok(token);

        } catch (UsernameNotFoundException ex) {
            // return a failure response if the username is not found
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }

    @PostMapping("/register")
    public User registerUser(@RequestBody User user, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null && userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return userDetailsService.saveUser(user);
        }
            else {
                throw new AccessDeniedException("You do not have permission to perform this operation.");
            }
    }

    @GetMapping("/user")
    public List<User> getAllUsers(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null && userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return userDetailsService.getAllUsers();
        }
        else {
            throw new AccessDeniedException("You do not have permission to perform this operation.");
        }
    }

    @GetMapping("user/{id}")
    public User getUserById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null && userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return userDetailsService.getUserById(id);
        } else {
            throw new AccessDeniedException("You do not have permission to perform this operation.");
        }
    }

    @DeleteMapping("user/{id}")
    public void deleteUserById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null && userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {

            userDetailsService.deleteUserById(id);
        } else {
            throw new AccessDeniedException("You do not have permission to perform this operation.");
        }
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // This returns a response entity with the exception message and a status of NOT_FOUND
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); // This returns a response entity with the exception message and a status of BAD_REQUEST
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String>  handleAccessDeniedException(AccessDeniedException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }


}
