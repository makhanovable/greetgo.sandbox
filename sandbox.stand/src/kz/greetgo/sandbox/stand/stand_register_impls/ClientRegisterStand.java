package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.sandbox.controller.errors.NotFound;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.register.ReportRegister;
import kz.greetgo.sandbox.controller.register.model.ClientListReportInstance;
import kz.greetgo.sandbox.controller.register.report.client_list.ClientListReportView;
import kz.greetgo.sandbox.controller.register.report.client_list.ClientListReportViewPdf;
import kz.greetgo.sandbox.controller.register.report.client_list.ClientListReportViewXlsx;
import kz.greetgo.sandbox.controller.register.report.client_list.model.ReportFooterData;
import kz.greetgo.sandbox.controller.register.report.client_list.model.ReportHeaderData;
import kz.greetgo.sandbox.controller.register.report.client_list.model.ReportItemData;
import kz.greetgo.sandbox.controller.util.Util;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.CharmDot;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.db.stand.model.PersonDot;
import kz.greetgo.sandbox.stand.util.PageUtils;

import java.io.OutputStream;
import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Bean
public class ClientRegisterStand implements ClientRegister {

  public BeanGetter<StandDb> db;
  public BeanGetter<ReportRegister> clientListReportRegister;

  @Override
  public long getCount(ClientRecordRequest request) {
    List<ClientDot> clientDots = new ArrayList<>(db.get().clientStorage.values());
    clientDots = this.getFilteredList(clientDots, request.nameFilter);

    return clientDots.size();
  }

  @Override
  public List<ClientRecord> getRecordList(ClientRecordRequest request) {
    List<ClientDot> clientDots = new ArrayList<>(db.get().clientStorage.values());
    List<ClientRecord> clientRecords = new ArrayList<>();

    clientDots = this.getFilteredList(clientDots, request.nameFilter);
    clientDots = this.getSortedList(clientDots, request.columnSortType, request.sortAscend);

    PageUtils.cutPage(clientDots,
      request.clientRecordCountToSkip,
      request.clientRecordCount);

    for (ClientDot clientDot : clientDots)
      clientRecords.add(clientDot.toClientRecord());

    return clientRecords;
  }

