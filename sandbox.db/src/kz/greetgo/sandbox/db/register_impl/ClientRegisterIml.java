package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.sandbox.controller.model.ClientForm;
import kz.greetgo.sandbox.controller.model.ClientInfo;
import kz.greetgo.sandbox.controller.register.ClientRegister;

import java.util.List;

public class ClientRegisterIml implements ClientRegister{
    public List<ClientInfo> getClientInfoList(int limit, int page, String filter, String orderBy, int desc){
        return null;
    }

    public int getClientsSize(String filter){
        return 0;
    }

    public float remove(List<Integer> id){
        return 0f;
    }

    public ClientForm info(int id){
        return null;
    }

    public void add(ClientForm clientForm){

    }

    public boolean update(ClientForm clientForm){
        return false;
    }
}
