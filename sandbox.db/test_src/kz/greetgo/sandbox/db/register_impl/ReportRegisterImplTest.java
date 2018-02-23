package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.enums.GenderType;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.register.ReportRegister;
import kz.greetgo.sandbox.controller.report.ClientReportView;
import kz.greetgo.sandbox.db.stand.model.CharmDot;
import kz.greetgo.sandbox.db.stand.model.ClientAccountDot;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.db.test.dao.AccountTetsDao;
import kz.greetgo.sandbox.db.test.dao.CharmTestDao;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.sandbox.db.test.util.RegisterTestDataUtils;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;

public class ReportRegisterImplTest extends ParentTestNg {

  public BeanGetter<ReportRegister> reportRegister;
  public BeanGetter<ClientTestDao> clientTestDao;
  public BeanGetter<CharmTestDao> charmTestDao;
  public BeanGetter<IdGenerator> idGenerator;
  public BeanGetter<AccountTetsDao> accountTetsDao;

  @Test
  void generateReportXLSXTest() throws Exception {
    this.clientTestDao.get().clear();
    this.charmTestDao.get().clear();

    //insert data to db
    insertRndCharms();
    List<ClientDot> clients = new ArrayList<>();
    Map<String, List<ClientAccountDot>> accounts = new HashMap<>();
    this.rndClientsWithAccounts(clients, accounts);

    Map<String, String> charms = new HashMap<>();
    for (CharmDot charmDot : this.charmTestDao.get().getAll())
      charms.put(charmDot.id, charmDot.name);

    ClientReportTestView testView = new ClientReportTestView();

    //
    //
    this.reportRegister.get().generateReport(null, null, 0, testView);
    //
    //

    int index = 0;
    assertThat(testView.rows).isNotEmpty();

    List<ClientRecord> expectedList = RegisterTestDataUtils.fromClientDotListToRecordList(clients, accounts);

    RegisterTestDataUtils.sortClientRecordList(expectedList, null, 0);

    for (ClientRecord clientRecord : testView.rows) {
      ClientRecord assertion = expectedList.get(index++);
      assertThat(clientRecord.id).isEqualTo(assertion.id);
      assertThat(clientRecord.name).isEqualTo(assertion.name);
      assertThat(clientRecord.surname).isEqualTo(assertion.surname);
      assertThat(clientRecord.patronymic).isEqualTo(assertion.patronymic);
      assertThat(clientRecord.age).isEqualTo(assertion.age);
      assertThat(clientRecord.charm).isEqualTo(charms.get(assertion.charm));
      assertThat(clientRecord.totalAccountBalance).isEqualTo(assertion.totalAccountBalance);
      assertThat(clientRecord.maximumBalance).isEqualTo(assertion.maximumBalance);
      assertThat(clientRecord.minimumBalance).isEqualTo(assertion.minimumBalance);
    }
  }


  private static class ClientReportTestView implements ClientReportView {
    public String[] headers;
    public List<ClientRecord> rows = new ArrayList<>();

    @Override
    public void start(String[] headers) throws Exception {
      this.headers = headers;
    }

    @Override
    public void appendRow(ClientRecord record) throws Exception {
      this.rows.add(record);
    }

    @Override
    public void finish() throws Exception {

    }
  }

  @SuppressWarnings("Duplicates")
  private ClientDot rndClientDot() {

    ClientDot c = new ClientDot();
    c.id = idGenerator.get().newId();
    c.name = idGenerator.get().newId();
    c.surname = idGenerator.get().newId();
    c.patronymic = idGenerator.get().newId();
    c.charm = RND.str(10);
    c.gender = RND.someEnum(GenderType.values());
    c.birthDate = RND.dateYears(-100, 0);

    Calendar cal = Calendar.getInstance();
    cal.setTime(c.birthDate);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    c.birthDate = cal.getTime();
    return c;
  }

  @SuppressWarnings("Duplicates")
  private void rndClientsWithAccounts(List<ClientDot> clients, Map<String, List<ClientAccountDot>> accountDotMap) throws Exception {
    List<CharmDot> charms = this.charmTestDao.get().getAll();
    if (charms == null || charms.isEmpty())
      throw new Exception("charms not initialized");

    for (int i = 0; i < 100; i++) {
      ClientDot cd = this.rndClientDot();
      cd.charm = charms.get(RND.plusInt(charms.size())).id;
      cd.birthDate.setTime(-2177474400000L + i * 31536000000L); //1900, 1900 + 1, 1900 + 2 ...
      this.clientTestDao.get().insertClientDot(cd);

      List<ClientAccountDot> accList = new ArrayList<>();
      for (int j = 0; j < 2 + RND.plusInt(5); j++) {
        ClientAccountDot cad = new ClientAccountDot();
        cad.money = (float) RND.plusDouble(1000000f, 10);
        cad.id = idGenerator.get().newId();
        cad.number = idGenerator.get().newId();
        cad.client = cd.id;

        this.accountTetsDao.get().insertAccaount(cad);
        accList.add(cad);
      }
      clients.add(cd);
      accountDotMap.put(cd.id, accList);
    }
  }

  @SuppressWarnings("Duplicates")
  private void insertRndCharms() {
    for (int i = 0; i < 10; i++) {
      CharmDot charmDot = new CharmDot();
      charmDot.id = idGenerator.get().newId();
      charmDot.name = RND.str(10);
      this.charmTestDao.get().insertCharmDot(charmDot);
    }
  }

}
