package ru.skillbox.currency.exchange.dto;

/** Name and ruble value of a currency for list responses. */
public record CurrencyShortDto(String name, Double value) {}
