package org.kiennguyenfpt.datingapp.repositories;

import org.kiennguyenfpt.datingapp.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Fetch payments by userId with user and location details (if needed)
    @Query(value = "SELECT p.payment_id, p.amount, p.date, u.user_id, l.address " +
            "FROM payments p " +
            "JOIN users u ON p.user_id = u.user_id " +
            "LEFT JOIN user_location l ON u.user_id = l.user_id " +
            "WHERE p.user_id = :userId", nativeQuery = true)
    List<Object[]> findPaymentsByUserId(@Param("userId") Long userId);

    // Fetch all payments with user and location details (if needed)
    @Query(value = "SELECT p.payment_id, p.amount, p.date, u.user_id, l.address, u.email " +
            "FROM payments p " +
            "JOIN users u ON p.user_id = u.user_id " +
            "LEFT JOIN user_location l ON u.user_id = l.user_id", nativeQuery = true)
    List<Object[]> findAllPayments();
}