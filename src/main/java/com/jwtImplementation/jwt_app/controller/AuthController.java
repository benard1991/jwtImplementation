package com.jwtImplementation.jwt_app.controller;


import com.jwtImplementation.jwt_app.dto.*;
import com.jwtImplementation.jwt_app.model.User;
import com.jwtImplementation.jwt_app.service.UserService;
import com.jwtImplementation.jwt_app.util.JwtUtil;
import com.jwtImplementation.jwt_app.util.PasswordEncoderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private   PasswordEncoderUtil passwordEncoderUtil;

     @Autowired
    private JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/register")
    public ResponseEntity<CustomResponse>register(@RequestBody RegistrationRequest registrationRequest){

        if(userService.findByUsername(registrationRequest.getUsername())!=null){
            return  ResponseEntity.badRequest().body(new CustomResponse("User already exist",HttpStatus.BAD_REQUEST.value()));
        }

        User user = new User();
        user.setUsername(registrationRequest.getUsername());
        user.setCompanyName(registrationRequest.getCompanyName());
        user.setPassword(passwordEncoderUtil.encodePassword(registrationRequest.getPassword()));
        userService.saveUser(user);
        return ResponseEntity.ok().body(new CustomResponse("User registered successfully",HttpStatus.OK.value()));

    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse>login(@RequestBody AuthRequest authRequest){
        logger.info("User login attempt for username: {}", authRequest.getUsername());

        User foundUser = userService.findByUsername(authRequest.getUsername());
        logger.info("User found: {}", foundUser);

        if(foundUser !=null && passwordEncoderUtil.matches(authRequest.getPassword(), foundUser.getPassword())){
            String accessToken = jwtUtil.generateAccessToken(foundUser.getUsername());
            String refreshToken = jwtUtil.generateRefreshToken(foundUser.getUsername());
            logger.info("Access token: {}", accessToken);

            foundUser.setRefreshToken(refreshToken);
            userService.saveUser(foundUser);

            // Calculate the expiration time for both tokens
            long accessTokenExpiration = System.currentTimeMillis() + jwtUtil.getAccessTokenExpirationTime();
            long refreshTokenExpiration = System.currentTimeMillis() + jwtUtil.getRefreshTokenExpirationTime();

            // Format the expiration times into a human-readable date format
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String accessTokenExpirationDate =dateFormat.format(new Date(accessTokenExpiration));
            String refreshTokenExpirationDate =  dateFormat.format(new Date(refreshTokenExpiration));

            return  ResponseEntity.ok(new AuthResponse("Success",HttpStatus.OK.value(), accessToken, refreshToken,accessTokenExpirationDate,refreshTokenExpirationDate));

        }
        return   ResponseEntity.badRequest().body(new AuthResponse("User not found",HttpStatus.FORBIDDEN.value()));


    }


    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        logger.info("Received refresh token: {}", refreshToken);

        User user = userService.findByRefreshToken(refreshToken);

        if (user == null) {
            logger.warn("No user found for refresh token: {}", refreshToken);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse("Invalid refresh token", HttpStatus.UNAUTHORIZED.value()));
        }

      // Check if the refresh token has expired
        long currentTime = System.currentTimeMillis();
        if (currentTime > user.getRefreshTokenExpirationTime()) {
            logger.warn("Refresh token has expired: {}", refreshToken);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse("Refresh token has expired", HttpStatus.UNAUTHORIZED.value()));
        }


        if (jwtUtil.validateToken(refreshToken, user.getUsername())) {
            String newAccessToken = jwtUtil.generateAccessToken(user.getUsername());
            String newRefreshToken = jwtUtil.generateRefreshToken(user.getUsername());

            user.setRefreshToken(newRefreshToken);
            user.setRefreshTokenExpirationTime(System.currentTimeMillis() + jwtUtil.getRefreshTokenExpirationTime()); // Update expiration time
            userService.saveUser(user);

            long newAccessTokenExpiration = System.currentTimeMillis() + jwtUtil.getAccessTokenExpirationTime();
            long newRefreshTokenExpiration = System.currentTimeMillis() + jwtUtil.getRefreshTokenExpirationTime();

            // Format the expiration times into a human-readable date format
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String newAccessTokenExpirationDate = dateFormat.format(newAccessTokenExpiration);
            String newRefreshTokenExpirationDate = dateFormat.format(newRefreshTokenExpiration);

            return  ResponseEntity.ok(new AuthResponse("Success",HttpStatus.OK.value(),newAccessToken, newRefreshToken,newAccessTokenExpirationDate,newAccessTokenExpirationDate));
        }

        logger.warn("Invalid refresh token: {}", refreshToken);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponse("Invalid refresh token", HttpStatus.UNAUTHORIZED.value()));
    }




}
