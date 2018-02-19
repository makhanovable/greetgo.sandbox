package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.CharmRecord;
import kz.greetgo.sandbox.controller.register.CharmRegister;
import kz.greetgo.sandbox.db.stand.model.CharmDot;
import kz.greetgo.sandbox.db.test.dao.CharmTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

@SuppressWarnings("WeakerAccess")
public class CharmRegisterImplTest extends ParentTestNg {

  public BeanGetter<CharmRegister> charmRegister;
  public BeanGetter<CharmTestDao> charmTestDao;
  public BeanGetter<IdGenerator> idGenerator;

  @Test
  void insertTest() {
    this.charmTestDao.get().clear();

    CharmDot cd = new CharmDot();
    cd.id = idGenerator.get().newId();
    cd.energy = (float) RND.plusDouble(10D, 10);
    cd.description = RND.str(10);
    cd.name = RND.str(10);

    this.charmTestDao.get().insertClientDot(cd);

    //
    //
    List<CharmRecord> charmlist = this.charmRegister.get().getAll();
    //
    //

    assertThat(charmlist.size()).isEqualTo(1);
    assertThat(charmlist.get(0).name.equals(cd.name) && charmlist.get(0).id.equals(cd.id)).isTrue();
  }
}
