package kz.greetgo.sandbox.db.register_impl.migration;

import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;

public class MigrateOneCiaFileTest extends MigrateCommonTests {

  @Test
  public void prepareTmpTables() throws Exception {
    MigrateOneCiaFile m = new MigrateOneCiaFile();
    m.connection = connection;
    m.prepareTmpTables();
  }

  @Test
  public void uploadData() throws Exception {
    File inFile = new File("build/MigrateOneCiaFileTest/cia" + RND.plusInt(10_000_000) + ".xml");
    inFile.getParentFile().mkdirs();

    createCiaFile(inFile);

    MigrateOneCiaFile m = new MigrateOneCiaFile();
    m.connection = connection;
    m.inputFile = inFile;
    m.prepareTmpTables();
    m.uploadData();

    List<Map<String, Object>> recordList = toListMap("select * from " + m.tmpClientTable + " order by no");
    assertThat(recordList).hasSize(2);
    assertThat(recordList.get(0).get("surname")).isEqualTo("Иванов");
    assertThat(recordList.get(1).get("surname")).isEqualTo("Петров");
  }

}