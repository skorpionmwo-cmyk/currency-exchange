package ru.skillbox.currency.exchange.cbr;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ValCurs")
@XmlAccessorType(XmlAccessType.FIELD)
public class CbrRates {

  @XmlElement(name = "Valute")
  private List<CbrQuote> quotes = new ArrayList<>();

  public List<CbrQuote> quotes() {
    return quotes;
  }
}
