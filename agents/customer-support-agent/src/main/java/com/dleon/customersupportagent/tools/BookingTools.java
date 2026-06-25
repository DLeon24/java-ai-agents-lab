package com.dleon.customersupportagent.tools;

import com.dleon.customersupportagent.domain.Booking;
import com.dleon.customersupportagent.domain.Customer;
import com.dleon.customersupportagent.exception.ProjectExceptions.BookingCannotBeCancelledException;
import com.dleon.customersupportagent.exception.ProjectExceptions.BookingNotFoundException;
import com.dleon.customersupportagent.exception.ProjectExceptions.CustomerNotFoundException;
import com.dleon.customersupportagent.repository.BookingRepository;
import com.dleon.customersupportagent.repository.CustomerRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class BookingTools {

  private final BookingRepository bookingRepository;
  private final CustomerRepository customerRepository;

  public BookingTools(BookingRepository bookingRepository, CustomerRepository customerRepository) {
    this.bookingRepository = bookingRepository;
    this.customerRepository = customerRepository;
  }

  @Tool(description = "Cancel a booking")
  @Transactional
  public String cancelBooking(long bookingId, String customerFirstName, String customerLastName) {
    Booking booking = findBooking(bookingId, customerFirstName, customerLastName);
    if (booking.getDateFrom().minusDays(11).isBefore(LocalDate.now())) {
      throw new BookingCannotBeCancelledException(bookingId,
          "booking from date is 11 days before today");
    }
    if (booking.getDateTo().minusDays(4).isBefore(booking.getDateFrom())) {
      throw new BookingCannotBeCancelledException(bookingId,
          "booking period is less than four days");
    }
    bookingRepository.delete(booking);
    return "Booking %d cancelled successfully".formatted(bookingId);
  }

  @Tool(description = "List booking for a customer")
  @Transactional(readOnly = true)
  public List<String> listBookingsForCustomer(String customerName, String customerSurname) {
    Customer customer =
        customerRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCase(customerName,
                customerSurname)
            .orElseThrow(() -> new CustomerNotFoundException(customerName, customerSurname));
    return bookingRepository.findByCustomer(customer).stream().map(Booking::toString).toList();
  }

  @Tool(description = "Get booking details")
  @Transactional(readOnly = true)
  public String getBookingDetails(long bookingId, String customerFirstName,
      String customerLastName) {
    return findBooking(bookingId, customerFirstName, customerLastName).toString();
  }

  private Booking findBooking(long bookingId, String customerFirstName, String customerLastName) {
    Booking booking = bookingRepository.findById(bookingId)
        .orElseThrow(() -> new BookingNotFoundException(bookingId));
    if (!booking.getCustomer().getFirstName()
        .equalsIgnoreCase(customerFirstName) || !booking.getCustomer().getLastName()
        .equalsIgnoreCase(customerLastName)) {
      throw new BookingNotFoundException(bookingId);
    }
    return booking;
  }
}
