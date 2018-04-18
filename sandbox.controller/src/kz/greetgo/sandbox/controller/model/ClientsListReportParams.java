package kz.greetgo.sandbox.controller.model;

import kz.greetgo.sandbox.controller.report.ClientsListReportView;

public class ClientsListReportParams {
    public String username;
    public ClientsListReportView view;
    public FilterSortParams filterSortParams;

    public ClientsListReportParams (String username, ClientsListReportView view, FilterSortParams filterSortParams) {
        this.username = username;
        this.view = view;
        this.filterSortParams = filterSortParams;
    }
 }
