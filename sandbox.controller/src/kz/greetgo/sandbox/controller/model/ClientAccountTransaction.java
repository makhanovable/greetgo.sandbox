package kz.greetgo.sandbox.controller.model;

public class ClientAccountTransaction {
    public int id;
    public int account;
    public float money; // >0 - зачисление на счёт, <0 - списание со счёта
    public String finished_at; // TODO change variable type (TimeStamp)
    public int type;
}
