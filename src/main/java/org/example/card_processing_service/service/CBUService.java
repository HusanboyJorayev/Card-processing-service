package org.example.card_processing_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CBUService {

    private final RestTemplate restTemplate;

    public BigDecimal getExchangeRate(String fromCurrency, String toCurrency) {
        // Assume we have an endpoint to get exchange rates from CBU
        String url = "https://cbu.uz/oz/exchange-rates/json/";

        // Example response { "USD": "11220.00", "EUR": "12345.00" }
        Map<String, String> rates = restTemplate.getForObject(url, Map.class);

        BigDecimal fromRate = new BigDecimal(rates.get(fromCurrency));
        BigDecimal toRate = new BigDecimal(rates.get(toCurrency));

        // Conversion rate from `fromCurrency` to `toCurrency`
        return fromRate.divide(toRate, 4, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal convertAmount(BigDecimal amount, String fromCurrency, String toCurrency) {
        BigDecimal exchangeRate = getExchangeRate(fromCurrency, toCurrency);
        return amount.multiply(exchangeRate);
    }
}
