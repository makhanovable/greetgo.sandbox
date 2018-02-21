package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.enums.AddressType;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.report.ClientReport;
import kz.greetgo.sandbox.controller.report.ClientReportPDF;
import kz.greetgo.sandbox.controller.report.ClientReportXLSX;
import kz.greetgo.sandbox.db.dao.CharmDao;
import kz.greetgo.sandbox.db.dao.ClientDao;

import java.io.File;
import java.io.OutputStream;
import java.util.*;


@Bean
public class ClientRegisterImpl implements ClientRegister {

  public BeanGetter<ClientDao> clientDao;
  public BeanGetter<CharmDao> charmDao;
  public BeanGetter<IdGenerator> idGenerator;

  @SuppressWarnings({"Duplicates"})
  @Override
  public void generateReport(OutputStream out, String type, String orderBy, int order, String filter) throws Exception {
    ClientReport clientReport = null;
    String filename = "report" + idGenerator.get().newId() + "." + type;
    File file = new File(filename);
    String[] headers = {"id", "name", "surname", "patronymic", "age", "charm", "total Account Balance", "maximum Balance", "minimum Balance"};

    Map<String, String> charms = new HashMap<>();
    for (CharmRecord cr : charmDao.get().getAll())
      charms.put(cr.id, cr.name);

    switch (type) {
      case "pdf":
        clientReport = new ClientReportPDF(out, headers);
        ((ClientReportPDF) clientReport).setCharms(charms);
        break;
      case "xlsx":
        clientReport = new ClientReportXLSX(out, headers);
        ((ClientReportXLSX) clientReport).setCharms(charms);
        break;
    }


    if (clientReport != null) {
      long records = this.getClientsSize(filter);
      int chunk = 100;
      for (int page = 0; page < Math.ceil(records / (double) chunk); page++) {
        // FIXME: 2/21/18 Если количество клиентов=100_000, то тысячу раз будешь один и тот же запрос выполнять с сорировкой и выборкой?
        clientReport.appendRows(this.getClientInfoList(chunk, page, filter, orderBy, order));
      }

      clientReport.finish();
    }

  }


  @Override
  public List<ClientRecord> getClientInfoList(int limit, int page, String filter, final String orderBy, int desc) {

    if (limit == 0) return new ArrayList<>();

    String[] orders = {"age", "totalAccountBalance", "maximumBalance", "minimumBalance"};
    Boolean match = orderBy != null && Arrays.stream(orders).anyMatch(o -> o.equals(orderBy));

    String ob = match ? orderBy : "concat(name, surname, patronymic)";
    limit = limit > 100 ? 100 : limit;
    int offset = limit * page;
    String order = desc == 1 ? "desc" : "asc";
    filter = getFormattedFilter(filter);
    // FIXME: 2/21/18 ЕСЛИ ФИЛЬТРА НЕТ, ТО ВЫБОРКИ ПО "LIKE" ВООБЩЕ НЕ ДОЛЖНО БЫТЬ В ЗАПРОСЕ!!!
    return this.clientDao.get().getClients(limit, offset, ob, order, filter);
  }

  @Override
  public long getClientsSize(String filter) {
    if (filter == null)
      return this.clientDao.get().countAll();
    filter = this.getFormattedFilter(filter);
    return this.clientDao.get().countByFilter(filter);
  }

  @Override
  public int remove(List<String> id) {
    return this.clientDao.get().changeClientsActuality(id, false);
  }

  @Override
  public ClientDetail detail(String id) {
    ClientDetail clientDetail = this.clientDao.get().detail(id);

    if (clientDetail != null) {
      List<ClientAddress> addresses = this.clientDao.get().getAddresses(id);
      for (ClientAddress addr : addresses) {
        if (addr.type.equals(AddressType.FACT))
          clientDetail.actualAddress = addr;
        else if (addr.type.equals(AddressType.REG))
          clientDetail.registerAddress = addr;
      }
    }

    return clientDetail;
  }

  @Override
  public void addOrUpdate(ClientToSave clientToSave) {
    if (clientToSave.id == null) {
      clientToSave.id = this.idGenerator.get().newId();
      this.clientDao.get().insertClient(clientToSave);
    } else {
      this.clientDao.get().updateClient(clientToSave);
    }

    if (clientToSave.numbersToDelete != null) {
      for (ClientPhoneNumber cpn : clientToSave.numbersToDelete) {
        this.clientDao.get().deletePhone(cpn);
      }
    }

    if (clientToSave.numersToSave != null) {
      for (ClientPhoneNumberToSave cpn : clientToSave.numersToSave) {
        if (cpn.client == null) {
          cpn.client = clientToSave.id;
          this.clientDao.get().insertPhone(cpn);
        } else {
          if (cpn.oldNumber == null)
            this.clientDao.get().insertPhone(cpn);
          else
            this.clientDao.get().updatePhone(cpn);
        }
      }
    }

    if (clientToSave.actualAddress != null) {
      if (clientToSave.actualAddress.client == null) {
        clientToSave.actualAddress.client = clientToSave.id;
        this.clientDao.get().insertAddress(clientToSave.actualAddress);
      } else {
        this.clientDao.get().updateAddress(clientToSave.actualAddress);
      }
    }
    if (clientToSave.registerAddress != null) {
      if (clientToSave.registerAddress.client == null) {
        clientToSave.registerAddress.client = clientToSave.id;
        this.clientDao.get().insertAddress(clientToSave.registerAddress);
      } else {
        this.clientDao.get().updateAddress(clientToSave.registerAddress);
      }
    }
  }

  private String getFormattedFilter(String filter) {
    if (filter == null || filter.isEmpty())
      return "%%";
    String[] filters = filter.trim().split(" ");
    filter = String.join("|", filters);
    filter = "%(" + filter.toLowerCase() + ")%";
    return filter;
  }
}
