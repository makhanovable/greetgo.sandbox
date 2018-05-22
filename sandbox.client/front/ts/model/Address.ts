import {AddressType} from "./AddressType";

export class Address {
  public id: number;
  public clientId: number;
  public type: AddressType;
  public street: string;
  public house: string;
  public flat: string;


  constructor(clientId: number, type: AddressType, street: string, house: string, flat: string) {
    this.clientId = clientId;
    this.type = type;
    this.street = street;
    this.house = house;
    this.flat = flat;
  }
}