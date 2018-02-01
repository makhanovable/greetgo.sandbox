package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.Json;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.MethodFilter;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.annotations.ParSession;
import kz.greetgo.mvc.annotations.ToJson;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.sandbox.controller.model.ClientDetails;
import kz.greetgo.sandbox.controller.model.ClientDetailsToSave;
import kz.greetgo.sandbox.controller.model.ClientRecord;
import kz.greetgo.sandbox.controller.model.ClientRecordRequest;
import kz.greetgo.sandbox.controller.model.FileContentType;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.security.NoSecurity;
import kz.greetgo.sandbox.controller.util.Controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import static kz.greetgo.mvc.core.RequestMethod.DELETE;
import static kz.greetgo.mvc.core.RequestMethod.GET;
import static kz.greetgo.mvc.core.RequestMethod.POST;

@Bean
@Mapping("/client")
public class ClientController implements Controller {

  public BeanGetter<ClientRegister> clientRegister;

  @ToJson
  @MethodFilter(GET)
  @Mapping("/count")
  public long getCount(@Par("clientRecordRequest") @Json ClientRecordRequest request) {
    return clientRegister.get().getCount(request);
  }

  @ToJson
  @MethodFilter(GET)
  @Mapping("/list")
  public List<ClientRecord> getRecordList(@Par("clientRecordRequest") @Json ClientRecordRequest request) {
    return clientRegister.get().getRecordList(request);
  }

  @MethodFilter(DELETE)
  @Mapping("/remove")
  public void removeRecord(@Par("clientRecordId") long id) {
    clientRegister.get().removeRecord(id);
  }

  @MethodFilter(GET)
  @ToJson
  @Mapping("/details")
  public ClientDetails getDetails(@Par("clientRecordId") Long id) {
    return clientRegister.get().getDetails(id);
  }

  @MethodFilter(POST)
  @Mapping("/save")
  public void saveDetails(@Par("clientDetailsToSave") @Json ClientDetailsToSave detailsToSave) {
    clientRegister.get().saveDetails(detailsToSave);
  }

  @MethodFilter(GET)
  @ToJson
  @Mapping("/list/token")
  //TODO здесь пепедать всю информацию и её сохранить report_instance_id
  public String getRecordListToken(@ParSession("personId") String personId,
                                   @Par("clientRecordRequest") @Json ClientRecordRequest request,
                                   @Par("fileContentType") @Json FileContentType fileContentType) {


    return clientRegister.get().prepareRecordListStream(personId);
  }

  @NoSecurity
  @MethodFilter(GET)
  @Mapping("/list/report")
  public void streamRecordList(@Par("clientRecordRequest") @Json ClientRecordRequest request,
                               @Par("fileContentType") @Json FileContentType fileContentType,
                               @Par("token") @Json String token,
                               RequestTunnel requestTunnel) throws Exception {
    try (OutputStream outStream = requestTunnel.getResponseOutputStream()) {
      clientRegister.get().streamRecordList(token, outStream, request, fileContentType, requestTunnel);
      requestTunnel.flushBuffer();
    }
  }

  private static final String ENG = "abcdefghijklmnopqrstuvwxyz";
  private static final String DEG = "0123456789";
  private static final char[] ALL = (ENG + ENG.toUpperCase() + DEG).toCharArray();

  private static final Random rnd = new SecureRandom();

  public static void main1(String[] args) {
    StringBuilder sb = new StringBuilder(27);
    int length = ALL.length;
    for (int i = 0; i < 27; i++) {
      sb.append(ALL[rnd.nextInt(length)]);
    }

    System.out.println(sb.toString());
  }

  public static void main(String[] args) throws Exception {

    ClientRecordRequest r = new ClientRecordRequest();
    r.nameFilter = "Привет мир";

    ByteArrayOutputStream bOut = new ByteArrayOutputStream();

    ObjectOutputStream oOut = new ObjectOutputStream(bOut);
    oOut.writeObject(r);

    byte[] bytes = bOut.toByteArray();

    System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(bytes));

    ByteArrayInputStream bIn = new ByteArrayInputStream(bytes);

    ObjectInputStream oIn = new ObjectInputStream(bIn);

    Object r2 = oIn.readObject();

    System.out.println(r2);
  }
}
