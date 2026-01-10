package com.codesupreme.couriersub.user.repo;

import com.codesupreme.couriersub.user.entity.User;
import com.codesupreme.couriersub.user.entity.UserVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserVerificationRepository extends JpaRepository<UserVerification, Long> {
    Optional<UserVerification> findByUser(User user);
}
