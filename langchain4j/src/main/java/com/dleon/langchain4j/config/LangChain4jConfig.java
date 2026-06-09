package com.dleon.langchain4j.config;

import com.dleon.langchain4j.service.Assistant;
import com.dleon.langchain4j.tool.CalculatorTools;
import com.dleon.langchain4j.tool.CountryApiTools;
import com.dleon.langchain4j.tool.DateTools;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LangChain4jConfig {

  @Bean
  public Assistant assistant(ChatLanguageModel chatLanguageModel, CalculatorTools calculatorTools,
      DateTools dateTools, CountryApiTools countryApiTools) {
    return AiServices.builder(Assistant.class).chatLanguageModel(chatLanguageModel)
        .tools(calculatorTools, dateTools, countryApiTools).build();
  }
}
