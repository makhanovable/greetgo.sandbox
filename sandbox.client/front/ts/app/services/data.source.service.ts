import {HttpService} from "../HttpService";
import {Observable} from "rxjs/index";

import {Injectable} from "@angular/core";
import {ClientRecord} from "../../model/client.record";

@Injectable()
export class DataSourceService {

    constructor(private http: HttpService) {
    }

    items: ClientRecord[];

    getClients(URL, params, type): Observable<any> {
        return this.http.post(URL, {
            options: JSON.stringify(params)
        }).map(
            (result) => {
                if (type == 0) {
                    this.items = [];
                    for (let res of result.json()) {
                        let clientRecord = new ClientRecord(res);
                        this.items.push(clientRecord);
                    }
                    return this.items;
                }
            }
        );
    }

}