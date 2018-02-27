package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.enums.AddressType;
import kz.greetgo.sandbox.controller.model.ClientAddress;
import kz.greetgo.sandbox.controller.model.ClientDetail;
import kz.greetgo.sandbox.controller.model.ClientPhoneNumber;
import kz.greetgo.sandbox.controller.model.ClientPhoneNumberToSave;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.report.ClientReportView;
import kz.greetgo.sandbox.db.dao.CharmDao;
import kz.greetgo.sandbox.db.dao.ClientDao;
import kz.greetgo.sandbox.db.jdbc.ClientListReportViewQuery;
import kz.greetgo.sandbox.db.jdbc.ClientListWebViewQuery;
import kz.greetgo.sandbox.db.jdbc.ClientNumberQuery;
import kz.greetgo.sandbox.db.util.ClientUtils;
import kz.greetgo.sandbox.db.util.JdbcSandbox;

import java.util.List;


@Bean
public class ClientRegisterImpl implements ClientRegister {

  public BeanGetter<ClientDao> clientDao;
  public BeanGetter<IdGenerator> idGenerator;
  public BeanGetter<JdbcSandbox> jdbcSandbox;

  public BeanGetter<CharmDao> charmDao;

  @Override
  public void generateReport(String filter, String orderBy, int order, ClientReportView view) throws Exception {
    ClientListReportViewQuery jdbc = new ClientListReportViewQuery(filter, orderBy, order, view);
    view.start(ClientUtils.reportHeaders);
    jdbcSandbox.get().execute(jdbc);
    view.finish();
  }

  @Override
  public List<ClientRecord> getClientInfoList(int limit, int page, String filter, final String orderBy, int desc) {
    // FIXME: 2/27/18 Если перейти на вторую страницу, и в фильтр ввести имя человека, то таблица будет пустой
    // FIXME: 2/27/18 Если добавить нового пользователя, то сортировка портится
    int offset = limit * page;
    ClientListWebViewQuery jdbc = new ClientListWebViewQuery(filter, orderBy, desc, limit, offset);
    return jdbcSandbox.get().execute(jdbc);
  }

  @Override
  public int getClientsSize(String filter) {

    return jdbcSandbox.get().execute(new ClientNumberQuery(filter));
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
        if (addr.type == AddressType.FACT)
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


}
