export interface Client {
    id: number;
    charm: string;
    age: number;
    total_account_balance: number;
    max_balance: number;
    min_balance: number;

    name: string;
    surname: string;
    patronymic: string;
    gender: string;
    birth_date: string;
    charm_id: number;
    addrFactStreet: string;
    addrFactHome: string;
    addrFactFlat: string;
    addrRegStreet: string;
    addrRegHome: string;
    addrRegFlat: string;
    phoneHome: string;
    phoneWork: string;
    phoneMob1: string;
    phoneMob2: string;
    phoneMob3: string;
}