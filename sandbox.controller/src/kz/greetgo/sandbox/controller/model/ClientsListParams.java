package kz.greetgo.sandbox.controller.model;

public class ClientsListParams {
    public int pageID;
    public FilterSortParams filterSortParams;

    public ClientsListParams(int pageID, FilterSortParams filterSortParams) {
        this.pageID = pageID;
        this.filterSortParams = filterSortParams;
    }
}
