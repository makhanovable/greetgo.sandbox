package kz.greetgo.sandbox.db.stand.beans;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.HasAfterInject;
import kz.greetgo.sandbox.db.stand.model.ClientDot;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Bean
public class ClientStandDb implements HasAfterInject {
    public Map<String, ClientDot> clientStorage = new HashMap<>();

    @Override
    public void afterInject() {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            String[] split = new String[7];
            for (int j = 0; j < 7; j++) {
                split[j] = Integer.toString(random.nextInt(5000));
            }
            appendPerson(split);
        }
//        clientStorage = new HashMap<>();
//        try (BufferedReader br = new BufferedReader(
//                new InputStreamReader(getClass().getResourceAsStream("ClientStandDbInitData1.txt"), "UTF-8"))) {
//
//            int lineNo = 0;
//
//            while (true) {
//                String line = br.readLine();
//                if (line == null) break;
//                lineNo++;
//                String trimmedLine = line.trim();
//                if (trimmedLine.length() == 0) continue;
//                if (trimmedLine.startsWith("#")) continue;
//
//                String[] splitLine = line.split(";");
//                appendPerson(splitLine, line, lineNo);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @SuppressWarnings("unused")
    private void appendPerson(String[] splitLine) {
        ClientDot c = new ClientDot();
        c.id = splitLine[0].trim();
        c.name = splitLine[1].trim();
        c.charm = splitLine[2].trim();
        c.age = splitLine[3].trim();
        c.total = splitLine[4].trim();
        c.max = splitLine[5].trim();
        c.min = splitLine[6].trim();
        clientStorage.put(c.id, c);
    }

    public void insert(
            String surname, String name, String patronymic, String gender,
            String birth_date, String charm, String addrFactStreet,
            String addrFactHome, String addrFactFlat, String addrRegStreet,
            String addrRegHome, String addrRegFlat, String phoneHome, String phoneWork,
            String phoneMob1, String phoneMob2, String phoneMob3
    ) {
        Random random = new Random();
        String[] split = new String[7];
        split[0]= Integer.toString(random.nextInt(500000));
        split[1] = surname + " " + name+ " "+ patronymic;
        split[2] = charm;
        split[3]= birth_date;
        split[4] = Integer.toString(random.nextInt(500000));;
        split[5] = Integer.toString(random.nextInt(500000));;
        split[6] = Integer.toString(random.nextInt(500000));;
        appendPerson(split);

        //System.out.println(surname + name + patronymic + gender + birth_date + charm + addrFactStreet + addrFactHome + addrFactFlat + addrRegStreet + addrRegHome + addrRegFlat + phoneHome + phoneWork + phoneMob1 + phoneMob2 + phoneMob3);
//        int id = -1;
//        try (BufferedReader br = new BufferedReader(
//                new InputStreamReader(getClass().getResourceAsStream("ClientStandDbInitData1.txt"), "UTF-8"))) {
//
//            int lineNo = 0;
//
//            String tmp, line = null;
//            while (true) {
//                tmp = br.readLine();
//                if (tmp == null) break;
//                line = tmp;
//            }
//            String[] splitLine = line.split(";");
//            id = Integer.parseInt(splitLine[0]) + 1;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        BufferedWriter bw = null;
//        Random random = new Random();
//        FileWriter fw = null;
//        try {
//            String data = "\n" + id + "; " + surname + " " + name + " " + patronymic + "; "
//                    + charm + "; "
//                    + birth_date + "; "
//                    + random.nextInt(100) + "; " + random.nextInt(500) + "; " + random.nextInt(50);
//            File file = new File("sandbox.stand\\db\\src\\kz\\greetgo\\sandbox\\db\\stand\\beans\\ClientStandDbInitData1.txt");
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//            fw = new FileWriter(file.getAbsoluteFile(), true);
//            bw = new BufferedWriter(fw);
//            bw.write(data);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (bw != null)
//                    bw.close();
//                if (fw != null)
//                    fw.close();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
    }
}
