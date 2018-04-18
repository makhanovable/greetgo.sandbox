package kz.greetgo.sandbox.db.register_impl.jdbc;

import kz.greetgo.db.ConnectionCallback;
import kz.greetgo.sandbox.controller.model.UserInfo;
import kz.greetgo.sandbox.controller.report.ClientsListReportView;
import kz.greetgo.sandbox.controller.report.model.ClientListRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class TestJdbc implements ConnectionCallback<Void> {

    private String username;
    private ClientsListReportView view;
    private List<Object> params;
    private StringBuilder sql;
    private String filterStr;
    private String sortBy;
    private String sortOrder;
    private Connection connection;


    public TestJdbc(String username, ClientsListReportView clientsListReportView, String filterStr, String sortBy, String sortOrder) {
        this.username = username;
        this.view = clientsListReportView;
        this.filterStr = "%" + filterStr + "%";
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
        this.params = new ArrayList<Object>();
        this.sql = new StringBuilder();
    }

    @Override
    public Void doInConnection(Connection connection) throws Exception {
        this.connection = connection;

        getClientListRow();

        return  null;
    }

    private void getClientListRow() throws  Exception{
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

                int index = 1;
                while(rs.next()) {
                    ClientListRow clientListRow = new ClientListRow();

                    clientListRow.no = index++;
                    clientListRow.fio = rs.getString("surname") + " " +
                            rs.getString("name") + " " + rs.getString("patronymic");
                    clientListRow.age = CountAge(rs.getDate("birth_date"));
                    clientListRow.charm = getCharm(rs.getInt("charm_id"));
                    clientListRow.totalCash = rs.getFloat("total_cash");
                    clientListRow.maxCash = rs.getFloat("max_cash");
                    clientListRow.minCash = rs.getFloat("min_cash");

                    view.append(clientListRow);
                }

                view.finish(this.username);
            }
        }
    }
    private void prepareSqlForClients() {
        sql.setLength(0);
        params.clear();

        sql.append("select clients.*, ");
        sql.append("coalesce(SUM(accounts.money), 0) as total_cash, ");
        sql.append("coalesce(MAX(accounts.money), 0) as max_cash, ");
        sql.append("coalesce(MIN(accounts.money), 0) as min_cash ");
        sql.append("from clients ");
        sql.append("left join accounts on clients.id = accounts.client_id ");
        sql.append("where name like ? ");
        params.add(filterStr);
        sql.append("or surname like ? ");
        params.add(filterStr);
        sql.append("or patronymic like ? ");
        params.add(filterStr);
        sql.append("group by clients.id ");

        if ("fio".equals(sortBy)) {
            sql.append("order by surname, name, patronymic ");
        } else
        if ("age".equals(sortBy)) {
            sql.append("order by birth_date ");
        } else
        if ("totalCash".equals(sortBy)) {
            sql.append("order by total_cash ");
        } else
        if ("maxCash".equals(sortBy)) {
            sql.append("order by max_cash ");
        } else
        if ("minCash".equals(sortBy)) {
            sql.append("order by min_cash ");
        }

        if ("up".equals(sortOrder)) {
            if ("age".equals(sortBy)){
                sql.append("DESC");
            } else {
                sql.append("ASC");
            }
        } else
        if ("down".equals(sortOrder)) {
            if ("age".equals(sortBy)){
                sql.append("ASC");
            } else {
                sql.append("DESC");
            }
        }
    }
    private int CountAge(Date birth_date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        Date date = new Date();
        long diffInMillies = Math.abs(date.getTime() - birth_date.getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        int years = (int) (diff / 365);

        return years;
    }

    private String getCharm(int charmID) throws Exception{
        prepareSqlForCharm(charmID);

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
