package kz.greetgo.sandbox.controller.report;

import kz.greetgo.sandbox.controller.report.model.ClientListRow;

public interface ClientsListReportView {
    void start (String title);

    void append(ClientListRow clientListRow);

    void finish(String userName);
}
