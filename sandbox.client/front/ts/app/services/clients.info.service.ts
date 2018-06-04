import {Injectable} from "@angular/core";
import {Client} from "../models/client";

@Injectable()
export class ClientsInfoService {
    list: Client[] = [];
    client: Client;

    getClientById(id): Client {
        for (let i = 0; i < this.list.length; i++) {
            if (this.list[i].id == id) {
                this.client = this.list[i];
                break;
            }
        }
        return this.client;
    }
}