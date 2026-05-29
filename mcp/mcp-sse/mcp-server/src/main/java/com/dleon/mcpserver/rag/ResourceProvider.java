package com.dleon.mcpserver.rag;

import com.dleon.mcpserver.repository.GameRepository;
import org.springaicommunity.mcp.annotation.McpResource;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceProvider {

  private final GameRepository gameRepository;

  public ResourceProvider(GameRepository gameRepository) {
    this.gameRepository = gameRepository;
  }

  @McpResource(uri = "games://game-list", name = "Game List",
      description = "A list of games available in the repository")
  public McpSchema.ReadResourceResult gameListResource() {
    var gameTitles = gameRepository.findAllTitles();
    var gameListText = new StringBuilder();
    for (String title : gameTitles) {
      gameListText.append("- ").append(title).append("\n");
    }

    return new McpSchema.ReadResourceResult(List.of(
        new McpSchema.TextResourceContents("games://game-list", "text/plain",
            gameListText.toString())));
  }

}
