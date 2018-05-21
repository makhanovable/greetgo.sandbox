import {AddressType} from "./AddressType";

export class Address {
  public id: number;
  public clientId: number;
  public addressType: AddressType;
  public street: string;
  public house: string;
  public flat: string;
}