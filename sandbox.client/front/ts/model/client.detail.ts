export class ClientDetail {
    id: number;
    name: string;
    surname: string;
    patronymic: string;
    gender: string;
    birth_date: string;
    charm: number;
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

    constructor(result) {
        if (result != null) {
            this.id = Number(JSON.stringify(result.id));
            this.name = JSON.stringify(result.name).replace(/["]+/g, '');
            this.surname = JSON.stringify(result.surname).replace(/["]+/g, '');
            this.patronymic = JSON.stringify(result.patronymic).replace(/["]+/g, '');
            this.gender = JSON.stringify(result.gender).replace(/["]+/g, '');
            this.birth_date = JSON.stringify(result.birth_date).replace(/["]+/g, '');
            this.charm = Number(JSON.stringify(result.charm));
            this.addrFactStreet = JSON.stringify(result.addrFactStreet).replace(/["]+/g, '');
            this.addrFactHome = JSON.stringify(result.addrFactHome).replace(/["]+/g, '');
            this.addrFactFlat = JSON.stringify(result.addrFactFlat).replace(/["]+/g, '');
            this.addrRegStreet = JSON.stringify(result.addrRegStreet).replace(/["]+/g, '');
            this.addrRegHome = JSON.stringify(result.addrRegHome).replace(/["]+/g, '');
            this.addrRegFlat = JSON.stringify(result.addrRegFlat).replace(/["]+/g, '');
            this.phoneHome = JSON.stringify(result.phoneHome).replace(/["]+/g, '');
            this.phoneWork = JSON.stringify(result.phoneWork).replace(/["]+/g, '');
            this.phoneMob1 = JSON.stringify(result.phoneMob1).replace(/["]+/g, '');
            this.phoneMob2 = JSON.stringify(result.phoneMob2).replace(/["]+/g, '');
            this.phoneMob3 = JSON.stringify(result.phoneMob3).replace(/["]+/g, '');
        }
    }

}