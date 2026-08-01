package backend.Service;

import backend.AdvisorDTO.ChatMessageDTO;
import backend.Client.OllamaClient;
import backend.Model.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdvisorService {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private OllamaClient ollamaClient;

    private static final String SYSTEM_PROMPT = """
            You are a friendly, practical personal finance advisor built into an expense tracker app.
            You will be given a summary of the user's real expense data, followed by their question.
            Rules:
            - Base every claim strictly on the data provided. Never invent numbers.
            - If the data is insufficient to answer, say so plainly.
            - Keep replies concise and actionable (a few sentences, or a short list).
            - Use the currency symbol/units present in the data as-is, do not convert.
            - Do not give investment, tax, or legal advice; stick to budgeting and spending habits.
            """;

    /**
     * Builds a compact, LLM-friendly summary of the user's expenses:
     * totals, per-category breakdown, and a recent-vs-previous-30-day comparison.
     */
    public String buildExpenseSummary(String userEmail) {
        List<Expense> expenses = expenseService.getRawExpensesForUser(userEmail);

        if (expenses.isEmpty()) {
            return "The user has not logged any expenses yet.";
        }

        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();

        Map<String, Double> byCategory = expenses.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getCategory().getName(),
                        Collectors.summingDouble(Expense::getAmount)
                ));

        LocalDate today = LocalDate.now();
        LocalDate last30Start = today.minusDays(30);
        LocalDate prev30Start = today.minusDays(60);

        double last30Total = expenses.stream()
                .filter(e -> e.getExpensedate() != null && !e.getExpensedate().isBefore(last30Start))
                .mapToDouble(Expense::getAmount).sum();

        double prev30Total = expenses.stream()
                .filter(e -> e.getExpensedate() != null
                        && !e.getExpensedate().isBefore(prev30Start)
                        && e.getExpensedate().isBefore(last30Start))
                .mapToDouble(Expense::getAmount).sum();

        List<Expense> topExpenses = expenses.stream()
                .sorted(Comparator.comparingDouble(Expense::getAmount).reversed())
                .limit(5)
                .toList();

        StringBuilder sb = new StringBuilder();
        sb.append("=== Expense Data Summary ===\n");
        sb.append(String.format("Total logged expenses: %d entries, total amount %.2f%n", expenses.size(), total));

        sb.append("\nSpend by category:\n");
        byCategory.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEach(entry -> sb.append(String.format("- %s: %.2f%n", entry.getKey(), entry.getValue())));

        sb.append(String.format("%nLast 30 days spend: %.2f%n", last30Total));
        sb.append(String.format("Previous 30 days spend: %.2f%n", prev30Total));
        if (prev30Total > 0) {
            double pctChange = ((last30Total - prev30Total) / prev30Total) * 100;
            sb.append(String.format("Change vs previous 30 days: %.1f%%%n", pctChange));
        }

        sb.append("\nTop 5 largest individual expenses:\n");
        for (Expense e : topExpenses) {
            sb.append(String.format("- %s (%s): %.2f on %s%n",
                    e.getTitle(), e.getCategory().getName(), e.getAmount(), e.getExpensedate()));
        }

        long daysTracked = ChronoUnit.DAYS.between(
                expenses.stream().map(Expense::getExpensedate).filter(java.util.Objects::nonNull)
                        .min(LocalDate::compareTo).orElse(today),
                today
        ) + 1;
        sb.append(String.format("%nData spans roughly %d day(s).%n", daysTracked));

        return sb.toString();
    }

    /**
     * Generates 3-5 short, data-grounded insights/tips for the dashboard.
     */
    public List<String> generateInsights(String userEmail) {
        List<Expense> expenses = expenseService.getRawExpensesForUser(userEmail);
        if (expenses.isEmpty()) {
            return List.of();
        }

        String summary = buildExpenseSummary(userEmail);

        String userPrompt = summary + """

                Based only on the data above, give the user 3 to 5 short, specific insights or tips
                about their spending. Each one should be a single sentence.
                Respond with ONLY the insights, one per line, with no numbering, bullets, or preamble.
                """;

        List<ChatMessageDTO> messages = new ArrayList<>();
        messages.add(new ChatMessageDTO("system", SYSTEM_PROMPT));
        messages.add(new ChatMessageDTO("user", userPrompt));

        String reply = ollamaClient.chat(messages);

        return List.of(reply.split("\n")).stream()
                .map(line -> line.replaceFirst("^[\\-\\*\\d\\.\\)\\s]+", "").trim())
                .filter(line -> !line.isEmpty())
                .toList();
    }

    /**
     * Handles a chat turn: injects the expense summary as context, then forwards
     * the conversation history and new message to Ollama.
     */
    public String chat(String userEmail, String userMessage, List<ChatMessageDTO> history) {
        String summary = buildExpenseSummary(userEmail);

        List<ChatMessageDTO> messages = new ArrayList<>();
        messages.add(new ChatMessageDTO("system", SYSTEM_PROMPT + "\n\n" + summary));

        if (history != null) {
            messages.addAll(history);
        }

        messages.add(new ChatMessageDTO("user", userMessage));

        return ollamaClient.chat(messages);
    }
}
