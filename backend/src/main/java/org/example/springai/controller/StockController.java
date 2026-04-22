package org.example.springai.controller;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/v1/stocks")
public class StockController {

    /**
     * 返回某个股票近 N 天的 mock 收盘价数据
     * GET /api/v1/stocks/series?ticker=AAPL&days=30
     */
    @GetMapping("/series")
    public Map<String, Object> series(
            @RequestParam(defaultValue = "AAPL") String ticker,
            @RequestParam(defaultValue = "30") int days
    ) {
        days = Math.max(7, Math.min(days, 365)); // 限制范围 7~365

        List<String> dates = new ArrayList<>();
        List<Double> prices = new ArrayList<>();

        // 用随机游走生成可视化友好的曲线
        Random r = new Random(ticker.toUpperCase().hashCode());
        double price = 80 + r.nextInt(120); // 起始价 80~200

        for (int i = days - 1; i >= 0; i--) {
            LocalDate d = LocalDate.now().minusDays(i);
            dates.add(d.toString());

            // 每天涨跌 -2% ~ +2%
            double change = (r.nextDouble() * 0.04) - 0.02;
            price = price * (1.0 + change);
            prices.add(round2(price));
        }

        return Map.of(
                "ticker", ticker.toUpperCase(),
                "days", days,
                "dates", dates,
                "prices", prices
        );
    }

    /**
     * 提供一个 ticker 列表给前端下拉选择
     */
    @GetMapping("/tickers")
    public Map<String, Object> tickers() {
        return Map.of(
                "tickers", List.of("AAPL", "TSLA", "MSFT", "AMZN", "GOOGL", "NVDA")
        );
    }

    private double round2(double x) {
        return Math.round(x * 100.0) / 100.0;
    }
}
