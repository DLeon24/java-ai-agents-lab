package com.dleon.langchain4j.tool;

import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CalculatorTools {

  private static final Logger log = LoggerFactory.getLogger(CalculatorTools.class);

  @Tool("Adds two numbers and returns the result")
  public double add(double a, double b) {
    log.info("add({}, {}) = {}", a, b, a + b);
    return a + b;
  }

  @Tool("Subtracts two numbers (a - b) and returns the result")
  public double subtract(double a, double b) {
    log.info("subtract({}, {}) = {}", a, b, a - b);
    return a - b;
  }

  @Tool("Multiplies two numbers and returns the result")
  public double multiply(double a, double b) {
    log.info("multiply({}, {}) = {}", a, b, a * b);
    return a * b;
  }

  @Tool("Divides two numbers (a / b) and returns the result. Returns an error message if b is zero.")
  public String divide(double a, double b) {
    if (b == 0) {
      log.warn("divide({}, {}) → division by zero", a, b);
      return "Error: cannot divide by zero";
    }
    double result = a / b;
    log.info("divide({}, {}) = {}", a, b, result);
    return String.valueOf(result);
  }

  @Tool("Calculates the square root of a number")
  public double squareRoot(double number) {
    double result = Math.sqrt(number);
    log.info("squareRoot({}) = {}", number, result);
    return result;
  }

  @Tool("Calculates a number raised to a power (base ^ exponent)")
  public double power(double base, double exponent) {
    double result = Math.pow(base, exponent);
    log.info("power({}, {}) = {}", base, exponent, result);
    return result;
  }
}
