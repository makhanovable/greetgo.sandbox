package kz.greetgo.sandbox.controller.model;

public class ReportParamsToSave {
    public int report_id;
    public String report_type;
    public String username;
    public String filterStr;
    public String sortBy;
    public String sortOrder;

    public ReportParamsToSave(int report_id, String username, String report_type, String filterStr, String sortBy,
                              String sortOrder) {
        this.report_id = report_id;
        this.username = username;
        this.report_type = report_type;
        this.filterStr = filterStr;
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
    }

    public ReportParamsToSave() {

    }
}
