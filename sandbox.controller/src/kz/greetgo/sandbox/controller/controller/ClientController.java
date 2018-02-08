package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.sandbox.controller.model.ClientForm;
import kz.greetgo.sandbox.controller.model.ClientInfo;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.util.Controller;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Bean
@Mapping("/client")
public class ClientController implements Controller {

    public BeanGetter<ClientRegister> clientRegister;


    @Mapping("/ping")
    public void ping(RequestTunnel requestTunnel){


        requestTunnel.getResponseWriter().append("hello");
    }



    @ToJson
    @Mapping("/list")
    public List<ClientInfo> list(@Par("limit") int limit, @Par("page") int page, @Par("filter") String filter,
                                 @Par("orderBy") String orderBy, @Par("desc") int desc){

        return clientRegister.get().getClientInfoList(limit, page, filter, orderBy, desc);
    }

    @ToJson
    @Mapping("/amount")
    public int getAmount(@Par("filter") String filter){
        return this.clientRegister.get().getClientsSize(filter);
    }

    // FIXME: 2/8/18 Передавай лист с клиента, а не склеенный стринг. Название метода должно быть понятным.
    @ToJson
    @Mapping("/remove")
    public float lol(@Par("ids") String ids){
        StringTokenizer st = new StringTokenizer(ids, "|");
        List<Integer> list = new ArrayList<>();

        while(st.hasMoreTokens()) {
            int id = Integer.parseInt(st.nextToken());
            list.add(id);

        }
        return this.clientRegister.get().remove(list);
    }

    @ToJson
    @Mapping("/info")
    public ClientForm info(@Par("id") int id){
        return this.clientRegister.get().info(id);
    }

    @ToJson
    @Mapping("/add")
    public String add(@Par("client") @Json ClientForm clientForm){
        this.clientRegister.get().add(clientForm);
        return "ok";
    }

    @ToJson
    @Mapping("/update")
    public String update(@Par("client") @Json ClientForm clientForm){
        return this.clientRegister.get().update(clientForm) ? "ok" : "bad";
    }



}
