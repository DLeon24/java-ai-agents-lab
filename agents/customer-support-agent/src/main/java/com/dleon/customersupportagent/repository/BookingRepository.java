package com.dleon.customersupportagent.repository;

import com.dleon.customersupportagent.domain.Booking;
import com.dleon.customersupportagent.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

  List<Booking> findByCustomer(Customer customer);
}
