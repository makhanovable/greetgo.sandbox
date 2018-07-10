package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.AuthRegister;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientRecordInfo;
import kz.greetgo.sandbox.controller.model.Options;
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
@Mapping("/client")
public class ClientController implements Controller {

    public BeanGetter<AuthRegister> authRegister;
    public BeanGetter<ClientRegister> clientRegister;

    @ToJson
    @NoSecurity
    @Mapping("/get_clients_list")
    public List<ClientRecord> getClientRecords(@Par("options") @Json Options options) {
        return clientRegister.get().getClientRecords(options);
    }

    @ToJson
    @NoSecurity
    @Mapping("/get_clients_list_count")
    public int getClientRecordsCount(@Par("filter") String filter) {
        return clientRegister.get().getClientRecordsCount(filter);
    }

    @ToJson
    @NoSecurity
    @Mapping("/add_new_client")
    public ClientRecord addNewClientRecord(@Json @Par("clientToSave") ClientDetails clientDetails) {
        return clientRegister.get().addNewClient(clientDetails);
    }

    @ToJson
    @NoSecurity
    @Mapping("/del_client")
    public void deleteClient(@Par("clientId") int clientId) {
        clientRegister.get().deleteClient(clientId);
    }

    @ToJson
    @NoSecurity
    @Mapping("/edit_client")
    public ClientRecord editClient(@Json @Par("clientToSave") ClientDetails clientDetails) {
        return clientRegister.get().editClient(clientDetails);
    }

    @ToJson
    @NoSecurity
    @Mapping("/get_client_info_by_id")
    public ClientDetails getClientById(@Par("clientId") int clientId) {
        return clientRegister.get().getClientById(clientId);
    }

    @ToJson
    @NoSecurity
    @Mapping("/get_charms")
    public List<Charm> getCharms() {
        return clientRegister.get().getCharms();
    }

    @ToJson
    @Mapping("/get_report/{type}")
    public void getReport(@ParSession("personId") String personId,
                          @ParPath("type") String type,
                          @Par("options") @Json Options options,
                          @Par("link") String link,
                          RequestTunnel tunnel) throws Exception {

        UserInfo userInfo = authRegister.get().getUserInfo(personId);

        String name = userInfo.surname + " " + userInfo.name;
        if (userInfo.patronymic != null)
            name += " " + userInfo.patronymic;

        tunnel.setResponseHeader("content-disposition", "attachment; filename = report." + type);

        OutputStream out = tunnel.getResponseOutputStream();
        PrintStream printStream = new PrintStream(out, false, "UTF-8");
        ClientRecordsReportView view;
        if (Objects.equals(type, "pdf"))
            view = new ClientRecordReportViewPdfImpl(printStream);
        else view = new ClientRecordReportViewXlsxImpl(printStream);

        clientRegister.get().renderClientList(options, view, name, link);
        printStream.flush();
        tunnel.flushBuffer();
    }

}
