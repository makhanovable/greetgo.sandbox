package kz.greetgo.sandbox.stand.util;

import kz.greetgo.sandbox.controller.model.ColumnSortType;
import kz.greetgo.sandbox.controller.util.Util;
import kz.greetgo.sandbox.db.stand.model.ClientDot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PageUtils {
  public static <T> boolean cutPage(List<T> list, long offset, long pageSize) {

    int sizeAtTheBeginning = list.size();

    List<T> newList = new ArrayList<T>();
    for (int i = 0, c = list.size(); i < c && newList.size() < pageSize; i++) {
      if (i >= offset) newList.add(list.get(i));
    }

    list.clear();
    list.addAll(newList);


    return offset + pageSize < sizeAtTheBeginning;
  }

  public static List<ClientDot> getFilteredList(List<ClientDot> clientDots, String nameFilter) {
    if (nameFilter == null || nameFilter.length() == 0)
      return clientDots;

    String loweredNameFilter = nameFilter.toLowerCase();

    Stream<ClientDot> stream = clientDots.stream().filter(new Predicate<ClientDot>() {
      @Override
      public boolean test(ClientDot clientDot) {
        if (clientDot.surname.toLowerCase().contains(loweredNameFilter) ||
          clientDot.name.toLowerCase().contains(loweredNameFilter) ||
          clientDot.patronymic.toLowerCase().contains(loweredNameFilter))
          return true;

        return false;
      }
    });

    clientDots = stream.collect(Collectors.toList());

    return clientDots;
  }

  public static List<ClientDot> getSortedList(List<ClientDot> clientDots, ColumnSortType columnSortType, boolean sortAscend) {
    switch (columnSortType) {
      case AGE:
        clientDots = getListByAge(clientDots, sortAscend);
        break;
      case TOTALACCOUNTBALANCE:
        clientDots = getListByTotalAccountBalance(clientDots, sortAscend);
        break;
      case MAXACCOUNTBALANCE:
        clientDots = getListByMaxAccountBalance(clientDots, sortAscend);
        break;
      case MINACCOUNTBALANCE:
        clientDots = getListByMinAccountBalance(clientDots, sortAscend);
        break;
      default:
        clientDots = getDefaultList(clientDots);
    }

    return clientDots;
  }

  public static List<ClientDot> getDefaultList(List<ClientDot> clientDots) {
    return clientDots;
  }

  public static List<ClientDot> getListByAge(List<ClientDot> clientDots, boolean ascend) {
    if (ascend) {
      clientDots.sort(new Comparator<ClientDot>() {
        public int compare(ClientDot o1, ClientDot o2) {
          return Util.getAge(o1.birthDate) - Util.getAge(o2.birthDate);
        }
      });
    } else {
      clientDots.sort(new Comparator<ClientDot>() {
        public int compare(ClientDot o1, ClientDot o2) {
          return Util.getAge(o2.birthDate) - Util.getAge(o1.birthDate);
        }
      });
    }

    return clientDots;
  }

  public static List<ClientDot> getListByTotalAccountBalance(List<ClientDot> clientDots, boolean ascend) {
    if (ascend) {
      clientDots.sort(new Comparator<ClientDot>() {
        public int compare(ClientDot o1, ClientDot o2) {
          return Float.compare(Util.stringToFloat(o1.totalAccountBalance), Util.stringToFloat(o2.totalAccountBalance));
        }
      });
    } else {
      clientDots.sort(new Comparator<ClientDot>() {
        public int compare(ClientDot o1, ClientDot o2) {
          return Float.compare(Util.stringToFloat(o2.totalAccountBalance), Util.stringToFloat(o1.totalAccountBalance));
        }
      });
    }

    return clientDots;
  }

  public static List<ClientDot> getListByMaxAccountBalance(List<ClientDot> clientDots, boolean ascend) {
    if (ascend) {
      clientDots.sort(new Comparator<ClientDot>() {
        public int compare(ClientDot o1, ClientDot o2) {
          return Float.compare(Util.stringToFloat(o1.maxAccountBalance), Util.stringToFloat(o2.maxAccountBalance));
        }
      });
    } else {
      clientDots.sort(new Comparator<ClientDot>() {
        public int compare(ClientDot o1, ClientDot o2) {
          return Float.compare(Util.stringToFloat(o2.maxAccountBalance), Util.stringToFloat(o1.maxAccountBalance));
        }
      });
    }

    return clientDots;
  }

  public static List<ClientDot> getListByMinAccountBalance(List<ClientDot> clientDots, boolean ascend) {
    if (ascend) {
      clientDots.sort(new Comparator<ClientDot>() {
        public int compare(ClientDot o1, ClientDot o2) {
          return Float.compare(Util.stringToFloat(o1.minAccountBalance), Util.stringToFloat(o2.minAccountBalance));
        }
      });
    } else {
      clientDots.sort(new Comparator<ClientDot>() {
        public int compare(ClientDot o1, ClientDot o2) {
          return Float.compare(Util.stringToFloat(o2.minAccountBalance), Util.stringToFloat(o1.minAccountBalance));
        }
      });
    }

    return clientDots;
  }

}
