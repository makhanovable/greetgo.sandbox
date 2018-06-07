import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material";
import {Component, Inject} from "@angular/core";

@Component({
    selector: 'error-dialog',
    template: require('./error.dialog.component.html'),
})
export class ErrorDialogComponent {

    constructor(public dialogRef: MatDialogRef<ErrorDialogComponent>,
                @Inject(MAT_DIALOG_DATA) public data: any) {
    }
}