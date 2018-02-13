package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.enums.AddressType;
import kz.greetgo.sandbox.controller.enums.GenderType;
import kz.greetgo.sandbox.controller.enums.PhoneNumberType;
import kz.greetgo.sandbox.controller.model.ClientAddress;
import kz.greetgo.sandbox.controller.model.ClientDetail;
import kz.greetgo.sandbox.controller.model.ClientPhoneNumber;
import kz.greetgo.sandbox.controller.model.ClientToSave;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.stand.model.ClientAddressDot;
import kz.greetgo.sandbox.db.stand.model.ClientDot;
import kz.greetgo.sandbox.db.stand.model.ClientPhoneNumberDot;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

@SuppressWarnings({"deprecation", "WeakerAccess", "ConstantConditions"})
public class ClientRegisterImplTest extends ParentTestNg {

  public BeanGetter<ClientRegister> clientRegister;
  public BeanGetter<ClientTestDao> clientTestDao;
  public BeanGetter<IdGenerator> idGenerator;

  @Test
  public void addClientTest() {
    this.clientTestDao.get().clear();
    ClientToSave client = rndClientToSave(null);

    //
    //
    this.clientRegister.get().add(client);
    //
    //

    ClientDetail clientDetail = this.clientTestDao.get().detail(client.id);
    this.assertClientDetail(clientDetail, new ClientDot(client));
    ClientAddress regAddress = this.clientTestDao.get().getAddres(client.id, AddressType.REG);
    ClientAddress actAddress = this.clientTestDao.get().getAddres(client.id, AddressType.FACT);
    this.assertClientAddres(actAddress, new ClientAddressDot(client.id, client.actualAddress));
    this.assertClientAddres(regAddress, new ClientAddressDot(client.id, client.registerAddress));

    List<ClientPhoneNumber> numberList = this.clientTestDao.get().getNumbersById(client.id);
    assertThat(numberList.isEmpty()).isFalse();
    assertThat(numberList.size()).isEqualTo(client.toSave.size());
    this.assertPhoneNumber(numberList.get(0), new ClientPhoneNumberDot(client.id, client.toSave.get(0)));

  }

  @Test
  public void getDetailTest() throws Exception {

    this.clientTestDao.get().clear();
    ClientDot c = this.rndClientDot();
    this.clientTestDao.get().insertClientDot(c);
    ClientPhoneNumberDot number1 = rndPhoneNumber(c.id, PhoneNumberType.WORK);
    this.clientTestDao.get().insertPhone(number1);
    ClientAddressDot actualAddress = rndAddress(c.id, AddressType.FACT);
    ClientAddressDot registerAddress = rndAddress(c.id, AddressType.REG);
    this.clientTestDao.get().insertAddress(actualAddress);
    this.clientTestDao.get().insertAddress(registerAddress);


    //
    //
    ClientDetail detail = this.clientRegister.get().detail(c.id);
    //
    //

    this.assertClientDetail(detail, c);
    assertThat(detail.phoneNumbers).isNotNull();
    assertThat(detail.actualAddress).isNotNull();
    assertThat(detail.registerAddress).isNotNull();
    assertThat(detail.phoneNumbers.size()).isEqualTo(1);
    this.assertClientAddres(detail.actualAddress, actualAddress);
    this.assertClientAddres(detail.registerAddress, registerAddress);
    this.assertPhoneNumber(detail.phoneNumbers.get(0), number1);

  }

  @SuppressWarnings("SameParameterValue")
  private ClientToSave rndClientToSave(String id) {
    ClientToSave client = new ClientToSave();
    client.id = id;
    client.name = RND.str(10);
    client.surname = RND.str(10);
    client.patronymic = RND.str(10);
    client.gender = GenderType.MALE;
    client.birthDate = RND.dateYears(1996, 2018);
    client.charm = RND.str(10);

    client.actualAddress = rndAddress(id, AddressType.FACT).toClientAddress();
    client.registerAddress = rndAddress(id, AddressType.REG).toClientAddress();

    client.toSave = new ArrayList<>();
    client.toSave.add(rndPhoneNumber(id, PhoneNumberType.WORK).toClientPhoneNumber());
//    client.toSave.add(rndPhoneNumber(id, PhoneNumberType.MOBILE).toClientPhoneNumber());
//    client.toSave.add(rndPhoneNumber(id, PhoneNumberType.HOME).toClientPhoneNumber());

    return client;
  }

  private void assertPhoneNumber(ClientPhoneNumber target, ClientPhoneNumberDot assertion) {
    if (target == null && assertion == null)
      return;
    assertThat(target).isNotNull();
    assertThat(target.client).isEqualTo(assertion.client);
    assertThat(target.number).isEqualTo(assertion.number);
    assertThat(target.type).isEqualTo(assertion.type);
  }

  private void assertClientAddres(ClientAddress target, ClientAddressDot assertion) {
    if (target == null && assertion == null)
      return;
    assertThat(target).isNotNull();
    assertThat(target.client).isEqualTo(assertion.client);
    assertThat(target.street).isEqualTo(assertion.street);
    assertThat(target.house).isEqualTo(assertion.house);
    assertThat(target.flat).isEqualTo(assertion.flat);
    assertThat(target.type).isEqualTo(assertion.type);
  }

  private ClientAddressDot rndAddress(String id, AddressType type) {
    ClientAddressDot address = new ClientAddressDot();
    address.client = id;
    address.type = type;

    address.street = RND.str(10);
    address.house = RND.str(10);
    return address;
  }

  private ClientPhoneNumberDot rndPhoneNumber(String clientId, PhoneNumberType type) {
    ClientPhoneNumberDot number = new ClientPhoneNumberDot();
    number.client = clientId;
    number.type = type;
    number.number = RND.str(10);

    return number;
  }

  private void assertClientDetail(ClientDetail target, ClientDot assertion) {
    if (target == null && assertion == null)
      return;
    assertThat(target).isNotNull();
    assertThat(target).isNotNull();
    assertThat(target.name).isEqualTo(assertion.name);
    assertThat(target.surname).isEqualTo(assertion.surname);
    assertThat(target.patronymic).isEqualTo(assertion.patronymic);
    assertThat(target.gender).isEqualTo(assertion.gender);

    assertThat(target.birthDate.getDay()).isEqualTo(assertion.birthDate.getDay());
    assertThat(target.birthDate.getMonth()).isEqualTo(assertion.birthDate.getMonth());
    assertThat(target.birthDate.getYear()).isEqualTo(assertion.birthDate.getYear());

    assertThat(target.charm).isEqualTo(assertion.charm);
    assertThat(target.id).isEqualTo(assertion.id);
  }

  private ClientDot rndClientDot() {
    ClientDot c = new ClientDot();
    c.id = idGenerator.get().newId();
    c.name = RND.str(10);
    c.surname = RND.str(10);
    c.patronymic = RND.str(10);
    c.charm = RND.str(10);
    c.gender = GenderType.MALE;
    c.birthDate = new Date();

    return c;
  }


}
