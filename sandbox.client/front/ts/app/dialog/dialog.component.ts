import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material";
import {Component, Inject} from "@angular/core";
import {HttpService} from "../HttpService";

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
    string;
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

    ngOnInit() {
        if (this.data.whichDialogNeeded == 2) {
            this.edit(this.data.clientId);
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
        this.surname = 'mkahanov';
    }

    add() { // TODO get from field and update list
        this.http.post("/client/add_new_client", {
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

}