package ru.skillbox.currency.exchange.cbr;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Component
public class CurrencyLoader {
  private static final int TIMEOUT_MS = (int) Duration.ofSeconds(15).toMillis();

  private final URI source;
  private final JAXBContext context;

  public CurrencyLoader(@Value("${currency.cbr.url}") URI source) throws JAXBException {
    this.source = source;
    this.context = JAXBContext.newInstance(CbrRates.class);
  }

  public List<CbrQuote> load()
      throws IOException, JAXBException, ParserConfigurationException, SAXException {
    var connection = source.toURL().openConnection();
    connection.setConnectTimeout(TIMEOUT_MS);
    connection.setReadTimeout(TIMEOUT_MS);

    try (InputStream body = connection.getInputStream()) {
      return read(body);
    }
  }

  List<CbrQuote> read(InputStream body)
      throws JAXBException, ParserConfigurationException, SAXException {
    SAXParserFactory parser = SAXParserFactory.newInstance();
    parser.setNamespaceAware(true);
    parser.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    var xml = new SAXSource(parser.newSAXParser().getXMLReader(), new InputSource(body));
    var rates = (CbrRates) context.createUnmarshaller().unmarshal(xml);
    if (rates.quotes().isEmpty()) {
      throw new JAXBException("The CBR feed contains no currency quotes");
    }
    return rates.quotes();
  }
}
