import {Address} from "./Address";
import {Phone} from "./Phone";
import {Gender} from "./Gender";

export class ClientInfo {
  public id: number/*int*/;
  public name: string;
  public surname: string;
  public patronymic: string;
  public gender: Gender;
  public birthDate: number/*long*/;
  public charmId: number/*int*/;
  public factAddress: Address;
  public regAddress: Address;
  public phones: Phone[];
}