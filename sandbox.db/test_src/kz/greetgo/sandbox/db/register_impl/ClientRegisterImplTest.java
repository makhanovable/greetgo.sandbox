package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.test.dao.ClientTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import org.testng.annotations.Test;

public class ClientRegisterImplTest extends ParentTestNg {

  public BeanGetter<ClientRegister> clientRegister;
  public BeanGetter<ClientTestDao> clientTestDao;

  @Test
  public void getInfoListSizeTest() throws Exception {

//    ClientDot c = new ClientDot();
//    c.id = RND.str(32);
//    c.name = RND.str(10);
//    c.surname = RND.str(10);
//    c.patronymic = RND.str(10);
//    c.charmId = RND.str(10);
//    c.gender = GenderType.MALE;
//    c.birthDate = new Date();
//    this.clientTestDao.get().insertClientDot(c);
//
//    //
//    //
//    String filter = null;
//    int count = this.clientRegister.get().getClientsSize(filter);
//    //
//    //
//
//    assertThat(count).isEqualTo(1);
  }


}
