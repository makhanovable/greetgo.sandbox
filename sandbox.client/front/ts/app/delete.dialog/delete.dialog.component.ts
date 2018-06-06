import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material";
import {Component, Inject} from "@angular/core";
import {HttpService} from "../HttpService";

@Component({
    selector: 'are-you-sure-dialog',
    template: require('./delete.dialog.component.html'),
})
export class DeleteDialogComponent {

    constructor(public dialogRef: MatDialogRef<DeleteDialogComponent>,
                @Inject(MAT_DIALOG_DATA) public data: any, private http: HttpService) {
    }

    delete() {
        this.dialogRef.close('yes');
    }

}