package kz.greetgo.sandbox.db.report;

import kz.greetgo.sandbox.db.report.model.ClientListRow;

public interface ClientsListReportView {
    void start (String title);

    void append(ClientListRow clientListRow);

    void finish(String userName);
}
