package kz.greetgo.sandbox.db.migration.core;

import kz.greetgo.sandbox.db.migration.model.ClientXMLRecord;

import java.text.SimpleDateFormat;

public class ToXMLParser {

    public String parseToXML(ClientXMLRecord clientXMLRecord) {
        StringBuilder sb = new StringBuilder();
        sb.append("<client id=\"").append(clientXMLRecord.id).append("\">\n");
        if (clientXMLRecord.surname != null) sb.append("<surname value=\"").append(clientXMLRecord.surname).append("\"").append("/>\n");
        if (clientXMLRecord.name != null) sb.append("<name value=\"").append(clientXMLRecord.name).append("\"").append("/>\n");
        if (clientXMLRecord.patronymic != null) sb.append("<patronymic value=\"").append(clientXMLRecord.patronymic).append("\"").append("/>\n");
        if (clientXMLRecord.gender != null) sb.append("<gender value=\"").append(clientXMLRecord.gender).append("\"").append("/>\n");
        if (clientXMLRecord.charm != null) sb.append("<charm value=\"").append(clientXMLRecord.charm).append("\"").append("/>\n");
        if (clientXMLRecord.birthDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");
            sb.append("<birth value=\"").append(sdf.format(clientXMLRecord.birthDate)).append("\"").append("/>\n");
        }

        sb.append("<address>\n");
        if (clientXMLRecord.fStreet != null) {
            sb.append("  <fact");
            sb.append(" street=\"").append(clientXMLRecord.fStreet).append("\"");
            sb.append(" house=\"").append(clientXMLRecord.fHouse).append("\"");
            sb.append(" flat=\"").append(clientXMLRecord.fFlat).append("\"");
            sb.append("/>\n");
        }
        if (clientXMLRecord.rStreet != null) {
            sb.append("  <register");
            sb.append(" street=\"").append(clientXMLRecord.rStreet).append("\"");
            sb.append(" house=\"").append(clientXMLRecord.rHouse).append("\"");
            sb.append(" flat=\"").append(clientXMLRecord.rFlat).append("\"");
            sb.append("/>\n");
        }
        sb.append("</address>\n");

        for (String number : clientXMLRecord.homePhones) {
            sb.append("<homePhone>").append(number).append("</homePhone>\n");
        }
        for (String number : clientXMLRecord.workPhones) {
            sb.append("<workPhone>").append(number).append("</workPhone>\n");
        }
        for (String number : clientXMLRecord.mobilePhones) {
            sb.append("<mobilePhone>").append(number).append("</mobilePhone>\n");
        }

        sb.append("</client>\n");

        return sb.toString();
    }
}
