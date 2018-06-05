import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material";
import {Component, Inject} from "@angular/core";
import {HttpService} from "../HttpService";
import {Charm} from "../models/charm";
import {CharmService} from "../services/charm.service";
import {ClientDetails} from "../models/client.details";

@Component({
    selector: 'course-dialog',
    template: require('./dialog-component.html'),
    styles: [require('./dialog-component.css')],
})
export class DialogComponent {

    constructor(public dialogRef: MatDialogRef<DialogComponent>,
                @Inject(MAT_DIALOG_DATA) public data: any, private http: HttpService,
                private charmsService: CharmService) {
    }


    clientDetail: ClientDetails;

    surname: string;
    name: string;
    patronymic: string;
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

    mydate: Date;
    charm_selected: number;
    gender: string;

    charmList: Charm[] = [];

    ngOnInit() {
        this.charmList = this.charmsService.list;

        if (this.data.whichDialogNeeded == 2) {
            this.getClientDetailById(this.data.clientId);
        }
    }

    start() {
        if (this.surname == null || this.surname == '' ||
            this.name == null || this.name == '' ||
            this.patronymic == null || this.patronymic == '' ||
            this.gender == null || this.gender == '' ||
            this.charm_selected == null ||
            this.mydate == null ||
            this.addrRegStreet == null || this.addrRegStreet == '' ||
            this.addrRegHome == null || this.addrRegHome == '' ||
            this.addrRegFlat == null || this.addrRegFlat == '' ||
            this.phoneHome == null || this.phoneHome == '') { // TODO check on html
        } else {
            if (this.data.whichDialogNeeded == 2) {
                this.edit(this.data.clientId);
            } else {
                this.add();
            }
        }
    }

