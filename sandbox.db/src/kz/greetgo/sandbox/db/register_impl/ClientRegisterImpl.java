package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.sandbox.controller.errors.InvalidParameter;
import kz.greetgo.sandbox.controller.model.*;
import kz.greetgo.sandbox.controller.register.ClientListReportRegister;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.controller.register.model.ClientListReportInstance;
import kz.greetgo.sandbox.controller.util.Util;
import kz.greetgo.sandbox.db.dao.CharmDao;
import kz.greetgo.sandbox.db.dao.ClientAddrDao;
import kz.greetgo.sandbox.db.dao.ClientDao;
import kz.greetgo.sandbox.db.dao.ClientPhoneDao;
import kz.greetgo.sandbox.db.register_impl.jdbc.client_list.GetClientCount;
import kz.greetgo.sandbox.db.register_impl.jdbc.client_list.GetClientList;
import kz.greetgo.sandbox.db.util.JdbcSandbox;

import java.io.OutputStream;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Bean
public class ClientRegisterImpl implements ClientRegister {

  public BeanGetter<ClientDao> clientDao;
  public BeanGetter<CharmDao> charmDao;
  public BeanGetter<ClientAddrDao> clientAddrDao;
  public BeanGetter<ClientPhoneDao> clientPhoneDao;
  public BeanGetter<JdbcSandbox> jdbc;
  public BeanGetter<ClientListReportRegister> clientListReportRegister;

  @Override
  public long getCount(ClientRecordRequest request) {
    if (request == null || request.nameFilter == null) throw new InvalidParameter();

    return jdbc.get().execute(new GetClientCount(request));
  }

  @Override
  public List<ClientRecord> getRecordList(ClientRecordRequest request) {
    if (request == null || request.nameFilter == null) throw new InvalidParameter();
    if (request.clientRecordCount < 0 || request.clientRecordCountToSkip < 0 || request.clientRecordCount == 0)
      throw new InvalidParameter();

    return jdbc.get().execute(new GetClientList(request));
  }

  @Override
  public void removeRecord(long id) {
    if (id < 0) throw new InvalidParameter();

    clientDao.get().deleteRowById(id);
  }

  @Override
  public ClientDetails getDetails(Long id) {
    if (id != null && id < 0) throw new InvalidParameter();

    ClientDetails resClientDetails = new ClientDetails();

    if (id == null) {
      resClientDetails.id = null;
      resClientDetails.surname = "";
      resClientDetails.name = "";
      resClientDetails.patronymic = "";
      resClientDetails.gender = Gender.EMPTY;
      resClientDetails.birthdate = "";
      resClientDetails.charmId = charmDao.get().selectFirstRowId();
      resClientDetails.charmList = charmDao.get().selectActualCharms();
      resClientDetails.registrationAddressInfo = new AddressInfo();
      resClientDetails.registrationAddressInfo.type = AddressType.REGISTRATION;
      resClientDetails.registrationAddressInfo.street = "";
      resClientDetails.registrationAddressInfo.house = "";
      resClientDetails.registrationAddressInfo.flat = "";
      resClientDetails.factualAddressInfo = new AddressInfo();
      resClientDetails.factualAddressInfo.street = "";
      resClientDetails.factualAddressInfo.type = AddressType.FACTUAL;
      resClientDetails.factualAddressInfo.house = "";
      resClientDetails.factualAddressInfo.flat = "";
      resClientDetails.phones = new ArrayList<>();
    } else {
      if (!clientDao.get().selectExistsRowById(id)) throw new InvalidParameter();

      resClientDetails = clientDao.get().selectRowById(id);
      resClientDetails.charmList = charmDao.get().selectActualCharms();
      resClientDetails.registrationAddressInfo =
        clientAddrDao.get().selectRowByClientAndType(id, AddressType.REGISTRATION.name());
      resClientDetails.factualAddressInfo =
        clientAddrDao.get().selectRowByClientAndType(id, AddressType.FACTUAL.name());
      resClientDetails.phones =
        clientPhoneDao.get().selectRowsByClient(id);
    }

    return resClientDetails;
  }

  @Override
  public void saveDetails(ClientDetailsToSave detailsToSave) {
    if (detailsToSave == null) throw new InvalidParameter();
    if (detailsToSave.id != null && !clientDao.get().selectExistsRowById(detailsToSave.id))
      throw new InvalidParameter();

    if (detailsToSave.id == null) {
      long id = clientDao.get().selectNextIdSeq();

      clientDao.get().insert(id, detailsToSave.surname, detailsToSave.name,
        detailsToSave.patronymic, detailsToSave.gender.name(), Date.valueOf(detailsToSave.birthdate),
        detailsToSave.charmId);
      clientAddrDao.get().insert(id, AddressType.REGISTRATION.name(), detailsToSave.registrationAddressInfo.street,
        detailsToSave.registrationAddressInfo.house, detailsToSave.registrationAddressInfo.flat);
      clientAddrDao.get().insert(id, AddressType.FACTUAL.name(), detailsToSave.factualAddressInfo.street,
        detailsToSave.factualAddressInfo.house, detailsToSave.factualAddressInfo.flat);

      for (Phone phone : detailsToSave.phones)
        clientPhoneDao.get().insert(id, phone.number, phone.type.name());
    } else {
      clientDao.get().update(detailsToSave.id, detailsToSave.surname, detailsToSave.name,
        detailsToSave.patronymic, detailsToSave.gender.name(), Date.valueOf(detailsToSave.birthdate),
        detailsToSave.charmId);
      clientAddrDao.get().update(detailsToSave.id, AddressType.REGISTRATION.name(), detailsToSave.registrationAddressInfo.street,
        detailsToSave.registrationAddressInfo.house, detailsToSave.registrationAddressInfo.flat);
      clientAddrDao.get().update(detailsToSave.id, AddressType.FACTUAL.name(), detailsToSave.factualAddressInfo.street,
        detailsToSave.factualAddressInfo.house, detailsToSave.factualAddressInfo.flat);

      for (Phone phone : detailsToSave.deletedPhones)
        clientPhoneDao.get().updateSetDisabled(detailsToSave.id, phone.number);

      for (Phone phone : detailsToSave.phones)
        clientPhoneDao.get().insert(detailsToSave.id, phone.number, phone.type.name());
    }
  }


  @Override
  public String prepareRecordListStream(String personId, ClientRecordRequest request, FileContentType fileContentType)
    throws Exception {
    return clientListReportRegister.get().save(personId, request, fileContentType);
  }

  @Override
  public void streamRecordList(String reportIdInstance, OutputStream outStream, RequestTunnel requestTunnel)
    throws Exception {
    ClientListReportInstance clientListReportInstance =
      clientListReportRegister.get().checkForValidity(reportIdInstance);

    FileContentType fileContentType = FileContentType.valueOf(clientListReportInstance.fileTypeName);

    String contentType, fileType;
    switch (fileContentType) {
      case PDF:
        contentType = "application/pdf";
        fileType = "pdf";
        break;
      case XLSX:
        contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        fileType = "xlsx";
        break;
      default:
        throw new InvalidParameter();
    }
    requestTunnel.setResponseContentType(contentType);
    requestTunnel.setResponseHeader("Content-Disposition", "attachment; filename=\"client_records_" +
      LocalDateTime.now().format(DateTimeFormatter.ofPattern(Util.reportDatePattern)) + "." + fileType + "\"");

    clientListReportRegister.get().generate(outStream, clientListReportInstance.personId,
      ClientRecordRequest.deserialize(clientListReportInstance.request), fileContentType);
  }
}
