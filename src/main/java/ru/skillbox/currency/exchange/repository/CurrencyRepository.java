package ru.skillbox.currency.exchange.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.skillbox.currency.exchange.entity.Currency;

/** Persistence operations for currency records. */
public interface CurrencyRepository extends JpaRepository<Currency, Long> {

  Optional<Currency> findFirstByIsoNumCodeOrderByIsoCharCodeAscIdAsc(Long isoNumCode);

  List<Currency> findAllByIsoCharCodeIn(Collection<String> isoCharCodes);
}
