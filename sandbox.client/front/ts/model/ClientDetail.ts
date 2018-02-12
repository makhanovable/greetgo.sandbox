import {ClientPhone} from "./ClientPhone";
import {ClientAddress} from "./ClientAddress";

import {GenderType} from "../enums/GenderType";

export class ClientDetail {
  
  public id: string;
  public name: string;
  public patronymic: string;
  public surname: string;
  public charm: string;
  public birthDate: Date;
  public actualAddress: ClientAddress;
  public registerAddress: ClientAddress;
  
  public phoneNumbers: ClientPhone[];
  public gender: GenderType;
}






