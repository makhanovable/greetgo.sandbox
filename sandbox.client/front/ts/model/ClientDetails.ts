export class ClientDetails {
    public id : string;
    public name : string;
    public surname : string;
    public patronymic : string;
    public gender : string;
    public birth_date : string;
    public charm : string;
    public fAdressStreet : string;
    public fAdressHouse : string;
    public fAdressFlat : string;
    public rAdressStreet : string;
    public rAdressHouse : string;
    public rAdressFlat : string;
    public homePhone : string[];
    public workPhone : string[];
    public mobilePhones : string[];

    public static from(a: ClientDetails) : ClientDetails {

        let ret: ClientDetails = new ClientDetails;

        ret.id = a.id;
        ret.name = a.name ;
        ret.surname = a.surname ;
        ret.patronymic = a.patronymic ;
        ret.gender = a.gender ;
        ret.birth_date = a.birth_date ;
        ret.charm = a.charm ;
        ret.fAdressStreet = a.fAdressStreet ;
        ret.fAdressHouse = a.fAdressHouse ;
        ret.fAdressFlat = a.fAdressFlat ;
        ret.rAdressStreet = a.rAdressStreet ;
        ret.rAdressHouse = a.rAdressHouse ;
        ret.rAdressFlat = a.rAdressFlat ;
        ret.workPhone = a.workPhone ;
        ret.homePhone = a.homePhone ;
        ret.mobilePhones = a.mobilePhones ;

        if (typeof ret.homePhone == "undefined") {
            ret.homePhone = [""];
        }
        if (typeof ret.workPhone == "undefined") {
            ret.workPhone= [""];
        }
        if (typeof ret.mobilePhones == "undefined") {
            ret.mobilePhones = [""];
        }

        return ret;
    }

    public clearPar() {
        console.log("I'm here");
        this.name = "";
        this.surname = "";
        this.patronymic = "";
        this.gender = "";
        this.birth_date = "";
        this.charm = "";
        this.fAdressStreet = "";
        this.fAdressHouse = "";
        this.fAdressFlat = "";
        this.rAdressStreet = "";
        this.rAdressHouse = "";
        this.rAdressFlat = "";
        this.workPhone = [""];
        this.homePhone = [""];
        this.mobilePhones = [""];
    }
}