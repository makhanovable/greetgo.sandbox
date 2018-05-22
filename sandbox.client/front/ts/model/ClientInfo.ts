import {Address} from "./Address";
import {Phone} from "./Phone";
import {Client} from "./Client";

export class ClientInfo {
  public client: Client;
  public factAddress: Address;
  public regAddress: Address;
  public phones: Phone[];

  constructor(client: Client, factAddress: Address, regAddress: Address, phones: Phone[]) {
    this.client = client;
    this.factAddress = factAddress;
    this.regAddress = regAddress;
    this.phones = phones;
  }
}