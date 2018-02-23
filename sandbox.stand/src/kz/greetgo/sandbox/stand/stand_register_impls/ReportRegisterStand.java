package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.register.ReportRegister;
import kz.greetgo.sandbox.controller.report.ClientReportView;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.ClientAccountDot;
import kz.greetgo.sandbox.db.stand.model.ClientDot;

@Bean
public class ReportRegisterStand implements ReportRegister {

  public BeanGetter<StandDb> db;

  @Override
  public void generateReport(String filter, String orderBy, int order, ClientReportView view) throws Exception {
    String[] headers = {"id", "name", "surname", "patronymic", "age", "charm", "total Account Balance", "maximum Balance", "minimum Balance"};


    view.start(headers);
    for (ClientDot clientDot : this.db.get().clientStorage.values()) {
      ClientRecord clientRecord = clientDot.toClientRecord();
      if (!this.db.get().clientAccountStorage.get(clientDot.id).isEmpty()) {
        ClientAccountDot cad = this.db.get().clientAccountStorage.get(clientDot.id).get(0);
        clientRecord.totalAccountBalance = 0;
        clientRecord.maximumBalance = cad.money;
        clientRecord.minimumBalance = cad.money;
      }

      for (ClientAccountDot clientAccountDot : this.db.get().clientAccountStorage.get(clientDot.id)) {
        clientRecord.totalAccountBalance += clientAccountDot.money;
        if (clientAccountDot.money > clientRecord.maximumBalance)
          clientRecord.minimumBalance = clientAccountDot.money;
        if (clientAccountDot.money < clientRecord.minimumBalance)
          clientRecord.minimumBalance = clientAccountDot.money;
      }
      view.appendRow(clientRecord);
    }
    view.finish();
  }
}
