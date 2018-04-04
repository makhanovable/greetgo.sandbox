package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.sandbox.controller.model.Charm;
import kz.greetgo.sandbox.controller.register.ClientRegister;
import kz.greetgo.sandbox.db.test.dao.CharmTestDao;
import kz.greetgo.sandbox.db.test.util.ParentTestNg;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;
import java.util.List;
import static org.fest.assertions.api.Assertions.assertThat;

public class ClientRegisterImplTest extends ParentTestNg {

    public BeanGetter<ClientRegister> clientRegister;
    public BeanGetter<CharmTestDao> charmTestDao;

    @Test
    public void testAddNewClient() throws Exception {
    }

    @Test
    public void testUpdateClient() throws Exception {
    }

    @Test
    public void testRemoveClient() throws Exception {
    }

    @Test
    public void testGetEditableClientInfo() throws Exception {
    }

    @Test
    public void testGetFilteredClientsInfo() throws Exception {
    }

    @Test
    public void testGetCharms() throws Exception {

        Charm charm = new Charm();
        charm.energy = 0.43f;
        charm.desc = RND.str(100);
        charm.name = RND.str(100);
        charm.id = RND.str(25);
        charmTestDao.get().insert(charm);

        //
        //
        List<Charm> charms = clientRegister.get().getCharms();
        //
        //

        assertThat(charms).isNotNull();
        assertThat(charms).hasSize(1);

        assertThat(charms.get(0)).isNotNull();
        assertThat(charms.get(0).desc).isEqualTo(charm.desc);
        assertThat(charms.get(0).energy).isEqualTo(charm.energy);
        assertThat(charms.get(0).name).isEqualTo(charm.name);
        assertThat(charms.get(0).id).isEqualTo(charm.id);
    }

}