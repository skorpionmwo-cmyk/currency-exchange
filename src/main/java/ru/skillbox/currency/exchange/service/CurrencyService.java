package ru.skillbox.currency.exchange.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.skillbox.currency.exchange.dto.CurrencyDto;
import ru.skillbox.currency.exchange.dto.CurrencyListDto;
import ru.skillbox.currency.exchange.dto.CurrencyShortDto;
import ru.skillbox.currency.exchange.entity.Currency;
import ru.skillbox.currency.exchange.mapper.CurrencyMapper;
import ru.skillbox.currency.exchange.repository.CurrencyRepository;

/** Provides currency queries, creation, and conversion to rubles. */
@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyService {
  private final CurrencyMapper mapper;
  private final CurrencyRepository repository;

  /** Returns stored currencies in ID order. */
  public CurrencyListDto getAll() {
    return new CurrencyListDto(repository.findAll(Sort.by("id")).stream()
        .map(currency -> new CurrencyShortDto(currency.getName(), currency.getValue()))
        .toList());
  }

  /** Returns the full record for the specified currency ID. */
  public CurrencyDto getById(Long id) {
    log.info("CurrencyService method getById executed");
    Currency currency = repository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Currency not found with id: " + id));
    return mapper.convertToDto(currency);
  }

  /** Converts units of the requested currency to rubles. */
  public Double convertValue(Long value, Long numCode) {
    log.info("CurrencyService method convertValue executed");
    Currency currency = repository.findFirstByIsoNumCodeOrderByIsoCharCodeAscIdAsc(numCode)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "Currency not found with numeric ISO code: " + numCode));
    Long nominal = currency.getNominal();
    return value * currency.getValue() / (nominal != null && nominal > 0 ? nominal : 1);
  }

  public CurrencyDto create(CurrencyDto dto) {
    log.info("CurrencyService method create executed");
    return mapper.convertToDto(repository.save(mapper.convertToEntity(dto)));
  }
}
