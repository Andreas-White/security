package com.peerlender.security.user.service;

import com.peerlender.security.user.model.User;
import com.peerlender.security.user.model.UserDetailsImpl;

import java.util.Optional;

public interface UserAuthenticationService {

    Optional<String> login(String username, String password);

    User findByToken(String token);
}
