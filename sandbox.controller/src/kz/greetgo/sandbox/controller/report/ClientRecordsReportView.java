package kz.greetgo.sandbox.controller.report;

import kz.greetgo.sandbox.controller.model.ClientRecordReportRow;

import java.util.Date;

public interface ClientRecordsReportView {
    void start();
    void append(ClientRecordReportRow row);
    void finish(String user, Date created_at, String link_to_download);
}
