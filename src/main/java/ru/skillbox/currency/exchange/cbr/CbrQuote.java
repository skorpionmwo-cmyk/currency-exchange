package ru.skillbox.currency.exchange.cbr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/** A currency quote from a Valute element of the CBR feed. */
@XmlAccessorType(XmlAccessType.FIELD)
public class CbrQuote {

  @XmlElement(name = "NumCode")
  private long numericCode;

  @XmlElement(name = "CharCode")
  private String code;

  @XmlElement(name = "Nominal")
  private long nominal;

  @XmlElement(name = "Name")
  private String name;

  @XmlElement(name = "Value")
  private String value;

  public long numericCode() {
    return numericCode;
  }

  public String code() {
    return code;
  }

  public long nominal() {
    return nominal;
  }

  public String name() {
    return name;
  }

  public double value() {
    return Double.parseDouble(value.trim().replace(',', '.'));
  }
}
