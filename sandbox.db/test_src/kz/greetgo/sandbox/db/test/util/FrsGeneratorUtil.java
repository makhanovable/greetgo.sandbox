package kz.greetgo.sandbox.db.test.util;

import kz.greetgo.sandbox.controller.model.AddrType;
import kz.greetgo.sandbox.db.migration.model.*;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.List;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class FrsGeneratorUtil {

    private static File file;

    public static String generateFrsFile(Account account, List<Transaction> transactions) throws Exception {
        file = new File("sandbox.db/test_src/kz/greetgo/sandbox/db/register_impl/migration/data/frs.txt");
        if (file.exists()) {
            file.delete();
        }
        file.getParentFile().mkdirs();
        file.createNewFile();
        StringBuilder content = new StringBuilder(generateFrsStr(account));

        for (Transaction transaction : transactions) content.append(generateFrsStr(transaction));

        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(content.toString());
        fileWriter.flush();
        fileWriter.close();
        return file.getPath();
    }

    public static void finish() {
        file.delete();
    }

    private static String generateFrsStr(Account account) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (account.account_number!= null) sb.append("\"account_number\":\"").append(account.account_number).append("\",");
        if (account.registered_at!= null) sb.append("\"registered_at\":\"").append(account.registered_at).append("\",");
        sb.append("\"type\":\"").append("new_account").append("\",");
        if (account.client_id!= null) sb.append("\"client_id\":\"").append(account.client_id).append("\"");
        sb.append("}\n");
        return sb.toString();
    }

    private static String generateFrsStr(Transaction transaction) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (transaction.transaction_type!= null) sb.append("\"transaction_type\":\"").append(transaction.transaction_type).append("\",");
        if (transaction.account_number!= null) sb.append("\"account_number\":\"").append(transaction.account_number).append("\",");
        if (transaction.money!= null) sb.append("\"money\":\"").append(transaction.money).append("\",");
        if (transaction.finished_at!= null) sb.append("\"finished_at\":\"").append(transaction.finished_at).append("\",");
        sb.append("\"type\":\"").append("transaction").append("\"");
        sb.append("}\n");
        return sb.toString();
    }

}
