import {Client} from "./models/client";
import {ClientWrapper} from "./models/client.wrapper";
import {HttpService} from "./HttpService";
import {Observable} from "rxjs/index";
import {CharmService} from "./services/charm.service";
import {ClientsInfoService} from "./services/clients.info.service";

export class ClientDataSource {

    constructor(private http: HttpService, private charmService: CharmService,
                private clientsInfo: ClientsInfoService) {
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
                this.charmService.getCharms();
                for (let i = 0; i < Number(JSON.stringify(result.json().items.length)); i++) {
                    let Client = {
                        id: Number(JSON.stringify(result.json().items[i].id)),
                        charm: this.charmService.getCharmNameById(JSON.stringify(result.json().items[i].charm)),
                        age: Number(JSON.stringify(result.json().items[i].age)),
                        total_account_balance: Number(JSON.stringify(result.json().items[i].total)),
                        max_balance: Number(JSON.stringify(result.json().items[i].max)),
                        min_balance: Number(JSON.stringify(result.json().items[i].min)),

                        name: JSON.stringify(result.json().items[i].name).replace(/["]+/g, '')  ,
                        surname: JSON.stringify(result.json().items[i].surname).replace(/["]+/g, '')  ,
                        patronymic: JSON.stringify(result.json().items[i].patronymic).replace(/["]+/g, '')  ,
                        gender: JSON.stringify(result.json().items[i].gender).replace(/["]+/g, '')  ,
                        birth_date: JSON.stringify(result.json().items[i].birth_date).replace(/["]+/g, '')  ,
                        charm_id: Number(JSON.stringify(result.json().items[i].charm)),
                        addrFactStreet: JSON.stringify(result.json().items[i].addrFactStreet).replace(/["]+/g, '')  ,
                        addrFactHome: JSON.stringify(result.json().items[i].addrFactHome).replace(/["]+/g, '')  ,
                        addrFactFlat: JSON.stringify(result.json().items[i].addrFactFlat).replace(/["]+/g, '')  ,
                        addrRegStreet: JSON.stringify(result.json().items[i].addrRegStreet).replace(/["]+/g, '')  ,
                        addrRegHome: JSON.stringify(result.json().items[i].addrRegHome).replace(/["]+/g, '')  ,
                        addrRegFlat: JSON.stringify(result.json().items[i].addrRegFlat).replace(/["]+/g, '')  ,
                        phoneHome: JSON.stringify(result.json().items[i].phoneHome).replace(/["]+/g, '')  ,
                        phoneWork: JSON.stringify(result.json().items[i].phoneWork).replace(/["]+/g, '')  ,
                        phoneMob1: JSON.stringify(result.json().items[i].phoneMob1).replace(/["]+/g, '')  ,
                        phoneMob2: JSON.stringify(result.json().items[i].phoneMob2).replace(/["]+/g, '')  ,
                        phoneMob3: JSON.stringify(result.json().items[i].phoneMob3).replace(/["]+/g, '')  ,
                    };
                    this.ELEMENTS.push(Client);
                }
                this.clientsInfo.list = this.ELEMENTS;
                return {
                    items: this.ELEMENTS,
                    total_count: Number(JSON.stringify(result.json().total_count))
                };
            }
        );
    }

}