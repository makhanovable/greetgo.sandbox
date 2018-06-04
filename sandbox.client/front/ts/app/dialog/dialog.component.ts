import {MAT_DIALOG_DATA, MatDialogRef, MatTableDataSource} from "@angular/material";
import {Component, Inject} from "@angular/core";
import {HttpService} from "../HttpService";
import {Client} from "../models/client";
import {Charm} from "../models/charm";
import {CharmService} from "../services/charm.service";
import {ClientsInfoService} from "../services/clients.info.service";
import {FormControl} from "@angular/forms";

@Component({
    selector: 'course-dialog',
    template: require('./dialog-component.html'),
    styles: [require('./dialog-component.css')],
})
export class DialogComponent {

    constructor(public dialogRef: MatDialogRef<DialogComponent>,
                @Inject(MAT_DIALOG_DATA) public data: any, private http: HttpService,
                private charmsService: CharmService, private clientInfoService: ClientsInfoService) {
    }

    client: Client;

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

    mydate: string;
    charm_selected: number;
    gender: string;

    charmList: Charm[] = [];

    ngOnInit() {
        this.charmList = this.charmsService.list;

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
            birth_date: this.mydate,
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
        }).toPromise().then(res => {
            // alert(res.json())
        }, error => {
            alert("error");
        });
    }

    getClientInfoById(clientId) {
        this.client = this.clientInfoService.getClientById(clientId);
        this.name = this.client.name;
        this.surname = this.client.surname;
        this.patronymic = this.client.patronymic;
        this.gender = this.client.gender;
        this.mydate= '27/03/1999';
        this.charm_selected = this.client.charm_id;
        this.addrFactStreet = this.client.addrFactStreet;
        this.addrFactHome = this.client.addrFactHome;
        this.addrFactFlat = this.client.addrFactFlat;
        this.addrRegStreet = this.client.addrRegStreet;
        this.addrRegHome = this.client.addrRegHome;
        this.addrRegFlat = this.client.addrRegFlat;
        this.phoneHome = this.client.phoneHome;
        this.phoneWork = this.client.phoneWork;
        this.phoneMob1 = this.client.phoneMob1;
        this.phoneMob2 = this.client.phoneMob2;
        this.phoneMob3 = this.client.phoneMob3;
    }

    add() {
        console.log(this.mydate);
        this.http.post("/client/add_new_client", {
            surname: this.surname,
            name: this.name,
            patronymic: this.patronymic,
            gender: this.gender,
            birth_date: this.mydate,
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
        }).toPromise().then(res => {
            // alert(res.json())
        }, error => {
            alert("error");
        });
    }

}