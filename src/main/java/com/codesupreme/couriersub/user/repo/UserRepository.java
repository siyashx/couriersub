package com.codesupreme.couriersub.user.repo;

import com.codesupreme.couriersub.common.enums.VerifyStatus;
import com.codesupreme.couriersub.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhone(String phone);
    boolean existsByPhone(String phone);
    List<User> findByVerifyStatus(VerifyStatus verifyStatus);

}
