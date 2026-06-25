package com.dleon.customersupportagent.repository;

import com.dleon.customersupportagent.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

  Optional<Customer> findByFirstNameIgnoreCaseAndLastNameIgnoreCase(String firstName,
      String lastName);
}
