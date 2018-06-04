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
                        id: JSON.stringify(result.json().items[i].id).replace(/["]+/g, ''),
                        name: JSON.stringify(result.json().items[i].name).replace(/["]+/g, ''),
                        charm: JSON.stringify(result.json().items[i].charm).replace(/["]+/g, ''),
                        age: JSON.stringify(result.json().items[i].age).replace(/["]+/g, ''),
                        total_account_balance: JSON.stringify(result.json().items[i].total).replace(/["]+/g, ''),
                        max_balance: JSON.stringify(result.json().items[i].max).replace(/["]+/g, ''),
                        min_balance: JSON.stringify(result.json().items[i].min).replace(/["]+/g, '')
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