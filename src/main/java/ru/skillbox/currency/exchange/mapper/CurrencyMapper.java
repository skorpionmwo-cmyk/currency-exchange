package ru.skillbox.currency.exchange.mapper;

import org.mapstruct.Mapper;
import ru.skillbox.currency.exchange.dto.CurrencyDto;
import ru.skillbox.currency.exchange.entity.Currency;

/** Maps stored currencies to and from the detailed API representation. */
@Mapper(componentModel = "spring")
public interface CurrencyMapper {

  CurrencyDto convertToDto(Currency currency);

  Currency convertToEntity(CurrencyDto currencyDto);
}
