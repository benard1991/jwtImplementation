package com.jwtImplementation.jwt_app.repository;

import com.jwtImplementation.jwt_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Fetch a User by their username
    User findByUsername(String username);

    // Fetch a User by their refresh token
    User findByRefreshToken(String refreshToken);



}
