import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material";
import {Component, Inject, OnInit} from "@angular/core";
import {FormBuilder, FormGroup} from "@angular/forms";

@Component({
    selector: 'error-course-dialog',
    template: require('./error-dialog-component.html'),
})
export class ErrorDialogComponent {

    constructor(
        public dialogRef: MatDialogRef<ErrorDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: any) { }

    onNoClick(): void {
        this.dialogRef.close();
    }
}