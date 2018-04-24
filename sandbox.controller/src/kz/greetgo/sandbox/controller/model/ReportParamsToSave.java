package kz.greetgo.sandbox.controller.model;

public class ReportParamsToSave {
    public int report_id;
    public String report_type;
    public String username;
    public FilterSortParams filterSortParams;

    public ReportParamsToSave(int report_id, String report_type, String username, FilterSortParams filterSortParams) {
        this.report_id = report_id;
        this.username = username;
        this.report_type = report_type;
        this.filterSortParams = filterSortParams;
    }
}
