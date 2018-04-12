package kz.greetgo.sandbox.db.register_impl.jdbc;

import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.model.Client;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.UserInfo;
import kz.greetgo.sandbox.controller.report.ClientsListReportView;
import kz.greetgo.sandbox.controller.report.model.ClientListRow;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TestJdbc implements ConnectionCallback<Void> {

    private UserInfo userInfo;
    private ClientsListReportView view;
    private List<Object> params;
    private StringBuilder sql;
    private String filterStr;
    private Connection connection;


    public TestJdbc(UserInfo userInfo, ClientsListReportView clientsListReportView, String filterStr) {
        this.userInfo = userInfo;
        this.view = clientsListReportView;
        this.filterStr = "%" + filterStr + "%";
        this.params = new ArrayList<Object>();
        this.sql = new StringBuilder();
    }

    @Override
    public Void doInConnection(Connection connection) throws Exception {
        this.connection = connection;

        getClient();

        return  null;
    }

    private void getClient() throws  Exception{
        prepareSqlForClients();

        try(PreparedStatement ps = connection.prepareStatement(sql.toString())){
            {
                int index = 1;
                for(Object param : params) {
                    ps.setObject(index++, param);
                }
            }

            try(ResultSet rs = ps.executeQuery()) {

                view.start("Список клиентов");

                while(rs.next()) {
                    Client client = new Client();

                    client.id = rs.getInt("id");
                    client.surname = rs.getString("surname");
                    client.name = rs.getString("name");
                    client.patronymic = rs.getString("patronymic");
                    client.birth_date = rs.getDate("birth_date");
                    client.charm_id = rs.getInt("charm_id");

                    view.append(getClientListRow(client));
                }

                view.finish(this.userInfo.surname);
            }
        }
    }
    private void prepareSqlForClients() {
        sql.setLength(0);
        params.clear();

        sql.append("select * from clients ");
        sql.append("where name like ? ");
        params.add(filterStr);
        sql.append("or surname like ? ");
        params.add(filterStr);
        sql.append("or patronymic like ? ");
        params.add(filterStr);
    }

    private ClientListRow getClientListRow(Client client) throws Exception {
        ClientListRow clientRecord = new ClientListRow();

        clientRecord.fio = client.surname + " " + client.name + " " + client.patronymic;
        clientRecord.age = client.CountAge();
        clientRecord.totalCash = getCash(client.id, "total");
        clientRecord.minCash = getCash(client.id, "max");
        clientRecord.maxCash = getCash(client.id, "min");
        clientRecord.charm = getCharm(client.charm_id);

        return clientRecord;
    }
    private float getCash(int clientID, String type) throws Exception{
        prepareSqlForCash(type, clientID);

        try(PreparedStatement ps = this.connection.prepareStatement(sql.toString())){
            {
                int index = 1;
                for(Object param : params) {
                    ps.setObject(index++, param);
                }
            }

            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    return rs.getFloat(1);
                }
            }
        }

        return 0;
    }
    private void prepareSqlForCash(String type, int clientID) {
        sql.setLength(0);
        params.clear();

        if ("total".equals(type)) {
            sql.append("select SUM(money) from accounts ");
        } else
        if ("max".equals(type)) {
            sql.append("select MAX(money) from accounts ");
        } else
        if ("min".equals(type)) {
            sql.append("select MIN(money) from accounts ");
        }

        sql.append("where client_id = ?");
        params.add(clientID);
    }

    private String getCharm(int clientID) throws Exception{
        prepareSqlForCharm(clientID);

        try(PreparedStatement ps = this.connection.prepareStatement(sql.toString())){
            {
                int index = 1;
                for(Object param : params) {
                    ps.setObject(index++, param);
                }
            }

            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    return rs.getString(1);
                }
            }
        }

        return "";
    }
    private void prepareSqlForCharm(int charmID) {
        sql.setLength(0);
        params.clear();

        sql.append("select name from charms ");
        sql.append("where id = ?");
        params.add(charmID);
    }
}
