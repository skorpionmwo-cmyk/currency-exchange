package ru.skillbox.currency.exchange.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.currency.exchange.cbr.CbrQuote;
import ru.skillbox.currency.exchange.entity.Currency;
import ru.skillbox.currency.exchange.repository.CurrencyRepository;

/** Imports a complete batch of CBR quotes in a single transaction. */
@Service
@RequiredArgsConstructor
public class CurrencySyncService {

  private final CurrencyRepository repository;

  /** Updates matching alphabetic ISO codes and inserts any missing quotes. */
  @Transactional
  public int update(List<CbrQuote> quotes) {
    Map<String, CbrQuote> byCode = quotes.stream()
        .collect(Collectors.toMap(CbrQuote::code, Function.identity()));
    Map<String, Currency> existing = repository.findAllByIsoCharCodeIn(byCode.keySet()).stream()
        .collect(Collectors.toMap(Currency::getIsoCharCode, Function.identity()));

    List<Currency> updated = quotes.stream()
        .map(quote -> apply(quote, existing.get(quote.code())))
        .toList();
    repository.saveAll(updated);
    return updated.size();
  }

  private Currency apply(CbrQuote quote, Currency existing) {
    double value = quote.value();
    if (quote.code() == null || !quote.code().matches("[A-Z]{3}")
        || quote.name() == null || quote.name().isBlank() || quote.numericCode() <= 0
        || quote.nominal() <= 0 || !Double.isFinite(value) || value <= 0) {
      throw new IllegalArgumentException("Invalid CBR quote: " + quote.code());
    }
    Currency currency = existing == null ? new Currency() : existing;
    currency.setIsoCharCode(quote.code());
    currency.setIsoNumCode(quote.numericCode());
    currency.setName(quote.name());
    currency.setNominal(quote.nominal());
    currency.setValue(value);
    return currency;
  }
}
