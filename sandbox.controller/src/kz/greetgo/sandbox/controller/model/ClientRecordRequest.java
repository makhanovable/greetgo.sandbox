package kz.greetgo.sandbox.controller.model;

import java.io.*;

public class ClientRecordRequest implements Serializable {
  public long clientRecordCountToSkip;
  public long clientRecordCount;
  public ColumnSortType columnSortType;
  public boolean sortAscend;
  public String nameFilter;

  public static byte[] serialize(ClientRecordRequest request) throws Exception {
    ByteArrayOutputStream bOut = new ByteArrayOutputStream();

    ObjectOutputStream oOut = new ObjectOutputStream(bOut);
    oOut.writeObject(request);

    return bOut.toByteArray();
  }

  public static ClientRecordRequest deserialize(byte[] serial) throws Exception {
    ByteArrayInputStream bIn = new ByteArrayInputStream(serial);
    ObjectInputStream oIn = new ObjectInputStream(bIn);

    return (ClientRecordRequest) oIn.readObject();
  }
}
