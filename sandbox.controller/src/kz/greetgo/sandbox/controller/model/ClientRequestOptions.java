package kz.greetgo.sandbox.controller.model;

public class ClientRequestOptions {
    public String filter, order, page, size;
    public SortBy sort;

    @Override
    public String toString() {
        return "ClientRequestOptions{" +
                "filter='" + filter + '\'' +
                ", order='" + order + '\'' +
                ", page='" + page + '\'' +
                ", size='" + size + '\'' +
                ", sort=" + sort +
                '}';
    }
}
