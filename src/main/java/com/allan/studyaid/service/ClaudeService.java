package com.allan.studyaid.service;

import com.allan.studyaid.model.ChatMessage;
import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Thin wrapper around the Anthropic Java SDK that forces Claude to return
 * a specific structured shape via a single forced tool call (tool_choice),
 * rather than free-form chat text.
 *
 * The "tool" here is never actually executed — its input_schema IS the
 * response shape we want, and the tool_use block's parsed input is
 * returned directly as the answer. See:
 * https://platform.claude.com/docs/en/cli-sdks-libraries/sdks/java#tool-use
 */
@Service
public class ClaudeService {

    private final AnthropicClient client;
    private final String model;
    private final long maxTokens;

    public ClaudeService(AnthropicClient client,
                          @Value("${anthropic.model}") String model,
                          @Value("${anthropic.max-tokens}") long maxTokens) {
        this.client = client;
        this.model = model;
        this.maxTokens = maxTokens;
    }

    /**
     * Calls Claude with a system prompt, prior turns, and the student's new question,
     * forcing the response to conform to the schema derived from responseType.
     *
     * @param systemPrompt tutoring behavior instructions (subject-specific)
     * @param history      prior turns supplied by the client (stateless server)
     * @param question     the student's new message
     * @param responseType a class annotated with @JsonClassDescription / @JsonPropertyDescription
     *                     describing the exact JSON shape Claude must return
     */
    public <T> T askStructured(String systemPrompt, List<ChatMessage> history, String question, Class<T> responseType) {
        MessageCreateParams.Builder builder = MessageCreateParams.builder()
                .model(Model.of(model))
                .maxTokens(maxTokens)
                .system(systemPrompt)
                .addTool(responseType);

        for (ChatMessage turn : history) {
            if ("assistant".equals(turn.role())) {
                builder.addAssistantMessage(turn.content());
            } else {
                builder.addUserMessage(turn.content());
            }
        }
        builder.addUserMessage(question);

        String toolName = responseType.getSimpleName()
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toLowerCase();
        builder.toolChoice(ToolChoice.ofTool(ToolChoiceTool.builder().name(toolName).build()));

        Message message = client.messages().create(builder.build());

        return message.content().stream()
                .flatMap(block -> block.toolUse().stream())
                .findFirst()
                .map(toolUse -> toolUse.input(responseType))
                .orElseThrow(() -> new NoSuchElementException(
                        "Claude did not return the expected tool_use block for " + responseType.getSimpleName()));
    }
}
