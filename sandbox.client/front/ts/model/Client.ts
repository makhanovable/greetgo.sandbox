import {Gender} from "./Gender";

export class Client {
  public id: number;
  public name: string;
  public surname: string;
  public patronymic: string;
  public gender: Gender;
  public birthDate: Date;
  public charmId: number;


  constructor(name: string, surname: string, patronymic: string, gender: Gender, birthDate: Date, charmId: number) {
    this.name = name;
    this.surname = surname;
    this.patronymic = patronymic;
    this.gender = gender;
    this.birthDate = birthDate;
    this.charmId = charmId;
  }
}