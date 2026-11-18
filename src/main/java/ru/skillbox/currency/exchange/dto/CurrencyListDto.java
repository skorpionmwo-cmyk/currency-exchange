package ru.skillbox.currency.exchange.dto;

import java.util.List;

/** Response containing the list of stored currencies. */
public record CurrencyListDto(List<CurrencyShortDto> currencies) {}
