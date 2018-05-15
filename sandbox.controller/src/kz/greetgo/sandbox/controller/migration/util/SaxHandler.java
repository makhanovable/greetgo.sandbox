package kz.greetgo.sandbox.controller.migration.util;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public abstract class SaxHandler extends DefaultHandler {
  private final List<String> pathList = new ArrayList<>();

  protected String path() {
    StringBuilder sb = new StringBuilder();
    for (String pathElement : pathList) {
      sb.append('/').append(pathElement);
    }
    return sb.toString();
  }

  private StringBuilder text = null;

  protected String text() {
    if (text == null) return "";
    return text.toString();
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    if (text == null) text = new StringBuilder();
    text.append(ch, start, length);
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    pathList.add(localName);

    try {
      startingTag(attributes);
    } catch (Exception e) {
      if (e instanceof SAXException) throw (SAXException) e;
      if (e instanceof RuntimeException) throw (RuntimeException) e;
      throw new RuntimeException(e);
    }

    text = null;
  }


  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    pathList.remove(pathList.size() - 1);

    try {
      endedTag(localName);
    } catch (Exception e) {
      if (e instanceof SAXException) throw (SAXException) e;
      if (e instanceof RuntimeException) throw (RuntimeException) e;
      throw new RuntimeException(e);
    }

    text = null;
  }


  abstract protected void startingTag(Attributes attributes) throws Exception;

  abstract protected void endedTag(String tagName) throws Exception;
}
