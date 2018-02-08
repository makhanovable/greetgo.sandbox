import { ClientInfo } from './../model/ClientInfo';
import { HttpService } from './HttpService';

import { Injectable } from '@angular/core';


//fixme если класс не нужен, то удали

@Injectable()
export class AppService {
    constructor(private httpService: HttpService) { }

    getClients() {
        
    }

}