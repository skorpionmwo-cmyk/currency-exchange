package ru.skillbox.currency.exchange.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.currency.exchange.dto.CurrencyDto;
import ru.skillbox.currency.exchange.dto.CurrencyListDto;
import ru.skillbox.currency.exchange.service.CurrencyService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/currency")
public class CurrencyController {
  private final CurrencyService service;

  @GetMapping
  ResponseEntity<CurrencyListDto> getAll() {
    return ResponseEntity.ok(service.getAll());
  }

  @GetMapping("/{id}")
  ResponseEntity<CurrencyDto> getById(@PathVariable("id") Long id) {
    return ResponseEntity.ok(service.getById(id));
  }

  @GetMapping("/convert")
  ResponseEntity<Double> convertValue(
      @RequestParam("value") Long value, @RequestParam("numCode") Long numCode) {
    return ResponseEntity.ok(service.convertValue(value, numCode));
  }

  @PostMapping("/create")
  ResponseEntity<CurrencyDto> create(@RequestBody CurrencyDto dto) {
    return ResponseEntity.ok(service.create(dto));
  }
}
