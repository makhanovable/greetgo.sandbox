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
    @Mapping("/recordList")
    public ClientToReturn getFilteredClientsInfo(@ParamsTo ClientsListParams clientsListParams,
                                                 @ParamsTo FilterSortParams filterSortParams) {

        if (filterSortParams.filterStr == null) { filterSortParams.filterStr = ""; }
        if (filterSortParams.sortOrder == null) { filterSortParams.sortOrder = ""; }
        if (filterSortParams.sortBy == null) { filterSortParams.sortBy = ""; }
        clientsListParams.filterSortParams = filterSortParams;

        return clientRegister.get().getFilteredClientsInfo(clientsListParams);
    }

    @ToJson
    @Mapping("/addNew")
    public ClientRecord addNewClient(@Par("clientToSave") @Json ClientToSave clientToSave) {
        return clientRegister.get().addNewClient(clientToSave);
    }

    @ToJson
    @Mapping("/update")
    public ClientRecord updateClient(@Par("clientToSave") @Json ClientToSave clientToSave) {
        return clientRegister.get().updateClient(clientToSave);
    }

    @AsIs
    @NoSecurity
    @Mapping("/remove")
    public String removeClient(@Par("clientID") String clientID) {
        return clientRegister.get().removeClient(clientID);
    }

    @ToJson
    @Mapping("/details/{clientID}")
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
    public int saveReportParams(@ParamsTo ReportParamsToSave reportParamsToSave,
                                @ParSession("personId") String personId) {

        UserInfo user = authRegister.get().getUserInfo(personId);
        String username = user.surname + " " + user.name + " " + user.patronymic;

        int report_id = RND.plusInt(100) + 1;

        if (reportParamsToSave.filterStr == null) {
            reportParamsToSave.filterStr = "";
        }
        if (reportParamsToSave.sortBy == null) {
            reportParamsToSave.sortBy = "";
        }
        if (reportParamsToSave.sortOrder== null) {
            reportParamsToSave.sortOrder = "";
        }

        reportParamsToSave.report_id = report_id;
        reportParamsToSave.username = username;

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
            FilterSortParams filterSortParams = new FilterSortParams();
            filterSortParams.filterStr = reportParams.filterStr;
            filterSortParams.sortBy = reportParams.sortBy;
            filterSortParams.sortOrder = reportParams.sortOrder;
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
            FilterSortParams filterSortParams = new FilterSortParams();
            filterSortParams.filterStr = reportParams.filterStr;
            filterSortParams.sortBy = reportParams.sortBy;
            filterSortParams.sortOrder = reportParams.sortOrder;
            ClientsListReportParams clientsListReportParams = new ClientsListReportParams(reportParams.username, view, filterSortParams);

            clientRegister.get().genClientListReport(clientsListReportParams);
        }

        tunnel.flushBuffer();
    }
}
