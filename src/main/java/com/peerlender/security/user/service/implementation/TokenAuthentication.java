package com.peerlender.security.user.service.implementation;

import com.google.common.collect.ImmutableMap;
import com.peerlender.security.user.model.User;
import com.peerlender.security.user.repository.UserRepository;
import com.peerlender.security.user.service.TokenService;
import com.peerlender.security.user.service.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class TokenAuthentication implements UserAuthenticationService {

    private final TokenService tokenService;
    private final UserRepository userRepository;

    @Autowired
    public TokenAuthentication(TokenService tokenService, UserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<String> login(String username, String password) {
        return Optional.ofNullable(userRepository
        .findByUserDetails_Username(username))
        .filter(user -> user.get().getUserDetails().getPassword().equals(password))
        .map(user -> tokenService.expiring(ImmutableMap.of("username",username)));
    }

    @Override
    public User findByToken(String token) {
        Map<String,String> result = tokenService.verify(token);
        return userRepository.findByUserDetails_Username(result.get("username")).get();
    }
}
