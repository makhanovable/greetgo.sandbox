import {Client} from "./models/client";
import {ClientWrapper} from "./models/client.wrapper";
import {HttpService} from "./HttpService";
import {Observable} from "rxjs/index";

export class ClientDataSource {

    constructor(private http: HttpService) {
    }

    ELEMENTS: Client[] = [];

    getClients(filter: string, sort: string, order: string, page: number, size: number): Observable<ClientWrapper> {
        console.log(filter + " - " + sort + " - " + order + " - " + page + " - " + size);
        if (size == null)
            size = 5;
        return this.http.post("/client/get_clients_list", {
            filter: filter, sort: sort, order: order, page: page, size: size
        }).map(
            (result) => {
                //alert(JSON.stringify(result.json()));
                this.ELEMENTS = [];
                for (let i = 0; i < Number(JSON.stringify(result.json().items.length)); i++) {
                    let Client = {
                        id: Number(JSON.stringify(result.json().items[i].id)),
                        name: JSON.stringify(result.json().items[i].name).replace(/["]+/g, ''),
                        charm: Number(JSON.stringify(result.json().items[i].charm)),
                        age: Number(JSON.stringify(result.json().items[i].age)),
                        total_account_balance: Number(JSON.stringify(result.json().items[i].total)),
                        max_balance: Number(JSON.stringify(result.json().items[i].max)),
                        min_balance: Number(JSON.stringify(result.json().items[i].min))
                    };
                    this.ELEMENTS.push(Client);
                }
                return {
                    items: this.ELEMENTS,
                    total_count: Number(JSON.stringify(result.json().total_count))
                };
            }
        );
    }
}