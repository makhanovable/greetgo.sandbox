export class EditableClientInfo {
    public id = "";
    public name = "";
    public surname = "";
    public patronymic = "";
    public gender = "";
    public birth_date = "";
    public charm = "";
    public fAdressStreet = "";
    public fAdressHouse = "";
    public fAdressFlat = "";
    public rAdressStreet = "";
    public rAdressHouse = "";
    public rAdressFlat = "";
    public workPhone = "";
    public homePhone = "";
    public mobilePhones = [""];

    public static from(a: EditableClientInfo) : EditableClientInfo {

        let ret: EditableClientInfo = new EditableClientInfo;

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
        this.workPhone = "";
        this.homePhone = "";
        this.mobilePhones = [""];
    }
}