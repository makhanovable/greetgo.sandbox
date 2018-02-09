import {AddressType} from "./../enums/AddressType";

export class ClientAddress {
  public clientId: string;
  public type: AddressType;
  public street: string;
  public house: string;
  public flat: string;
}

