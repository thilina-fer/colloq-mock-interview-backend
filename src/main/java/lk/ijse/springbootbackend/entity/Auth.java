package lk.ijse.springbootbackend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Auth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long authId;
    private String username;
    private String password;
    private String email;
    private String googleId;
    private String facebookId;
    private String githubId;
    private String role;
    private Boolean emailVerified;
    private String profilePic;
    private String status;
}
