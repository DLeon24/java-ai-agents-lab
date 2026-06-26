package com.dleon.customersupportagent.rag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class RagIngestion implements ApplicationRunner {

  private static final Logger log = LoggerFactory.getLogger(RagIngestion.class);

  private final VectorStore vectorStore;
  private final ResourcePatternResolver resourceResolver =
      new PathMatchingResourcePatternResolver();

  @Value("${app.rag.location:}")
  private String ragLocation;

  @Value("${app.rag.max-segment-size}")
  private int maxSegmentSize;

  @Value("${app.rag.max-overlap-size}")
  private int maxOverlapSize;

  public RagIngestion(VectorStore vectorStore) {
    this.vectorStore = vectorStore;
  }

  @Override
  public void run(ApplicationArguments args) throws IOException {
    int existingDocuments = countStoredDocuments();
    if (existingDocuments > 0) {
      log.info("Vector store already contains {} document(s), skipping RAG ingestion",
          existingDocuments);
      return;
    }

    String pattern = toClasspathPattern(ragLocation);
    Resource[] resources = resourceResolver.getResources(pattern);

    if (resources.length == 0) {
      log.warn("No documents found at {}", pattern);
      return;
    }

    List<Document> documents = new ArrayList<>();
    for (Resource resource : resources) {
      if (!resource.isReadable()) {
        continue;
      }
      TextReader reader = new TextReader(resource);
      reader.getCustomMetadata().put("source", resource.getFilename());
      documents.addAll(reader.get());
    }
    TokenTextSplitter splitter =
        new TokenTextSplitter(maxSegmentSize, maxOverlapSize, 5, 10_000, true);
    List<Document> segments = splitter.apply(documents);
    vectorStore.add(segments);
    log.info("Documents ingested successfully ({} files, {} segments)", resources.length,
        segments.size());
  }

  private int countStoredDocuments() {
    return vectorStore.getNativeClient()
        .filter(JdbcTemplate.class::isInstance)
        .map(JdbcTemplate.class::cast)
        .map(jdbcTemplate -> {
          String sql = "SELECT COUNT(*) FROM %s.%s".formatted(PgVectorStore.DEFAULT_SCHEMA_NAME,
              PgVectorStore.DEFAULT_TABLE_NAME);
          Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
          return count != null ? count : 0;
        })
        .orElse(0);
  }

  private static String toClasspathPattern(String path) {
    String normalized = path.endsWith("/") ? path : path + "/";
    if (normalized.startsWith("classpath:")) {
      return normalized + "**/*";
    }
    return "classpath:" + normalized + "**/*";
  }
}
