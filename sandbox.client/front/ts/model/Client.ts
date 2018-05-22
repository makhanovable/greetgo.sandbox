import {Gender} from "./Gender";

export class Client {
  public id: number;
  public name: string;
  public surname: string;
  public patronymic: string;
  public gender: Gender;
  public birthDate: Date;
  public charmId: number;

  public isActive: boolean = true;

  constructor(id: number, name: string, surname: string, patronymic: string, gender: Gender, birthDate: Date, charmId: number) {
    this.id = id;
    this.name = name;
    this.surname = surname;
    this.patronymic = patronymic;
    this.gender = gender;
    this.birthDate = birthDate;
    this.charmId = charmId;
  }
}