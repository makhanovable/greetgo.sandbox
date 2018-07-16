package kz.greetgo.sandbox.db.test.util;

import kz.greetgo.sandbox.controller.model.AddrType;
import kz.greetgo.sandbox.db.migration.model.Address;
import kz.greetgo.sandbox.db.migration.model.Client;
import kz.greetgo.sandbox.db.migration.model.Phone;
import org.apache.log4j.Logger;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.List;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class CiaGeneratorUtil {

    private static File file;
    private static Logger logger = Logger.getLogger(CiaGeneratorUtil.class);

    public static String generateXmlFile(Client client, List<Address> addrs, List<Phone> phones) throws Exception {
        file = new File("sandbox.db/test_src/kz/greetgo/sandbox/db/register_impl/migration/data/cia.xml");
        if (file.exists()) {
            file.delete();
        }
        file.getParentFile().mkdirs();
        file.createNewFile();
        String content = generateXmlStr(client, addrs, phones);

        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(content);
        fileWriter.flush();
        fileWriter.close();
        return file.getPath();
    }

    public static void finish() {
        file.delete();
    }

    private static String generateXmlStr(Client client, List<Address> addrs, List<Phone> phones) {
        StringBuilder sb = new StringBuilder();
        sb.append("<cia>\n");
        sb.append("<client id=\"").append(client.cia_id).append("\">\n");

        if (client.surname != null) sb.append("<surname value=\"").append(client.surname).append("\"/>\n");
        if (client.name != null) sb.append("<name value=\"").append(client.name).append("\"/>\n");
        if (client.patronymic != null) sb.append("<patronymic value=\"").append(client.patronymic).append("\"/>\n");
        if (client.charm != null) sb.append("<charm value=\"").append(client.charm).append("\"/>\n");
        if (client.gender != null) sb.append("<gender value=\"").append(client.gender).append("\"/>\n");
        if (client.birth != null) {
            String birth = null;
            try{
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                birth = dateFormat.format(client.birth);
            } catch (Exception e) {// empty
                logger.error(e);
            }
            sb.append("<birth value=\"").append(birth).append("\"/>\n");
        }

        if (addrs != null && !addrs.isEmpty()) {
            sb.append("<address>\n");
            for (Address addr : addrs) {
                String tag = null;
                if (addr.type == AddrType.FACT) tag = "fact";
                else if (addr.type == AddrType.REG) tag = "register";
                sb.append("<").append(tag).append(" street=\"").append(addr.street).append("\"").append(" house=\"").append(addr.house).append("\"").append(" flat=\"").append(addr.flat).append("\"/>\n");
            }
            sb.append("</address>\n");
        }

        if (phones != null && !phones.isEmpty()) {
            for (Phone phone : phones) {
                String tag = null;
                switch (phone.type) {
                    case WORK:
                        tag = "workPhone";
                        break;
                    case HOME:
                        tag = "homePhone";
                        break;
                    case MOBILE:
                        tag = "mobilePhone";
                        break;
                }
                sb.append("<").append(tag).append(">").append(phone.phone).append("</").append(tag).append(">\n");
            }
        }

        sb.append("</client>\n");
        sb.append("</cia>\n");

        return sb.toString();
    }

}
