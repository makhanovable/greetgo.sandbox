package kz.greetgo.sandbox.controller.register;

import kz.greetgo.sandbox.controller.model.Charm;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientRecordInfo;
import kz.greetgo.sandbox.controller.model.Options;
import kz.greetgo.sandbox.controller.render.ClientRecordsReportView;

import java.sql.Blob;
import java.util.List;

public interface ClientRegister {

    ClientRecordInfo getClientRecords(Options options);

    void deleteClient(int clientId);

    ClientRecord addNewClient(ClientDetails details);

    ClientRecord editClient(ClientDetails details);

    ClientDetails getClientById(int clientId);

    List<Charm> getCharms();

    void renderClientList(Options options, ClientRecordsReportView view);

}
