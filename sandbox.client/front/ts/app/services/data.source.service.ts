import {HttpService} from "../HttpService";
import {Observable} from "rxjs/index";

import {Injectable} from "@angular/core";
import {ClientInfo} from "../models/client.info";

@Injectable()
export class DataSourceService {

    constructor(private http: HttpService) {
    } // TODO edit

    getClients(URL, params): Observable<any> {
        return this.http.post(URL, {
            options: params
        }).map(
            (result) => {
                return new ClientInfo(result.json());
            }
        );
    }

}