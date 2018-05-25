package kz.greetgo.sandbox.controller.model;

import java.util.List;

public class ClientAccountInfoPage {
  public List<ClientAccountInfo> items;
  public int totalItemsCount;

  public ClientAccountInfoPage(List<ClientAccountInfo> items, int totalItemsCount) {
    this.items = items;
    this.totalItemsCount = totalItemsCount;
  }
}
