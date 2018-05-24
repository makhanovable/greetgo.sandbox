package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.register.charm.CharmRegister;
import kz.greetgo.sandbox.db.test.dao.CharmTestDao;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class CharmRegisterImplTest {

  public BeanGetter<CharmRegister> charmRegister;

  public BeanGetter<CharmTestDao> charmTestDao;

  @Test
  public void get_all_charms() throws Exception {
    throw new Exception();
  }

  @Test
  public void get_negativeId() throws Exception {
    int negativeId = -1 * RND.plusInt(10);

    //
    //
    charmRegister.get().getCharm(negativeId);
    //
    //
  }
}