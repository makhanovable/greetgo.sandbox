import {AddressType} from "./AddressType";

export class Address {
  public id: number/*int*/;
  public clientId: number/*int*/;
  public type: AddressType;
  public street: string;
  public house: string;
  public flat: string;

  constructor(id: number, clientId: number, type: AddressType, street: string, house: string, flat: string) {
    this.id = id;
    this.clientId = clientId;
    this.type = type;
    this.street = street;
    this.house = house;
    this.flat = flat;
  }
}