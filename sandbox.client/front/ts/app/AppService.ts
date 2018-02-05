import { ClientInfo } from './../model/ClientInfo';
import { HttpService } from './HttpService';
import { Charm } from './../model/Charm';
import { Injectable } from '@angular/core';




@Injectable()
export class AppService {
    constructor(private httpService: HttpService) { }

    getClients() {
        
    }

}