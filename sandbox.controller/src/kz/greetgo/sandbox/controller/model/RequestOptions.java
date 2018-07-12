package kz.greetgo.sandbox.controller.model;

public class RequestOptions {
    public String filter, order, page, size;
    public SortBy sort;

    @Override
    public String toString() {
        return "RequestOptions{" +
                "filter='" + filter + '\'' +
                ", order='" + order + '\'' +
                ", page='" + page + '\'' +
                ", size='" + size + '\'' +
                ", sort=" + sort +
                '}';
    }
}
