package kz.greetgo.sandbox.controller.render;

import kz.greetgo.sandbox.controller.model.ClientRecord;

import java.util.Date;

public interface ClientRecordsReportView {
    void start();
    void append(ClientRecord row);
    void finish(String user, Date created_at, String link_to_download);
}
