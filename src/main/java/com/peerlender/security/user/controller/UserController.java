package com.peerlender.security.user.controller;

import com.peerlender.security.user.model.User;
import com.peerlender.security.user.model.UserDetailsImpl;
import com.peerlender.security.user.repository.UserRepository;
import com.peerlender.security.user.service.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserAuthenticationService authenticationService;
    private final UserRepository userRepository;

    @Autowired
    public UserController(UserAuthenticationService authenticationService, UserRepository userRepository) {
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
    }

    @PostMapping(value = "register")
    public void register(@RequestBody UserDetailsImpl userDetails) {
        userRepository.save(new User(userDetails));
    }
}
