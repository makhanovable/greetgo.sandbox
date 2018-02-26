package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.enums.AddressType;
import kz.greetgo.sandbox.controller.model.CharmRecord;
import kz.greetgo.sandbox.controller.model.ClientAddress;
import kz.greetgo.sandbox.controller.model.ClientDetail;
import kz.greetgo.sandbox.controller.model.ClientPhoneNumber;
import kz.greetgo.sandbox.controller.model.ClientPhoneNumberToSave;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.report.ClientRecordView;
import kz.greetgo.sandbox.db.SqlBuilder.ClientRecordReportViewQuery;
import kz.greetgo.sandbox.db.SqlBuilder.ClientRecordWebViewQuery;
import kz.greetgo.sandbox.db.SqlBuilder.CountClientRecordsQuery;
import kz.greetgo.sandbox.db.dao.CharmDao;
import kz.greetgo.sandbox.db.dao.ClientDao;
import kz.greetgo.sandbox.db.util.ClientRecordJdbc;
import kz.greetgo.sandbox.db.util.ClientRecordListView;
import kz.greetgo.sandbox.db.util.ClientUtils;
import kz.greetgo.sandbox.db.util.JdbcSandbox;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Bean
public class ClientRegisterImpl implements ClientRegister {

  public BeanGetter<ClientDao> clientDao;
  public BeanGetter<IdGenerator> idGenerator;
  public BeanGetter<JdbcSandbox> jdbcSandbox;

  public BeanGetter<CharmDao> charmDao;

  @Override
  public void generateReport(String filter, String orderBy, int order, ClientRecordView view) throws Exception {
    Map<String, String> charms = new HashMap<>();
    for (CharmRecord charmRecord : this.charmDao.get().getAll())
      charms.put(charmRecord.id, charmRecord.name);

    view.start(ClientUtils.reportHeaders);

    ClientRecordJdbc jdbc = new ClientRecordJdbc(new ClientRecordReportViewQuery(), filter, orderBy, order, view);
    jdbc.charms = charms;
    jdbcSandbox.get().execute(jdbc);

    view.finish();
  }


  @Override
  public List<ClientRecord> getClientInfoList(int limit, int page, String filter, final String orderBy, int desc) {

    if (limit <= 0) return new ArrayList<>();

    ClientRecordListView view = new ClientRecordListView();
    int offset = limit * page;
    ClientRecordJdbc jdbc = new ClientRecordJdbc(new ClientRecordWebViewQuery(), filter, orderBy, desc, limit, offset, view);

    jdbcSandbox.get().execute(jdbc);

    return view.list;
  }

  @Override
  public long getClientsSize(String filter) {

    if (filter == null)
      return this.clientDao.get().countAll();
    String finalFilter = ClientUtils.getFormattedFilter(filter);

    CountClientRecordsQuery query = new CountClientRecordsQuery();
    query.withFilter = finalFilter != null;
    final int[] result = new int[1];
    jdbcSandbox.get().execute(connection -> {
      try (PreparedStatement ps = connection.prepareStatement(query.generateSql(new StringBuilder()).toString())) {

        int argIndex = 1;
        if (finalFilter != null)
          ps.setString(argIndex++, finalFilter);

        try (ResultSet rs = ps.executeQuery()) {
          while (rs.next()) {
            result[0] = rs.getInt(1);
          }
          return null;
        }
      }
    });

//    return this.clientDao.get().countByFilter(filter);
    return result[0];
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
