package kz.greetgo.sandbox.controller.controller;

import com.sun.net.httpserver.HttpContext;
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
import org.omg.PortableServer.Current;

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

        System.out.print(reportType);

        UserInfo user = authRegister.get().getUserInfo(personId);
        String username = user.surname + " " + user.name + " " + user.patronymic;

        int report_id = RND.plusInt(100) + 1;

        if (filterStr == null) {
            filterStr = "";
        }
        if (sortBy == null) {
            sortBy = "";
        }
        if (sortOrder== null) {
            sortOrder = "";
        }

        ReportParamsToSave reportParamsToSave = new ReportParamsToSave(report_id, username, reportType,
                                                                        filterStr, sortBy, sortOrder);

        return clientRegister.get().saveReportParams(reportParamsToSave);
    }

    @AsIs
    @NoSecurity
    @Mapping("/report/{reportID}")
    public void genClientListReport(@ParPath("reportID") int reportID, RequestTunnel tunnel) throws Exception {

        ReportParamsToSave reportParams = clientRegister.get().popReportParams(reportID);

        if ("PDF".equals(reportParams.report_type)) {
            
            // TODO: убирай такие выводы. Если нужно логирование, используй логирование. Для этого есть специальные библиотеки.
            System.out.println("Generating PDF document");

            String filename = reportParams.report_type + "-" + RND.intStr(10) + ".pdf";
            filename = URLEncoder.encode(filename, "UTF-8");

            tunnel.setResponseHeader("Content-Disposition", "attachment; filename=" + filename);
            tunnel.setResponseContentType("application/pdf");
            OutputStream out = tunnel.getResponseOutputStream();

            ClientsListReportPDFViewReal view = new ClientsListReportPDFViewReal(out);
            FilterSortParams filterSortParams = new FilterSortParams(reportParams.filterStr, reportParams.sortBy, reportParams.sortOrder);
            ClientsListReportParams clientsListReportParams = new ClientsListReportParams(reportParams.username, view, filterSortParams);

            clientRegister.get().genClientListReport(clientsListReportParams);
        } else {
            System.out.println("Generating XLSX document");

            String filename = reportParams.report_type + "-" + RND.intStr(10) + ".xlsx";
            filename = URLEncoder.encode(filename, "UTF-8");

            tunnel.setResponseHeader("Content-Disposition", "attachment; filename=" + filename);
            tunnel.setResponseContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            OutputStream out = tunnel.getResponseOutputStream();

            ClientsListReportViewReal view = new ClientsListReportViewReal(out);
            FilterSortParams filterSortParams = new FilterSortParams(reportParams.filterStr, reportParams.sortBy, reportParams.sortOrder);
            ClientsListReportParams clientsListReportParams = new ClientsListReportParams(reportParams.username, view, filterSortParams);

            clientRegister.get().genClientListReport(clientsListReportParams);
        }

        tunnel.flushBuffer();
    }
}
