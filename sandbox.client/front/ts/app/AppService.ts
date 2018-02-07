import { ClientInfo } from './../model/ClientInfo';
import { HttpService } from './HttpService';

import { Injectable } from '@angular/core';




@Injectable()
export class AppService {
    constructor(private httpService: HttpService) { }

    getClients() {
        
    }

}