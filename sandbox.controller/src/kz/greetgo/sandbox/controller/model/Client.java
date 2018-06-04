package kz.greetgo.sandbox.controller.model;

public class Client {
    public Client(int id, String surname, String name,
                  String patronymic, Gender gender, String birth_date, int charm) {
        this.id = id;
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
        this.gender = gender;
        this.birth_date = birth_date;
        this.charm = charm;
    }

    public int id;
    public String surname, name, patronymic;
    public Gender gender;
    public String birth_date; // TODO change variable type (Date)
    public int charm;
}
