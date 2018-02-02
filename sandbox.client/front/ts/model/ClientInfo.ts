export class ClientInfo {
    public id: number;
    public name: string;
    public patronymic: string;
    public surName: string;
    public gender: Gender;
    public birthDay: Date;
    public charm: Charm;
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


