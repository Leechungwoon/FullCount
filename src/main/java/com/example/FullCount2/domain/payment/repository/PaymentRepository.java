package com.example.FullCount2.domain.payment.repository;

import com.example.FullCount2.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
