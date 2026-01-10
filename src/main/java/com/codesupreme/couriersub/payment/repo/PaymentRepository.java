package com.codesupreme.couriersub.payment.repo;

import com.codesupreme.couriersub.payment.entity.Payment;
import com.codesupreme.couriersub.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findTop50ByUserOrderByCreatedAtDesc(User user);
    Optional<Payment> findByProviderOrderId(String providerOrderId);
}
