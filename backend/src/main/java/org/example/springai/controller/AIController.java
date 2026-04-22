package org.example.springai.controller;

import org.example.springai.service.UserManager;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai")
public class AIController {

    private final UserManager userManager;
    private final ChatClient chatClient;
    private static final String SYSTEM_PROMPT = """
You are an AI Asset Management Assistant for an educational web application.

CRITICAL OUTPUT RULES (must follow exactly):
1) Your response MUST contain these three sections, in this order:
   - "Answer"
   - "Next steps"
   - "Disclaimer"

2) "Answer" must contain 5–10 bullet points with general, low-risk personal finance guidance.
3) "Next steps" must contain 2–4 bullet points with concrete actions.
4) "Disclaimer" must contain BOTH of the following bullet points:
   - "This is not financial advice."
   - "Investments can lose value."

SAFETY & SCOPE:
- Do NOT provide personalised financial advice.
- Do NOT guarantee returns.
- Do NOT claim access to real-time market data.
- If asked for live prices, say you do not have real-time data.

STYLE:
- Plain English.
- Concise.
- No emojis.
""";

    // Spring AI 会提供 ChatClient.Builder（前提是你引入了 spring-ai-ollama starter）
    public AIController(UserManager userManager, ChatClient.Builder builder) {
        this.userManager = userManager;
        this.chatClient = builder.build();
    }

    /**
     * Request body from the front-end
     */
    public record ChatRequest(String username, String message) {}

    /**
     * AI advisory endpoint
     */
    @PostMapping("/chat")
    public Map<String, Object> chat(@RequestBody ChatRequest request) {

        String username = request.username();
        String message = request.message();

        // 1. Load conversation history
        List<String> history = userManager.getConversation(username);

        int maxLines = 4; // 只保留最近 2 轮（USER/AI * 2）
        List<String> recent = history.size() > maxLines
                ? history.subList(history.size() - maxLines, history.size())
                : history;

        StringBuilder context = new StringBuilder();
        for (String h : recent) {
            // 清洗换行，避免 prompt 过长/格式乱
            String cleaned = h.replace("\r", " ").replace("\n", " ").trim();
            // 截断每条，防止历史回复太长
            if (cleaned.length() > 300) {
                cleaned = cleaned.substring(0, 300) + "...";
            }
            context.append(cleaned).append("\n");
        }

        String finalUserMessage = """
Conversation summary (most recent):
%s
Current question:
%s
""".formatted(context.toString(), message);

        String reply = chatClient
                .prompt()
                .system(SYSTEM_PROMPT)
                .user(finalUserMessage)
                .call()
                .content();

        // 3. Save conversation history
        history.add("USER: " + message);
        history.add("AI: " + reply);
        userManager.saveConversation(username, history);

        // 4. Return structured response for the front-end
        return Map.of(
                "reply", reply,
                "riskNotes", List.of(
                        "This response does not constitute financial advice",
                        "All investments carry risk and values may fluctuate"
                ),
                "actionItems", List.of(
                        Map.of(
                                "title", "Clarify your goal",
                                "detail", "Define timeframe, liquidity needs, and risk tolerance"
                        ),
                        Map.of(
                                "title", "Diversify",
                                "detail", "Avoid concentrating too much capital in a single asset"
                        )
                )
        );
    }



    /**
     * Optional: retrieve conversation history
     */
    @GetMapping("/history/{username}")
    public Map<String, Object> history(@PathVariable String username) {
        return Map.of(
                "username", username,
                "messages", userManager.getConversation(username)
        );
    }
}
