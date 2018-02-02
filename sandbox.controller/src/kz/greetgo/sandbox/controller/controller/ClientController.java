package kz.greetgo.sandbox.controller.controller;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.security.NoSecurity;
import kz.greetgo.sandbox.controller.util.Controller;

import java.io.OutputStream;
import java.util.List;

import static kz.greetgo.mvc.core.RequestMethod.*;

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
  @Mapping("/list/instance")
  public String getRecordListToken(@ParSession("personId") String personId,
                                   @Par("clientRecordRequest") @Json ClientRecordRequest request,
                                   @Par("fileContentType") @Json FileContentType fileContentType) throws Exception {
    return clientRegister.get().prepareRecordListStream(personId, request, fileContentType);
  }

  @NoSecurity
  @MethodFilter(GET)
  @Mapping("/list/report")
  public void streamRecordList(@Par("instance") @Json String reportIdInstance,
                               RequestTunnel requestTunnel) throws Exception {
    try (OutputStream outStream = requestTunnel.getResponseOutputStream()) {
      clientRegister.get().streamRecordList(reportIdInstance, outStream, requestTunnel);
      requestTunnel.flushBuffer();
    }
  }
}
