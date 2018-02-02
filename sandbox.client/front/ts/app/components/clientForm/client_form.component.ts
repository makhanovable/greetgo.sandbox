import { GenderType } from './../../../enums/GenderType';
import { ClientInfo } from './../../../model/ClientInfo';
import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { Number } from 'core-js/library/web/timers';

@Component({
    selector: 'client-form-component',
    template: require('./client_form.component.html'),
    styles: [require('./client_form.component.css')],
})
export class ClientFormComponent implements OnInit {

    formData: ClientInfo;
    genders = Object.keys(GenderType)

    constructor(
        public dialogRef: MatDialogRef<ClientFormComponent>,
        @Inject(MAT_DIALOG_DATA) public data: any) {
        
    
    }

    ngOnInit() {

        if (this.data.item) {
            this.formData = this.data.item as ClientInfo
        } else {
            this.formData = new ClientInfo;
        }
    }

    save() {
        // TODO send to server to save 
        
        //temp, do not forget to remove 
        if (!this.formData.id)
            this.formData.id = Math.random();

        this.dialogRef.close(this.formData);
    }

}