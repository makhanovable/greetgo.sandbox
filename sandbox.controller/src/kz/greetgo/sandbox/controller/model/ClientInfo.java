package kz.greetgo.sandbox.controller.model;

import kz.greetgo.sandbox.controller.enums.Gender;

import java.sql.Date;


/**
 * Created by damze on 2/1/18.
 */
public class ClientInfo {
    private int id;
    private String surSame;
    private String name;
    private String patronymic;
    private Gender gender;
    private Date birthDate;
    private Charm charm;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSurSame() {
        return surSame;
    }

    public void setSurSame(String surSame) {
        this.surSame = surSame;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Charm getCharm() {
        return charm;
    }

    public void setCharm(Charm charm) {
        this.charm = charm;
    }
}
