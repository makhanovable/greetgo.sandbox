import {AddressType} from "./AddressType";

export class Address {
  public id: number;
  public clientId: number;
  public addressType: AddressType;
  public street: string;
  public house: string;
  public flat: string;


  constructor(clientId: number, addressType: AddressType, street: string, house: string, flat: string) {
    this.clientId = clientId;
    this.addressType = addressType;
    this.street = street;
    this.house = house;
    this.flat = flat;
  }
}