package com.dleon.langchain4j.tool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class CountryApiTools {

  private static final Logger log = LoggerFactory.getLogger(CountryApiTools.class);

  private final RestTemplate restTemplate = new RestTemplate();
  private final ObjectMapper mapper = new ObjectMapper();

  @Tool(
      "Looks up real country information using the restcountries.com REST API. " + "Returns name, capital, population, region, and languages.")
  public String lookupCountry(
      @P("Country name to look up, e.g. Peru, Colombia, Japan") String country) {
    log.info("lookupCountry invoked for: {}", country);
    try {
      String encoded = URLEncoder.encode(country, StandardCharsets.UTF_8);
      String url =
          "https://restcountries.com/v3.1/name/" + encoded + "?fields=name,capital,population,region,languages";
      String json = restTemplate.getForObject(url, String.class);
      JsonNode root = mapper.readTree(json);
      JsonNode countryNode = root.get(0);

      String name = countryNode.path("name").path("common").asText();
      String capital =
          countryNode.path("capital").has(0) ? countryNode.path("capital").get(0).asText() : "N/A";
      long population = countryNode.path("population").asLong();
      String region = countryNode.path("region").asText();

      StringBuilder languages = new StringBuilder();
      countryNode.path("languages").fields().forEachRemaining(entry -> {
        if (!languages.isEmpty()) {
          languages.append(", ");
        }
        languages.append(entry.getValue().asText());
      });

      String result =
          String.format("Country: %s, Capital: %s, Population: %,d, Region: %s, Languages: %s",
              name, capital, population, region, languages);
      log.info("Result: {}", result);
      return result;
    } catch (Exception e) {
      log.error("Error looking up country '{}': {}", country, e.getMessage());
      return "Error looking up country '" + country + "': " + e.getMessage();
    }
  }
}
