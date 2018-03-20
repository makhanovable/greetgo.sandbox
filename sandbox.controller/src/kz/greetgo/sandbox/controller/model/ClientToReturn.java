package kz.greetgo.sandbox.controller.model;

import java.util.ArrayList;
import java.util.List;

public class ClientToReturn {
    public int pageCount;
    public List<PrintedClientInfo> clientInfos;

    public ClientToReturn() {
        clientInfos = new ArrayList<PrintedClientInfo>();
    }

}
