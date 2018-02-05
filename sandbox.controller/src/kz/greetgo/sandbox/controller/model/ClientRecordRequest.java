package kz.greetgo.sandbox.controller.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ClientRecordRequest implements Serializable {
  public long clientRecordCountToSkip;
  public long clientRecordCount;
  public ColumnSortType columnSortType;
  public boolean sortAscend;
  public String nameFilter;

  public static byte[] serialize(ClientRecordRequest request) {
    if (request == null) return null;

    try {
      ByteArrayOutputStream bOut = new ByteArrayOutputStream();

      ObjectOutputStream oOut = new ObjectOutputStream(bOut);
      oOut.writeObject(request);

      return bOut.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  public static ClientRecordRequest deserialize(byte[] serial) {
    try {
      ByteArrayInputStream bIn = new ByteArrayInputStream(serial);
      ObjectInputStream oIn = new ObjectInputStream(bIn);

      return (ClientRecordRequest) oIn.readObject();
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