  private List<ClientDot> getFilteredList(List<ClientDot> clientDots, String nameFilter) {
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

  private List<ClientDot> getSortedList(List<ClientDot> clientDots, ColumnSortType columnSortType, boolean sortAscend) {
    switch (columnSortType) {
      case AGE:
        clientDots = this.getListByAge(clientDots, sortAscend);
        break;
      case TOTALACCOUNTBALANCE:
        clientDots = this.getListByTotalAccountBalance(clientDots, sortAscend);
        break;
      case MAXACCOUNTBALANCE:
        clientDots = this.getListByMaxAccountBalance(clientDots, sortAscend);
        break;
      case MINACCOUNTBALANCE:
        clientDots = this.getListByMinAccountBalance(clientDots, sortAscend);
        break;
      default:
        clientDots = this.getDefaultList(clientDots);
    }

    return clientDots;
  }

  private List<ClientDot> getDefaultList(List<ClientDot> clientDots) {
    return clientDots;
  }

  private List<ClientDot> getListByAge(List<ClientDot> clientDots, boolean ascend) {
    if (ascend) {
      clientDots.sort(new Comparator<ClientDot>() {
        public int compare(ClientDot o1, ClientDot o2) {
          return o1.age - o2.age;
        }
      });
    } else {
      clientDots.sort(new Comparator<ClientDot>() {
        public int compare(ClientDot o1, ClientDot o2) {
          return o2.age - o1.age;
        }
      });
    }

    return clientDots;
  }

  private List<ClientDot> getListByTotalAccountBalance(List<ClientDot> clientDots, boolean ascend) {
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

  private List<ClientDot> getListByMaxAccountBalance(List<ClientDot> clientDots, boolean ascend) {
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

  private List<ClientDot> getListByMinAccountBalance(List<ClientDot> clientDots, boolean ascend) {
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

  @Override
  public void removeRecord(long id) {
    Map<Long, ClientDot> clientDotMap = db.get().clientStorage;

    if (clientDotMap.remove(id) == null)
      throw new NotFound();
  }

  @Override
  public ClientDetails getDetails(Long id) {
    List<CharmDot> charmDots = new ArrayList<>(db.get().charmStorage.values());
    ClientDetails clientDetails;

    if (id == null) {
      clientDetails = new ClientDetails();

      clientDetails.id = null;
      clientDetails.surname = "";
      clientDetails.name = "";
      clientDetails.patronymic = "";
      clientDetails.gender = Gender.EMPTY;
      clientDetails.birthdate = "";
      clientDetails.charmId = charmDots.get(0).toCharm().id;

      clientDetails.registrationAddressInfo = new AddressInfo();
      clientDetails.registrationAddressInfo.type = AddressType.REGISTRATION;
      clientDetails.registrationAddressInfo.street = "";
      clientDetails.registrationAddressInfo.house = "";
      clientDetails.registrationAddressInfo.flat = "";

      clientDetails.factualAddressInfo = new AddressInfo();
      clientDetails.factualAddressInfo.type = AddressType.REGISTRATION;
      clientDetails.factualAddressInfo.street = "";
      clientDetails.factualAddressInfo.house = "";
      clientDetails.factualAddressInfo.flat = "";

      clientDetails.phones = new ArrayList<>();
    } else {
      ClientDot clientDot = db.get().clientStorage.get(id);
      clientDetails = clientDot.toClientDetails();
    }

    for (CharmDot charmDot : charmDots)
      clientDetails.charmList.add(charmDot.toCharm());

    return clientDetails;
  }

  @Override
  public void saveDetails(ClientDetailsToSave detailsToSave) {
    Map<Long, ClientDot> clientDotMap = db.get().clientStorage;
    ClientDot clientDot;
    long id = db.get().curClientId.getAndIncrement();
    db.get().curClientId.set(id + 1);

    if (detailsToSave.id == null) {
      clientDot = new ClientDot();
      clientDot.toClientDot(detailsToSave, id, db.get().charmStorage);
      clientDotMap.put(id, clientDot);
    } else {
      clientDot = clientDotMap.get(detailsToSave.id);
      clientDot.toClientDot(detailsToSave, null, db.get().charmStorage);
    }
  }

  @Override
  public String prepareRecordListStream(String personId, ClientRecordRequest request, FileContentType fileContentType)
    throws Exception {
    return clientListReportRegister.get().save(personId, request, fileContentType);
  }

  @Override
  public void streamRecordList(String reportIdInstance, OutputStream outputStream, RequestTunnel requestTunnel)
    throws Exception {
    ClientListReportInstance clientListReportInstance =
      clientListReportRegister.get().checkForValidity(reportIdInstance);

    FileContentType fileContentType = FileContentType.valueOf(clientListReportInstance.fileTypeName);
    clientListReportRegister.get().prepareForGeneration(requestTunnel, "client_record", fileContentType);

    PersonDot personDot = db.get().personStorage.get(clientListReportInstance.personId);

    ClientListReportView reportView;
    switch (fileContentType) {
      case PDF:
        reportView = new ClientListReportViewPdf(outputStream);
        break;
      default:
        reportView = new ClientListReportViewXlsx(outputStream);
        break;
    }

    this.generateReportView(reportView, ClientRecordRequest.deserialize(clientListReportInstance.request),
      Util.getFullname(personDot.surname, personDot.name, personDot.patronymic));
  }

  private void generateReportView(ClientListReportView reportView, ClientRecordRequest request, String authorName) throws Exception {
    List<ClientDot> clientDots = new ArrayList<>(db.get().clientStorage.values());
    clientDots = this.getFilteredList(clientDots, request.nameFilter);
    clientDots = this.getSortedList(clientDots, request.columnSortType, request.sortAscend);

    ReportHeaderData headerData = new ReportHeaderData();
    headerData.columnSortType = request.columnSortType;
    reportView.start(headerData);

    for (ClientDot clientDot : clientDots)
      reportView.append(clientDotToReportItemData(clientDot));

    ReportFooterData reportFooterData = new ReportFooterData();
    reportFooterData.createdAt = Date.from(Instant.now());
    reportFooterData.createdBy = authorName;
    reportView.finish(reportFooterData);
  }

  private ReportItemData clientDotToReportItemData(ClientDot clientDot) throws Exception {
    ReportItemData ret = new ReportItemData();

    ret.fullname = Util.getFullname(clientDot.surname, clientDot.name, clientDot.patronymic);
    ret.charmName = clientDot.charm.name;
    ret.age = clientDot.age;
    ret.totalAccountBalance = Util.stringToFloat(clientDot.totalAccountBalance);
    ret.maxAccountBalance = Util.stringToFloat(clientDot.maxAccountBalance);
    ret.minAccountBalance = Util.stringToFloat(clientDot.minAccountBalance);

    return ret;
  }
}