    edit(clientId) {
        this.http.post("/client/edit_client", {
            clientId: clientId,
            surname: this.surname,
            name: this.name,
            patronymic: this.patronymic,
            gender: this.gender,
            birth_date: this.mydate.getMonth() + "/" + this.mydate.getDay() + "/" + this.mydate.getFullYear(),
            charm_selected: this.charm_selected,
            addrFactStreet: this.addrFactStreet,
            addrFactHome: this.addrFactHome,
            addrFactFlat: this.addrFactFlat,
            addrRegStreet: this.addrRegStreet,
            addrRegHome: this.addrRegHome,
            addrRegFlat: this.addrRegFlat,
            phoneHome: this.phoneHome,
            phoneWork: this.phoneWork,
            phoneMob1: this.phoneMob1,
            phoneMob2: this.phoneMob2,
            phoneMob3: this.phoneMob3
        }).toPromise().then(result => {
            let Client = {
                id: Number(JSON.stringify(result.json().id)),
                name: JSON.stringify(result.json().name).replace(/["]+/g, ''),
                charm: JSON.stringify(result.json().charm).replace(/["]+/g, ''),
                age: Number(JSON.stringify(result.json().age)),
                total_account_balance: Number(JSON.stringify(result.json().total)),
                max_balance: Number(JSON.stringify(result.json().max)),
                min_balance: Number(JSON.stringify(result.json().min)),
            };
            this.dialogRef.close(Client);
        }, error => {
            alert("error");
        });

    }

    getClientDetailById(clientId) {
        this.http.post("/client/get_client_info_by_id", {
            clientId: clientId
        }).toPromise().then(result => {
            let ClientDetails = {
                id: Number(JSON.stringify(result.json().id)),
                name: JSON.stringify(result.json().name).replace(/["]+/g, ''),
                surname: JSON.stringify(result.json().surname).replace(/["]+/g, ''),
                patronymic: JSON.stringify(result.json().patronymic).replace(/["]+/g, ''),
                gender: JSON.stringify(result.json().gender).replace(/["]+/g, ''),
                birth_date: JSON.stringify(result.json().birth_date).replace(/["]+/g, ''),
                charm_id: Number(JSON.stringify(result.json().charm)),
                addrFactStreet: JSON.stringify(result.json().addrFactStreet).replace(/["]+/g, ''),
                addrFactHome: JSON.stringify(result.json().addrFactHome).replace(/["]+/g, ''),
                addrFactFlat: JSON.stringify(result.json().addrFactFlat).replace(/["]+/g, ''),
                addrRegStreet: JSON.stringify(result.json().addrRegStreet).replace(/["]+/g, ''),
                addrRegHome: JSON.stringify(result.json().addrRegHome).replace(/["]+/g, ''),
                addrRegFlat: JSON.stringify(result.json().addrRegFlat).replace(/["]+/g, ''),
                phoneHome: JSON.stringify(result.json().phoneHome).replace(/["]+/g, ''),
                phoneWork: JSON.stringify(result.json().phoneWork).replace(/["]+/g, ''),
                phoneMob1: JSON.stringify(result.json().phoneMob1).replace(/["]+/g, ''),
                phoneMob2: JSON.stringify(result.json().phoneMob2).replace(/["]+/g, ''),
                phoneMob3: JSON.stringify(result.json().phoneMob3).replace(/["]+/g, ''),
            };
            this.setData(ClientDetails);
        }, error => {
            alert("error");
        });
    }

    setData(clientDetail) {
        this.clientDetail = clientDetail;
        if (this.clientDetail != null) {
            this.name = this.clientDetail.name;
            this.surname = this.clientDetail.surname;
            this.patronymic = this.clientDetail.patronymic;
            this.gender = this.clientDetail.gender;
            this.mydate = new Date(this.clientDetail.birth_date);
            this.charm_selected = this.clientDetail.charm_id;
            this.addrFactStreet = this.clientDetail.addrFactStreet;
            this.addrFactHome = this.clientDetail.addrFactHome;
            this.addrFactFlat = this.clientDetail.addrFactFlat;
            this.addrRegStreet = this.clientDetail.addrRegStreet;
            this.addrRegHome = this.clientDetail.addrRegHome;
            this.addrRegFlat = this.clientDetail.addrRegFlat;
            this.phoneHome = this.clientDetail.phoneHome;
            this.phoneWork = this.clientDetail.phoneWork;
            this.phoneMob1 = this.clientDetail.phoneMob1;
            this.phoneMob2 = this.clientDetail.phoneMob2;
            this.phoneMob3 = this.clientDetail.phoneMob3;
        }
    }

    add() {
        this.http.post("/client/add_new_client", {
            surname: this.surname,
            name: this.name,
            patronymic: this.patronymic,
            gender: this.gender,
            birth_date: this.mydate.getMonth() + "/" + this.mydate.getDay() + "/" + this.mydate.getFullYear(),
            charm: this.charm_selected,
            addrFactStreet: this.addrFactStreet,
            addrFactHome: this.addrFactHome,
            addrFactFlat: this.addrFactFlat,
            addrRegStreet: this.addrRegStreet,
            addrRegHome: this.addrRegHome,
            addrRegFlat: this.addrRegFlat,
            phoneHome: this.phoneHome,
            phoneWork: this.phoneWork,
            phoneMob1: this.phoneMob1,
            phoneMob2: this.phoneMob2,
            phoneMob3: this.phoneMob3
        }).toPromise().then(result => {
            let Client = {
                id: Number(JSON.stringify(result.json().id)),
                name: JSON.stringify(result.json().name).replace(/["]+/g, ''),
                charm: JSON.stringify(result.json().charm).replace(/["]+/g, ''),
                age: Number(JSON.stringify(result.json().age)),
                total_account_balance: Number(JSON.stringify(result.json().total)),
                max_balance: Number(JSON.stringify(result.json().max)),
                min_balance: Number(JSON.stringify(result.json().min)),
            };
            this.dialogRef.close(Client);
        }, error => {
            alert("error");
        });
    }

}