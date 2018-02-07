import { ClientPhone } from './ClientPhone';
import { ClientAddress } from './ClientAddress';

import { GenderType } from '../enums/GenderType';

export class ClientInfo {
    //table data
    public id: number;
    public name: string;
    public patronymic: string;
    public surname: string;
    public age: number;
    public charmId: string;
    public totalAccountBalance: number;
    public maximumBalance: number;
    public minimumBalance: number;

    //manually loading data.
    public birthDate: Date;
    public actualAddress: ClientAddress;
    public registerAddress: ClientAddress;

    public phoneNumbers: ClientPhone[];
    public gender: GenderType;
}






