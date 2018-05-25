import {AddressType} from "./AddressType";

export class Address {
  public id: number/*int*/;
  public clientId: number/*int*/;
  public type: AddressType;
  public street: string;
  public house: string;
  public flat: string;

}