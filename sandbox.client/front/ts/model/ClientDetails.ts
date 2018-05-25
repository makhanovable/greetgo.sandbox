import {Address} from "./Address";
import {Phone} from "./Phone";
import {Gender} from "./Gender";
import {Charm} from "./Charm";

export class ClientDetails {
  public id: number/*int*/ = -1;
  public name: string;
  public surname: string;
  public patronymic: string;
  public gender: Gender;
  public birthDate: number/*long*/;
  public charmId: number/*int*/;
  public charmsDictionary: Charm[];
  public factAddress: Address;
  public regAddress: Address;
  public phones: Phone[];
}