package org.example.springai.controller;

import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/banks")
public class BankController {

    /**
     * Returns deposit interest rates (mock data).
     * Front-end can display this as a table.
     */
    @GetMapping("/rates")
    public Map<String, Object> getRates() {
        return Map.of(
                "updatedAt", Instant.now().getEpochSecond(),
                "termsMonths", List.of(3, 6, 12),
                "banks", List.of(
                        Map.of(
                                "name", "HSBC",
                                "aprByTerm", Map.of("3", 0.028, "6", 0.031, "12", 0.034)
                        ),
                        Map.of(
                                "name", "Barclays",
                                "aprByTerm", Map.of("3", 0.026, "6", 0.030, "12", 0.033)
                        ),
                        Map.of(
                                "name", "Lloyds",
                                "aprByTerm", Map.of("3", 0.025, "6", 0.029, "12", 0.032)
                        )
                )
        );
    }

    /**
     * Simple deposit interest calculator (simple interest for demonstration).
     */
    public record CalcRequest(String bank, int termMonths, double principal) {}

    @PostMapping("/calc")
    public Map<String, Object> calc(@RequestBody CalcRequest req) {
        double apr = aprFor(req.bank(), req.termMonths());
        double years = req.termMonths() / 12.0;

        double interest = req.principal() * apr * years;
        double total = req.principal() + interest;

        return Map.of(
                "bank", req.bank(),
                "termMonths", req.termMonths(),
                "principal", round2(req.principal()),
                "aprUsed", apr,
                "interest", round2(interest),
                "total", round2(total)
        );
    }

    /**
     * Mock APR rules based on bank and term.
     * Later you can replace this with DB/API lookup.
     */
    private double aprFor(String bank, int termMonths) {
        String b = (bank == null) ? "" : bank.trim().toLowerCase();

        double base = switch (b) {
            case "hsbc" -> 0.028;
            case "barclays" -> 0.026;
            case "lloyds" -> 0.025;
            default -> 0.024;
        };

        if (termMonths >= 12) return base + 0.006;
        if (termMonths >= 6)  return base + 0.003;
        return base;
    }

    private double round2(double x) {
        return Math.round(x * 100.0) / 100.0;
    }
}
