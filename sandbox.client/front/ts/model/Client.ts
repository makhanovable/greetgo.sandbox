import {Gender} from "./Gender";

export class Client {
  public id: number;
  public name: string;
  public surname: string;
  public patronymic: string;
  public gender: Gender;
  public birthDate: Date;
  public charm: number;
}