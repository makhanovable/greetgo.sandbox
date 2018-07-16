package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.AuthRegister;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.register.MigrationRegister;
import kz.greetgo.sandbox.controller.report.ClientRecordReportViewPdfImpl;
import kz.greetgo.sandbox.controller.report.ClientRecordReportViewXlsxImpl;
import kz.greetgo.sandbox.controller.report.ClientRecordsReportView;
import kz.greetgo.sandbox.controller.security.NoSecurity;
import kz.greetgo.sandbox.controller.util.Controller;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Objects;

@Bean
@Mapping("/migration")
public class MigrationController implements Controller {

    public BeanGetter<MigrationRegister> migrationRegister;

    @Mapping("/launch")
    public void launch() throws Exception {
        migrationRegister.get().start();
    }

}
