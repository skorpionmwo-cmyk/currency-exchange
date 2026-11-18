package ru.skillbox.currency.exchange.cbr;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.nio.charset.Charset;
import javax.xml.bind.JAXBException;
import org.junit.jupiter.api.Test;

class CurrencyLoaderTest {

  private final CurrencyLoader loader =
      new CurrencyLoader(URI.create("https://cbr.ru/scripts/XML_daily.asp"));

  CurrencyLoaderTest() throws JAXBException {}

  @Test
  void readsTheDeclaredEncodingAndNominal() throws Exception {
    CbrQuote quote = loader.read(xml("""
        <?xml version="1.0" encoding="windows-1251"?>
        <ValCurs Date="19.11.2026">
          <Valute ID="R01030">
            <NumCode>012</NumCode>
            <CharCode>DZD</CharCode>
            <Nominal>100</Nominal>
            <Name>Алжирских динаров</Name>
            <Value>60,0640</Value>
            <VunitRate>0,60064</VunitRate>
          </Valute>
        </ValCurs>
        """)).get(0);

    assertThat(quote.code()).isEqualTo("DZD");
    assertThat(quote.name()).isEqualTo("Алжирских динаров");
    assertThat(quote.numericCode()).isEqualTo(12);
    assertThat(quote.nominal()).isEqualTo(100);
    assertThat(quote.value()).isEqualTo(60.064);
  }

  @Test
  void rejectsEmptyFeed() {
    assertThatThrownBy(() -> loader.read(xml("<ValCurs/>")))
        .isInstanceOf(JAXBException.class)
        .hasMessageContaining("no currency quotes");
  }

  private static ByteArrayInputStream xml(String source) {
    return new ByteArrayInputStream(source.getBytes(Charset.forName("windows-1251")));
  }
}
