package ru.skillbox.currency.exchange.cbr;

import java.io.IOException;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;
import ru.skillbox.currency.exchange.service.CurrencySyncService;

/** Refreshes currency rates from the configured CBR feed. */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "currency.cbr.sync-enabled", havingValue = "true")
public class CurrencySyncJob {
  private final CurrencyLoader loader;
  private final CurrencySyncService service;

  /** Fetches the current feed and applies it to the currency table. */
  @Scheduled(fixedRateString = "${currency.cbr.interval-ms}")
  public void refresh() {
    try {
      log.info("Starting CBR currency synchronization");
      int count = service.update(loader.load());
      log.info("CBR currency synchronization completed: {} quotes processed", count);
    } catch (IOException | JAXBException | ParserConfigurationException | SAXException
        | RuntimeException exception) {
      log.error("CBR currency synchronization failed", exception);
    }
  }
}
