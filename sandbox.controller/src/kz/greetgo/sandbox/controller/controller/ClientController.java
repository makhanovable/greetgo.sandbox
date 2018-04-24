package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.AuthRegister;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.report.ClientsListReportPDFViewReal;
import kz.greetgo.sandbox.controller.report.ClientsListReportView;
import kz.greetgo.sandbox.controller.report.ClientsListReportViewReal;
import kz.greetgo.sandbox.controller.security.NoSecurity;
import kz.greetgo.sandbox.controller.util.Controller;
import kz.greetgo.util.RND;

import java.io.*;
import java.net.URLEncoder;
import java.util.List;

import static com.sun.prism.impl.PrismSettings.trace;

@Bean
@Mapping("/client")
public class ClientController implements Controller {

    public BeanGetter<ClientRegister> clientRegister;
    public BeanGetter<AuthRegister> authRegister;

    @ToJson
    @Mapping("/clientsInfo/{pageID}/{filterStr}/{sortBy}/{sortOrder}")
    //TODO: 2.01. Имена мапингов контроллеров должны совпадать с именами методов контроллеров.
    public ClientToReturn getFilteredClients(@ParPath("pageID") String pageID, @ParPath("filterStr") String filterStr,
            @ParPath("sortBy") String sortBy, @ParPath("sortOrder") String sortOrder) {

        FilterSortParams filterSortParams = new FilterSortParams(filterStr, sortBy, sortOrder);
        ClientsListParams clientsListParams = new ClientsListParams(Integer.parseInt(pageID), filterSortParams);

        return clientRegister.get().getFilteredClientsInfo(clientsListParams);
    }

    @ToJson
    @Mapping("/addNewClient")
    public ClientRecord addNewClient(@Par("clientToSave") @Json ClientToSave clientToSave) {
        return clientRegister.get().addNewClient(clientToSave);
    }

    @ToJson
    @Mapping("/updateClient")
    public ClientRecord updateClient(@Par("clientToSave") @Json ClientToSave clientToSave) {
        return clientRegister.get().updateClient(clientToSave);
    }

    @AsIs
    @NoSecurity
    @Mapping("/removeClient")
    public String removeClient(@Par("clientID") String clientID) {
        return clientRegister.get().removeClient(clientID);
    }

    @ToJson
    @Mapping("/clientDetails/{clientID}")
    public ClientDetails getEditableClientInfo(@ParPath("clientID") String clientID) {
        return clientRegister.get().getEditableClientInfo(clientID);
    }

    @ToJson
    @Mapping("/charms")
    public List<Charm> getCharms() {
        return clientRegister.get().getCharms();
    }

    @ToJson
    @Mapping("/report")
    public int createReport(@Par("reportType") String reportType, @Par("filterStr") String filterStr,
                             @ParSession("personId") String personId, @Par("sortBy") String sortBy,
                             @Par("sortOrder") String sortOrder) {

        UserInfo user = authRegister.get().getUserInfo(personId);
        String username = user.surname + " " + user.name + " " + user.patronymic;

        FilterSortParams filterSortParams = new FilterSortParams(filterStr, sortBy, sortOrder);

        int report_id = RND.plusInt(100) + 1;
        ReportParamsToSave reportParamsToSave = new ReportParamsToSave(report_id, username, reportType, filterSortParams);

        return clientRegister.get().saveReportParams(reportParamsToSave);
    }

    @AsIs
    @NoSecurity
    @Mapping("/report/{reportID}")
    public void genClientListReport(@ParPath("reportID") int reportID, RequestTunnel tunnel) throws Exception {

        ReportParamsToSave reportParams = clientRegister.get().popReportParams(reportID);

        if ("pdf".equals(reportParams.report_type)) {
            String filename = reportParams.report_type + "-" + RND.intStr(10) + ".pdf";
            filename = URLEncoder.encode(filename, "UTF-8");

            tunnel.setResponseHeader("Content-Disposition", "attachment; filename=" + filename);
            OutputStream out = tunnel.getResponseOutputStream();

            ClientsListReportPDFViewReal view = new ClientsListReportPDFViewReal(out);
            ClientsListReportParams clientsListReportParams = new ClientsListReportParams(reportParams.username, view, reportParams.filterSortParams);

            clientRegister.get().genClientListReport(clientsListReportParams);
        } else {
            String filename = reportParams.report_type + "-" + RND.intStr(10) + ".xlsx";
            filename = URLEncoder.encode(filename, "UTF-8");

            tunnel.setResponseHeader("Content-Disposition", "attachment; filename=" + filename);
            OutputStream out = tunnel.getResponseOutputStream();

            ClientsListReportViewReal view = new ClientsListReportViewReal(out);
            ClientsListReportParams clientsListReportParams = new ClientsListReportParams(reportParams.username, view, reportParams.filterSortParams);

            clientRegister.get().genClientListReport(clientsListReportParams);
        }

        tunnel.flushBuffer();
    }
}
