package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.enums.AddressType;
import kz.greetgo.sandbox.controller.enums.GenderType;
import kz.greetgo.sandbox.controller.enums.PhoneNumberType;
import kz.greetgo.sandbox.controller.model.ClientAddress;
import kz.greetgo.sandbox.controller.model.ClientDetail;
import kz.greetgo.sandbox.controller.model.ClientPhoneNumber;
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

@SuppressWarnings({"deprecation", "WeakerAccess"})
public class ClientRegisterImplTest extends ParentTestNg {

  public BeanGetter<ClientRegister> clientRegister;
  public BeanGetter<ClientTestDao> clientTestDao;
  public BeanGetter<IdGenerator> idGenerator;

  @Test
  public void addClientTest() throws Exception {

    this.clientTestDao.get().clear();
    ClientDot c = this.insertClientDot();
    List<ClientPhoneNumberDot> numbers = new ArrayList<>();

    ClientPhoneNumberDot number1 = insertPhoneNumber(c.id, PhoneNumberType.WORK);

    ClientAddressDot actualAddress = insertAddress(c.id, AddressType.FACT);
    ClientAddressDot registerAddress = insertAddress(c.id, AddressType.REG);

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


  private void assertPhoneNumber(ClientPhoneNumber clientPhoneNumberDot, ClientPhoneNumberDot assertion) {
    assertThat(clientPhoneNumberDot.client).isEqualTo(assertion.client);
    assertThat(clientPhoneNumberDot.number).isEqualTo(assertion.number);
    assertThat(clientPhoneNumberDot.type).isEqualTo(assertion.type);
  }

  private void assertClientAddres(ClientAddress target, ClientAddressDot assertion) {
    assertThat(target.client).isEqualTo(assertion.client);
    assertThat(target.street).isEqualTo(assertion.street);
    assertThat(target.house).isEqualTo(assertion.house);
    assertThat(target.flat).isEqualTo(assertion.flat);
    assertThat(target.type).isEqualTo(assertion.type);
  }

  private ClientAddressDot insertAddress(String id, AddressType type) {
    ClientAddressDot address = new ClientAddressDot();
    address.street = RND.str(10);
    address.client = id;
    address.house = RND.str(10);
    address.type = type;
    this.clientTestDao.get().insertAddress(address);
    return address;
  }

  private ClientPhoneNumberDot insertPhoneNumber(String clientId, PhoneNumberType type) {
    ClientPhoneNumberDot number = new ClientPhoneNumberDot();
    number.client = clientId;
    number.type = type;
    number.number = RND.str(10);
    this.clientTestDao.get().insertPhone(number);
    return number;
  }

  private void assertClientDetail(ClientDetail target, ClientDot assertion) {
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

  private ClientDot insertClientDot() {
    ClientDot c = new ClientDot();
    c.id = idGenerator.get().newId();
    c.name = RND.str(10);
    c.surname = RND.str(10);
    c.patronymic = RND.str(10);
    c.charm = RND.str(10);
    c.gender = GenderType.MALE;
    c.birthDate = new Date();
    this.clientTestDao.get().insertClientDot(c);
    return c;
  }


}
