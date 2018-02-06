package kz.greetgo.sandbox.controller.register.model;

import kz.greetgo.sandbox.controller.model.ClientRecordRequest;

public class ClientListReportInstance {
  public String personId;
  public ClientRecordRequest request;
  public String fileTypeName;

  @SuppressWarnings("unused")
  public void setRequestBytes(byte[] data) {
    request = ClientRecordRequest.deserialize(data);
  }

  @SuppressWarnings("unused")
  public byte[] getRequestBytes() {
    return ClientRecordRequest.serialize(request);
  }
}
