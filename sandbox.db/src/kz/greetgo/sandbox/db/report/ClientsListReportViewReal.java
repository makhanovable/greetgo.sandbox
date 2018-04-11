package kz.greetgo.sandbox.db.report;

import kz.greetgo.sandbox.db.report.model.ClientListRow;


import java.io.OutputStream;

public class ClientsListReportViewReal implements ClientsListReportView{

    private final OutputStream out;

    public ClientsListReportViewReal(OutputStream out) {
        this.out = out;
    }

    @Override
    public void start(String title) {
//        Xlsx xlsx = new Xlsx();
    }

    @Override
    public void append(ClientListRow clientListRow) {

    }

    @Override
    public void finish(String userName) {

    }
}
