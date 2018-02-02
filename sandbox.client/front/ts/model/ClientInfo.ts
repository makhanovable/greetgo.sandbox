import { ClientAddress } from './ClientAddress';
import { Charm } from './Charm';
import { GenderType } from '../enums/GenderType';

export class ClientInfo {
    //table data
    public id: number;
    public name: string;
    public patronymic: string;
    public surname: string;
    public birthDay: Date = new Date();
    public charm: Charm = {} as Charm;
    public totalAccountBalance: number;
    public maximumBalance: number;
    public minimumBalance: number;

    //manually loading data
    public addresses: ClientAddress[];
    public phoneNumbers: ClientAddress[];
    public gender: GenderType;
}






