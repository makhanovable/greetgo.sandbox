import { AddressType } from './../enums/AddressType';

export class ClientAddress {
    public clientId: number;
    public type: AddressType;
    public street: string;
    public house: string;
    public flat: string;
}

