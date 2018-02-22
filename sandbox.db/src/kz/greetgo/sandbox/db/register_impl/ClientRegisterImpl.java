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
import kz.greetgo.sandbox.db.dao.ClientDao;
import kz.greetgo.sandbox.db.util.ClientUtils;
import kz.greetgo.sandbox.db.util.JdbcSandbox;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Bean
public class ClientRegisterImpl implements ClientRegister {

  public BeanGetter<ClientDao> clientDao;
  public BeanGetter<IdGenerator> idGenerator;
  public BeanGetter<JdbcSandbox> jdbcSandbox;

  @Override
  public List<ClientRecord> getClientInfoList(int limit, int page, String filter, final String orderBy, int desc) {

    if (limit <= 0) return new ArrayList<>();

    List<ClientRecord> result = new ArrayList<>();

    String[] orders = ClientUtils.sortableColumns;
    Boolean match = orderBy != null && Arrays.stream(orders).anyMatch(o -> o.equals(orderBy));

    int offset = limit * page;

    String queryFilter = ClientUtils.getFormattedFilter(filter);

    StringBuilder query = new StringBuilder();
    query.append("select c.id, c.name, c.surname, c.patronymic, date_part('year',age(c.birthDate)) as age, c.charm, ca.totalAccountBalance, ca.maximumBalance, ca.minimumBalance");
    query.append(" from (select * from Client where actual=true");
    if (queryFilter != null)
      query.append(" and lower(concat(name, surname, patronymic)) SIMILAR TO ?");
    query.append(") c");
    query.append(" left join (select client, max(money) maximumBalance, min(money) minimumBalance, sum(money) totalAccountBalance from ClientAccount group by client) ca on ca.client=c.id");
    if (match)
      query.append(" order by ").append(orderBy);
    else
      query.append(" order by concat(name, surname, patronymic)");
    if (desc == 1)
      query.append(" desc");
    query.append(" limit ? offset ?");

    jdbcSandbox.get().execute(connection -> {

      try (PreparedStatement ps = connection.prepareStatement(query.toString())) {

        int argIndex = 1;
        if (queryFilter != null)
          ps.setString(argIndex++, queryFilter);
        ps.setInt(argIndex++, limit);
        ps.setInt(argIndex, offset);

        try (ResultSet resultSet = ps.executeQuery()) {

          while (resultSet.next()) {
            ClientRecord record = ClientUtils.rsToClientRecord(resultSet);
            result.add(record);
          }
        }
      }

      return null;
    });

    return result;
  }

  @Override
  public long getClientsSize(String filter) {
    if (filter == null)
      return this.clientDao.get().countAll();
    filter = ClientUtils.getFormattedFilter(filter);
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


}
