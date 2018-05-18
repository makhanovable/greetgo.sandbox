import {Address} from "./Address";
import {Phone} from "./Phone";
import {Charm} from "./Charm";
import {Client} from "./Client";

export class ClientInfoModel {
  public clientInfo: Client;
  public factAddress: Address;
  public regAddress: Address;
  public phones: Phone[];
  public charmDots: Charm[];
}