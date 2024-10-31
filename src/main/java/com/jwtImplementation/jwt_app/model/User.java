package com.jwtImplementation.jwt_app.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RestController;


@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @Column(unique = true, nullable = false)
    private  String username;

    @Column(nullable = false)
    private String Password;

    @Column(nullable = false)
    private String companyName;

    private String refreshToken;

    private Long refreshTokenExpirationTime;




}
