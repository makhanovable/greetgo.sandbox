export class ClientInfo {
    public id: number;
    public name: string;
    public patronymic: string;
    public surName: string;
    public birthDay: Date;
    public charm: Charm;
    public totalAccountBalance: number;
    public maximumBalance: number;
    public minimumBalance: number;
}

export class Charm {
    public id: number;
    public name: string;
    public description: string;
    public energy: number;
}

export enum Gender {
    MALE, FEMALE
}


