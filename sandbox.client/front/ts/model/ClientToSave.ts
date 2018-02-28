import { ClientDetail } from './ClientDetail';
import {ClientPhone} from "./ClientPhone";
import {ClientAddress} from "./ClientAddress";

import {GenderType} from "../enums/GenderType";

export class ClientToSave {
  //table data
  public id: string;
  public name: string;
  public patronymic: string;
  public surname: string;
  public charm: string;
  public birthDate: Date;
  public gender: GenderType;
  
  public actualAddress: ClientAddress;
  public registerAddress: ClientAddress;
  
  public numbersToSave: ClientPhone[];
  public numbersToDelete: ClientPhone[];
  
  constructor(data?: ClientDetail){
    if(data) {
      this.id = data.id;
      this.name = data.name;
      this.surname = data.surname;
      this.patronymic = data.patronymic;
      this.birthDate = data.birthDate;
      this.gender = data.gender;
      this.charm = data.charm;      
    }
  }

}






