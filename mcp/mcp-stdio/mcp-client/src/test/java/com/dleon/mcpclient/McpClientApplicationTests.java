package com.dleon.mcpclient;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Requires MCP stdio server JAR and PostgreSQL to be available during context bootstrap")
class McpClientApplicationTests {

  @Test
  void contextLoads() {
  }

}
