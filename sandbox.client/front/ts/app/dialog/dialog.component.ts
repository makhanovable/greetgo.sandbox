import {MAT_DIALOG_DATA, MatDialogRef, MatTableDataSource} from "@angular/material";
import {Component, Inject} from "@angular/core";
import {HttpService} from "../HttpService";
import {Client} from "../models/client";
import {ClientInfo} from "../models/client.info";

@Component({
    selector: 'course-dialog',
    template: require('./dialog-component.html'),
    styles: [require('./dialog-component.css')],
})
export class DialogComponent {

    constructor(public dialogRef: MatDialogRef<DialogComponent>,
                @Inject(MAT_DIALOG_DATA) public data: any, private http: HttpService) {
    }

    surname: string;
    name: string;
    patronymic: string;
    addrFactStreet: string;
    addrFactHome: string;
    addrFactFlat;
    addrRegStreet: string;
    addrRegHome: string;
    addrRegFlat: string;
    phoneHome: string;
    phoneWork: string;
    phoneMob1: string;
    phoneMob2: string;
    phoneMob3: string;

    date: string;
    charm: string;
    gender: string;

    clientInfo: ClientInfo;

    ngOnInit() {
        if (this.data.whichDialogNeeded == 2) {
            this.getClientInfoById(this.data.clientId);
        }
    }

    start() {
        if (this.data.whichDialogNeeded == 2) {
            this.edit(this.data.clientId);
        } else {
            this.add();
        }
    }

    edit(clientId) {

        this.http.post("/client/edit_client", {
            clientId: clientId,
            surname: this.surname,
            name: this.name,
            patronymic: this.patronymic,
            gender: this.gender,
            birth_date: this.date,
            charm: this.charm,
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
        }).toPromise().then(res => {
            // alert(res.json())
        }, error => {
            alert("error");
        });
    }

    getClientInfoById(clientId) {
        this.http.get("/client/get_client_info_by_id", {
            clientId: clientId
        }).toPromise().then(result => {
            this.clientInfo = {
                name: JSON.stringify(result.json().name).replace(/["]+/g, ''),
                surname: JSON.stringify(result.json().surname).replace(/["]+/g, ''),
                patronymic: result.json().patronymic.replace(/["]+/g, ''),
                gender: JSON.stringify(result.json().gender).replace(/["]+/g, ''),
                birth_date: JSON.stringify(result.json().birth_date).replace(/["]+/g, ''),
                charm: JSON.stringify(result.json().charm).replace(/["]+/g, ''),
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
                phoneMob3: JSON.stringify(result.json().phoneMob3).replace(/["]+/g, '')
            };
            this.name = this.clientInfo.name;
            this.surname = this.clientInfo.surname;
            this.patronymic = this.clientInfo.patronymic;
            this.gender = this.clientInfo.gender;
            // this.date= this.clientInfo.birth_date;
            this.charm = this.clientInfo.charm;
            this.addrFactStreet = this.clientInfo.addrFactStreet;
            this.addrFactHome = this.clientInfo.addrFactHome;
            this.addrFactFlat = this.clientInfo.addrFactFlat;
            this.addrRegStreet = this.clientInfo.addrRegStreet;
            this.addrRegHome = this.clientInfo.addrRegHome;
            this.addrRegFlat = this.clientInfo.addrRegFlat;
            this.phoneHome = this.clientInfo.phoneHome;
            this.phoneWork = this.clientInfo.phoneWork;
            this.phoneMob1 = this.clientInfo.phoneMob1;
            this.phoneMob2 = this.clientInfo.phoneMob2;
            this.phoneMob3 = this.clientInfo.phoneMob3;
        }).catch(error => {
        })
    }

    add() {
        this.http.post("/client/add_new_client", {
            surname: this.surname,
            name: this.name,
            patronymic: this.patronymic,
            gender: this.gender + "",
            birth_date: this.date,
            charm: this.charm,
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
        }).toPromise().then(res => {
            // alert(res.json())
        }, error => {
            alert("error");
        });
    }

}