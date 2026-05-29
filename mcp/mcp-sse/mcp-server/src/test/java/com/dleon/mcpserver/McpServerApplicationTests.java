package com.dleon.mcpserver;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Requires external MCP SSE provider to be reachable during context bootstrap")
class McpServerApplicationTests {

  @Test
  void contextLoads() {
  }

}
