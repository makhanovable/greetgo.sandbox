package kz.greetgo.sandbox.db.migration.core;

import kz.greetgo.sandbox.db.migration.model.ClientXMLRecord;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ReadXMLFile {
    private List<ClientXMLRecord> clientXMLRecords;

    public List<ClientXMLRecord> loadData() {
        clientXMLRecords = new ArrayList<>();

        try {
            File fXmlFile = new File("/Users/sanzharburumbay/Documents/Greetgo_Internship/greetgo.sandbox/build/out_files/from_cia_2018-05-15-120656-1-300.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("client");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                ClientXMLRecord clientXMLRecord = new ClientXMLRecord();

                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    clientXMLRecord.id = eElement.getAttribute("id");

                    if (eElement.getElementsByTagName("name").item(0) != null) {
                        clientXMLRecord.name = eElement.getElementsByTagName("name").item(0).getAttributes().item(0).getNodeValue();
                    }
                    if (eElement.getElementsByTagName("surname").item(0) != null) {
                        clientXMLRecord.surname = eElement.getElementsByTagName("surname").item(0).getAttributes().item(0).getNodeValue();
                    }
                    if (eElement.getElementsByTagName("patronymic").item(0) != null) {
                        clientXMLRecord.patronymic = eElement.getElementsByTagName("patronymic").item(0).getAttributes().item(0).getNodeValue();
                    }
                    if (eElement.getElementsByTagName("gender").item(0) != null) {
                        clientXMLRecord.gender = eElement.getElementsByTagName("gender").item(0).getAttributes().item(0).getNodeValue();
                    }
                    if (eElement.getElementsByTagName("charm").item(0) != null) {
                        clientXMLRecord.charm = eElement.getElementsByTagName("charm").item(0).getAttributes().item(0).getNodeValue();
                    }

                    if (eElement.getElementsByTagName("birth").item(0) != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        clientXMLRecord.birthDate = new java.sql.Date(sdf.parse(eElement.getElementsByTagName("birth").item(0).getAttributes().item(0).getNodeValue()).getTime());
                    }

                    NodeList childNodes = eElement.getChildNodes();
                    for (int i = 0; i < childNodes.getLength(); i++) {
                        Node item = childNodes.item(i);

                        if ("address".equals(item.getNodeName())) {
                            for (int j = 0; j < item.getChildNodes().getLength(); j++) {
                                Node address = item.getChildNodes().item(j);
                                if ("fact".equals(address.getNodeName())) {
                                    clientXMLRecord.fStreet = address.getAttributes().item(2).getNodeValue();
                                    clientXMLRecord.fHouse = address.getAttributes().item(1).getNodeValue();
                                    clientXMLRecord.fFlat = address.getAttributes().item(0).getNodeValue();
                                }
                                if ("register".equals(address.getNodeName())) {
                                    clientXMLRecord.rStreet = address.getAttributes().item(2).getNodeValue();
                                    clientXMLRecord.rHouse = address.getAttributes().item(1).getNodeValue();
                                    clientXMLRecord.rFlat = address.getAttributes().item(0).getNodeValue();
                                }
                            }
                        }

                        if ("mobilePhone".equals(item.getNodeName())) {
                            clientXMLRecord.mobilePhones.add(item.getTextContent());
                        }
                        if ("homePhone".equals(item.getNodeName())) {
                            clientXMLRecord.homePhones.add(item.getTextContent());
                        }
                        if ("workPhone".equals(item.getNodeName())) {
                            clientXMLRecord.workPhones.add(item.getTextContent());
                        }
                    }

                    clientXMLRecords.add(clientXMLRecord);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return clientXMLRecords;
    }
}
