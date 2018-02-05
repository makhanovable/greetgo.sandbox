import { ClientPhone } from './../../../model/ClientPhone';
import { Charm } from './../../../model/Charm';
import { GenderType } from './../../../enums/GenderType';
import { ClientInfo } from './../../../model/ClientInfo';
import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { Number } from 'core-js/library/web/timers';
import { PhoneNumberType } from '../../../enums/PhoneNumberType';

@Component({
    selector: 'client-form-component',
    template: require('./client_form.component.html'),
    styles: [require('./client_form.component.css')],
})
export class ClientFormComponent implements OnInit {

    formData: ClientInfo;
    charms: any[];
    FactAddressAsRegister: boolean = true;
    phoneMask: any[] = ['+', '7', ' ', '(', /[1-9]/, /\d/, /\d/, ')', ' ', /\d/, /\d/, /\d/, '-', /\d/, /\d/, /\d/, /\d/];
    workPhones: ClientPhone[] = [];
    homePhones: ClientPhone[] = [];
    mobilePhones: ClientPhone[] = [];

    constructor(
        public dialogRef: MatDialogRef<ClientFormComponent>,
        @Inject(MAT_DIALOG_DATA) public data: any) { }

    ngOnInit() {
        console.log(this.data.item)
        if (this.data.item) {
            this.formData = JSON.parse(JSON.stringify(this.data.item)) as ClientInfo;            
            this.loadEditableData(this.formData.id);
            this.formData.phoneNumbers = []

        } else {
            this.formData = new ClientInfo;
            this.formData.phoneNumbers.push({ number: "", type: PhoneNumberType.WORK } as ClientPhone)
            this.formData.phoneNumbers.push({ number: "", type: PhoneNumberType.HOME } as ClientPhone)
            this.formData.phoneNumbers.push({ number: "", type: PhoneNumberType.MOBILE } as ClientPhone)
        }
        this.charms = this.data.charms


        this.formData.phoneNumbers.forEach(element => {
            switch (element.type) {
                case PhoneNumberType.HOME:
                    this.homePhones.push(element)
                    break;
                case PhoneNumberType.MOBILE:
                    this.mobilePhones.push(element)
                    break;
                case PhoneNumberType.WORK:
                    this.workPhones.push(element)
                    break;

            }

        });


    }


    addNumber(type: PhoneNumberType) {
        var newNumber = { type: type, number: "" } as ClientPhone;
        this.formData.phoneNumbers.push(newNumber)
        
        switch (type) {
            case PhoneNumberType.HOME:
                this.homePhones.push(newNumber)
                break;
            case PhoneNumberType.WORK:
                this.workPhones.push(newNumber)
                break;
            case PhoneNumberType.MOBILE:
                this.mobilePhones.push(newNumber)
                break;
        }
    }

    loadEditableData(id: number) {


    }

    getPhoneLabel(type: PhoneNumberType) {
        switch (type) {
            case PhoneNumberType.HOME:
                return "Home number"
            case PhoneNumberType.WORK:
                return "Work number"
            case PhoneNumberType.MOBILE:
                return "Mobile number"
        }
        return undefined
    }

    save() {

        // TODO send to server to save 

        //temp, do not forget to remove 
        if (!this.formData.id)
            this.formData.id = Math.random();

        this.dialogRef.close(this.formData);
    }

    exit() {
        console.log(this.formData)
        // this.dialogRef.close(); 
    }

}