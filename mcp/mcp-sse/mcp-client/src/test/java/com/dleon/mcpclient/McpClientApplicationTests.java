package com.dleon.mcpclient;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Requires external MCP SSE provider to be reachable during context bootstrap")
class McpClientApplicationTests {

  @Test
  void contextLoads() {
  }

}
