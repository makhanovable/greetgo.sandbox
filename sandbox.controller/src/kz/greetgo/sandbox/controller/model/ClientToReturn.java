package kz.greetgo.sandbox.controller.model;

import java.util.ArrayList;
import java.util.List;

public class ClientToReturn {
    public int pageCount;
    public List<ClientRecord> clientInfos;

    public ClientToReturn() {
        clientInfos = new ArrayList<ClientRecord>();
    }

}
