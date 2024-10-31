package com.jwtImplementation.jwt_app.service;


import com.jwtImplementation.jwt_app.model.User;
import org.springframework.beans.factory.annotation.Autowired;

public interface UserService {

    public User saveUser(User user);

    public  User findByUsername( String username);

    public  User findByRefreshToken(String refreshToken);
}
