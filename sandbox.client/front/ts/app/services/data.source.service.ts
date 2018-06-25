import {HttpService} from "../HttpService";
import {Observable} from "rxjs/index";

import {Injectable} from "@angular/core";
import {ClientInfo} from "../../model/client.info";

@Injectable()
export class DataSourceService {

    constructor(private http: HttpService) {
    }

    getClients(URL, params, type): Observable<any> {
        return this.http.post(URL, {
            options: JSON.stringify(params)
        }).map(
            (result) => {
                if (type == 0)
                    return new ClientInfo(result.json());
            }
        );
    }

}