/*
package lk.ijse.springbootbackend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Auth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long authId;
    private String username;
    private String password;
    private String email;
    private String googleId;
    private String role;
    private Boolean emailVerified;
    private String profilePic;
    private String status;
    private boolean profileUpdated;
}
*/


package lk.ijse.springbootbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Auth implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long authId;

    private String username; // මෙය ඔයාගේ display name එක ලෙස භාවිතා කළ හැක
    private String password;

    @Column(unique = true, nullable = false)
    private String email; // Login වීමට භාවිතා කරන අනන්‍ය ලිපිනය

    private String googleId;

    @Enumerated(EnumType.STRING)
    private Role role; // Admin, Candidate, Interviewer

    private Boolean emailVerified;
    private String profilePic;
    private String status;
    private boolean profileUpdated;

    /**
     * Spring Security පද්ධතියට User ගේ බලතල (Roles) ලබා දීම.
     * මෙහිදී "ROLE_ADMIN" වැනි format එකකට ලබා දීම සම්මතයයි.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    /**
     * Spring Security විසින් User ව හඳුනාගන්නා අනන්‍ය නාමය (Identifier).
     * අපි මෙතැනදී Email එක භාවිතා කරමු.
     */
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    // ගිණුමේ තත්ත්වය පරීක්ෂා කරන Methods (සාමාන්‍යයෙන් true ලෙස තබයි)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // අවශ්‍ය නම් account status එක ACTIVE ද කියා මෙතැනදී බැලිය හැක
        return "ACTIVE".equalsIgnoreCase(status);
    }
}