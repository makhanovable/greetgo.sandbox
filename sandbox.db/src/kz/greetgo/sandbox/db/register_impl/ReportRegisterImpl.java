package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.CharmRecord;
import kz.greetgo.sandbox.controller.register.ReportRegister;
import kz.greetgo.sandbox.controller.report.ClientReportView;
import kz.greetgo.sandbox.db.dao.CharmDao;
import kz.greetgo.sandbox.db.util.ClientReportJdbc;
import kz.greetgo.sandbox.db.util.ClientUtils;
import kz.greetgo.sandbox.db.util.JdbcSandbox;

import java.util.HashMap;
import java.util.Map;

@Bean
public class ReportRegisterImpl implements ReportRegister {

  public BeanGetter<JdbcSandbox> jdbcSandbox;
  public BeanGetter<CharmDao> charmDao;

  @Override
  public void generateReport(String filter, String orderBy, int order, ClientReportView view) throws Exception {
    Map<String, String> charms = new HashMap<>();
    for (CharmRecord charmRecord : this.charmDao.get().getAll())
      charms.put(charmRecord.id, charmRecord.name);
    view.start(ClientUtils.reportHeaders);
    jdbcSandbox.get().execute(new ClientReportJdbc(filter, orderBy, order, view, charms));
    view.finish();
  }

}
