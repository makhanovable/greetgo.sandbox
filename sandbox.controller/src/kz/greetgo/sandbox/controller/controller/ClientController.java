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
import kz.greetgo.sandbox.controller.model.ClientRequestOptions;
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
    @Mapping("/get-list")
    public List<ClientRecord> getClientList(@Par("options") @Json ClientRequestOptions options) {
        return clientRegister.get().getClientList(options);
    }

    @ToJson
    @NoSecurity
    @Mapping("/get-list-count")
    public int getClientListCount(@Par("filter") String filter) {
        return clientRegister.get().getClientListCount(filter);
    }

    @ToJson
    @NoSecurity
    @Mapping("/add")
    public ClientRecord addClient(@Json @Par("toSave") ClientDetails clientDetails) {
        return clientRegister.get().addClient(clientDetails);
    }

    @ToJson
    @NoSecurity
    @Mapping("/delete")
    public void deleteClient(@Par("clientId") int clientId) {
        clientRegister.get().deleteClient(clientId);
    }

    @ToJson
    @NoSecurity
    @Mapping("/edit")
    public ClientRecord editClient(@Json @Par("toSave") ClientDetails clientDetails) {
        return clientRegister.get().editClient(clientDetails);
    }

    @ToJson
    @NoSecurity
    @Mapping("/get-details")
    public ClientDetails getClientDetails(@Par("clientId") int clientId) {
        return clientRegister.get().getClientDetails(clientId);
    }

    @ToJson
    @NoSecurity
    @Mapping("/get-charms")
    public List<Charm> getCharms() {
        return clientRegister.get().getCharms();
    }

    @ToJson
    @Mapping("/render-list/{type}")
    public void renderClientList(@ParSession("personId") String personId,
                          @ParPath("type") String type,
                          @Par("options") @Json ClientRequestOptions options,
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
