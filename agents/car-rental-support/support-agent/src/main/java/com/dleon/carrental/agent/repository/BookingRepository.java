package com.dleon.carrental.agent.repository;

import com.dleon.carrental.agent.domain.Booking;
import com.dleon.carrental.agent.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

  List<Booking> findByCustomer(Customer customer);
}
