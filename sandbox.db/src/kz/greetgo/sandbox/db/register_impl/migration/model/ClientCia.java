package kz.greetgo.sandbox.db.register_impl.migration.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClientCia {

  public String cia_id;
  public String id;
  public String name;
  public String surname;
  public String patronymic;
  public Date birth;
  public String gender;
  public String charm;

  public AddressCia reg;
  public AddressCia fact;

  public List<PhoneCia> numbers = new ArrayList<>();

  public ClientCia(String id) {
    this.id = id;
  }
}
