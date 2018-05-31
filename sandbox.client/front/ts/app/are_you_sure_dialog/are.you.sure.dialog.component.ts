import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material";
import {Component, Inject} from "@angular/core";
import {HttpService} from "../HttpService";

@Component({
    selector: 'are-you-sure-dialog',
    template: require('./are-you-sure-dialog-component.html'),
})
export class AreYouSureDialogComponent {

    constructor(public dialogRef: MatDialogRef<AreYouSureDialogComponent>,
                @Inject(MAT_DIALOG_DATA) public data: any, private http: HttpService) {
    }

    clientId: number;

    ngOnInit() {
        this.clientId = this.data.clientId;
    }

    del() { // TODO update list

        // this.http.post("/client/del_client", {
        //     clientId: clientId
        // }).toPromise().then(res => {
        //     // alert(res.json())
        // }, error => {
        //     alert("error");
        // });
    }

}