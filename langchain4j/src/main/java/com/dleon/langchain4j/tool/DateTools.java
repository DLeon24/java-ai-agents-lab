package com.dleon.langchain4j.tool;

import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class DateTools {

  private static final Logger log = LoggerFactory.getLogger(DateTools.class);

  private static final Locale LOCALE_EN = Locale.ENGLISH;

  @Tool("Returns the current date in a human-readable format")
  public String currentDate() {
    String date =
        LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", LOCALE_EN));
    log.info("currentDate() = {}", date);
    return date;
  }

  @Tool("Returns the current date and time in a human-readable format")
  public String currentDateTime() {
    String dateTime = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy, HH:mm:ss", LOCALE_EN));
    log.info("currentDateTime() = {}", dateTime);
    return dateTime;
  }
}
