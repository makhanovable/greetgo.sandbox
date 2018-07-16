package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.Charm;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientRequestOptions;
import kz.greetgo.sandbox.controller.report.ClientRecordsReportView;

import java.util.List;

public interface ClientRegister {

    List<ClientRecord> getClientList(ClientRequestOptions options);

    int getClientListCount(String filter);

    void deleteClient(Long clientId);

    ClientRecord addClient(ClientDetails details);

    ClientRecord editClient(ClientDetails details);

    ClientDetails getClientDetails(Long clientId);

    List<Charm> getCharms();

    void renderClientList(ClientRequestOptions options,
                          ClientRecordsReportView view,
                          String username, String link);

}
