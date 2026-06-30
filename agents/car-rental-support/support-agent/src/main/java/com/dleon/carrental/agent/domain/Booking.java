package com.dleon.carrental.agent.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class Booking {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  private Customer customer;

  private LocalDate dateFrom;
  private LocalDate dateTo;
  private String location;

  @Override
  public String toString() {
    return "Booking ID: %d - From: %s - To: %s - Location: %s".formatted(id, dateFrom, dateTo,
        location);
  }
}
