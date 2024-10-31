package com.jwtImplementation.jwt_app.service;

import com.jwtImplementation.jwt_app.model.User;
import com.jwtImplementation.jwt_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findByRefreshToken(String refreshToken) {
        return  userRepository.findByRefreshToken(refreshToken);
    }


}
