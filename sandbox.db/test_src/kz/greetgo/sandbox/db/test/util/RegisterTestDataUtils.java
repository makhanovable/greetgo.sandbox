package kz.greetgo.sandbox.db.test.util;

import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.db.stand.model.ClientAccountDot;
import kz.greetgo.sandbox.db.stand.model.ClientDot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RegisterTestDataUtils {

  public static void sortClientRecordList(List<ClientRecord> list, String orderBy, int order) {
    String ob = orderBy == null ? "default" : orderBy;

    list.sort((o1, o2) -> {
      if (order == 1) {
        ClientRecord tmp = o1;
        o1 = o2;
        o2 = tmp;
      }
      switch (ob) {
        case "age":
          return Integer.compare(o1.age, o2.age);
        case "totalAccountBalance":
          return Double.compare(o1.totalAccountBalance, o2.totalAccountBalance);
        case "maximumBalance":
          return Double.compare(o1.maximumBalance, o2.maximumBalance);
        case "minimumBalance":
          return Double.compare(o1.minimumBalance, o2.minimumBalance);
        default:
          String fio1 = o1.getFIO().toLowerCase();
          String fio2 = o2.getFIO().toLowerCase();
          return fio1.compareTo(fio2);
      }

    });
  }

  public static List<ClientDot> filterClientDotList(List<ClientDot> list, String filter) {

    if (filter != null && !filter.isEmpty()) {
      String[] filterTokens = filter.trim().split(" ");
      return list.stream().filter(o -> Arrays.stream(filterTokens).anyMatch(y -> o.getFIO().toLowerCase().contains(y.toLowerCase()))).collect(Collectors.toList());
    } else
      return list;
  }

  public static List<ClientRecord> fromClientDotListToRecordList(List<ClientDot> clientDots, Map<String, List<ClientAccountDot>> accounts) {
    List<ClientRecord> records = new ArrayList<>();
    for (ClientDot clientDot : clientDots) {
      ClientRecord cr = clientDot.toClientRecord();
      List<ClientAccountDot> accList = accounts.get(clientDot.id);

      if (accList != null && !accList.isEmpty()) {
        float max = accList.get(0).money;
        float min = accList.get(0).money;
        float total = 0;
        for (ClientAccountDot cad : accList) {
          total += cad.money;
          if (max < cad.money)
            max = cad.money;
          if (min > cad.money)
            min = cad.money;
        }
        cr.totalAccountBalance = total;
        cr.maximumBalance = max;
        cr.minimumBalance = min;
      }
      records.add(cr);
    }
    return records;
  }
}
