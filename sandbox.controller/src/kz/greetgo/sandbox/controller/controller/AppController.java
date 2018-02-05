package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.ToJson;
import kz.greetgo.sandbox.controller.model.ClientInfo;
import kz.greetgo.sandbox.controller.register.AuthRegister;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.util.Controller;

import java.util.List;

@Bean
@Mapping("/client")
public class AppController implements Controller {

    public BeanGetter<ClientRegister> clientRegister;

    @ToJson
    @Mapping("/ping")
    public String ping(){
        return "pong";
    }

    @ToJson
    @Mapping("/get")
    public List<ClientInfo> get(){
        return clientRegister.get().getClients(0,0,"","",true);
    }


}
