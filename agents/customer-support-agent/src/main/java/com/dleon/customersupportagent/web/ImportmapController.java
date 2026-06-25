package com.dleon.customersupportagent.web;

import jakarta.annotation.PostConstruct;
import org.mvnpm.importmap.Aggregator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImportmapController {

  private static final String JAVASCRIPT_CODE = """
      const im = document.createElement('script');
      im.type = 'importmap';
      im.textContent = JSON.stringify(%s);
      document.currentScript.after(im);
      """;

  private String importmap;

  @GetMapping(value = "/_importmap/dynamic.importmap", produces = "application/importmap+json")
  public String importMap() {
    return importmap;
  }

  @GetMapping(value = "/_importmap/dynamic-importmap.js", produces = "application/javascript")
  public String importMapJs() {
    return JAVASCRIPT_CODE.formatted(importmap);
  }

  @PostConstruct
  void init() {
    Aggregator aggregator = new Aggregator();
    aggregator.addMapping("icons/", "/icons/");
    aggregator.addMapping("components/", "/components/");
    aggregator.addMapping("fonts/", "/fonts/");
    importmap = aggregator.aggregateAsJson();
  }
}
