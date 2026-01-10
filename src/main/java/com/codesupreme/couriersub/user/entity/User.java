package com.codesupreme.couriersub.user.entity;

import com.codesupreme.couriersub.common.enums.UserRole;
import com.codesupreme.couriersub.common.enums.VerifyStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_phone", columnList = "phone", unique = true)
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String firstName;

    @Column(nullable = false, length = 80)
    private String lastName;

    @Column(nullable = false, unique = true, length = 20)
    private String phone; // 994XXXXXXXXX

    @Column(nullable = false, length = 100)
    private String password; // PLAIN TEXT (istədiyin kimi)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role = UserRole.COURIER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VerifyStatus verifyStatus = VerifyStatus.REGISTERED;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // getters/setters
    public Long getId() { return id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public VerifyStatus getVerifyStatus() { return verifyStatus; }
    public void setVerifyStatus(VerifyStatus verifyStatus) { this.verifyStatus = verifyStatus; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
