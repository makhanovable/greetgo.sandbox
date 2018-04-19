package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.report.ClientsListReportPDFViewReal;
import kz.greetgo.sandbox.controller.report.ClientsListReportView;
import kz.greetgo.sandbox.controller.report.ClientsListReportViewReal;
import kz.greetgo.sandbox.controller.security.NoSecurity;
import kz.greetgo.sandbox.controller.util.Controller;

import java.io.*;
import java.util.List;

@Bean
@Mapping("/client")
public class ClientController implements Controller {

    public BeanGetter<ClientRegister> clientRegister;

    @ToJson
    @Mapping("/clientsInfo/{pageID}/{filterStr}/{sortBy}/{sortOrder}")
    //TODO: 2.01. Имена мапингов контроллеров должны совпадать с именами методов контроллеров.
    public ClientToReturn filteredClients(@ParPath("pageID") String pageID, @ParPath("filterStr") String filterStr,
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

    @AsIs
    @NoSecurity
    @Mapping("/report")
    public void createReport(@ParPath("reportType") String reportType,@ParPath("filterStr") String filterStr,
                             @ParPath("username") String username,
                               @ParPath("sortBy") String sortBy, @ParPath("sortOrder") String sortOrder) {
        String home = System.getProperty("user.home");
        File file = new File(home+"/Downloads/report.xlsx");
        OutputStream outf;
        try {
            outf = new FileOutputStream(file);
        } catch (Exception e) {
            if (e instanceof FileNotFoundException) throw (RuntimeException) e;
            throw new RuntimeException("Report file create error",e);
        }

        ClientsListReportView view = null;
        if ("excel".equals(reportType)) {
            view = new ClientsListReportViewReal(outf);
        } else
        if ("pdf".equals(reportType)) {
            view = new ClientsListReportPDFViewReal(outf);
        }

//        clientRegister.get().genClientListReport(username, view, filterStr, sortBy, sortOrder);
    }
}
