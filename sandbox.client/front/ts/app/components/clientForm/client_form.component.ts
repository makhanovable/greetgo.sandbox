import { PhoneNumberType } from './../../../enums/PhoneNumberType';
import { ClientAddress } from './../../../model/ClientAddress';
import { CharmInfo } from './../../../model/CharmInfo';
import { HttpService } from './../../HttpService';
import { ClientPhone } from './../../../model/ClientPhone';

import { GenderType } from './../../../enums/GenderType';
import { ClientInfo } from './../../../model/ClientInfo';
import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { Number } from 'core-js/library/web/timers';
import { AddressType } from '../../../enums/AddressType';
import { error } from 'util';

// FIXME: 2/8/18 Ставь ; где надо. Убери ненужные импорты. Не засоряй консоль, перед коммитом убирай консоль.лог

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
    newForm: boolean = false;

    requiredPhoneNumbers = {
        "HOME": 1,
        "WORK": 1,
        "MOBILE": 3,
    }

    constructor(
        public dialogRef: MatDialogRef<ClientFormComponent>,
        @Inject(MAT_DIALOG_DATA) public data: any,
        private httpService: HttpService) { }

    ngOnInit() {
        if (this.data.item) {
            this.loadEditableData(this.data.item.id);
            this.newForm = true;
        } else {
            this.formData = new ClientInfo;
            this.formData.phoneNumbers = []
            this.formData.registerAddress = new ClientAddress
            this.formData.registerAddress.type = AddressType.REG
            this.formData.actualAddress = new ClientAddress
            this.formData.actualAddress.type = AddressType.FACT
            this.fillMissingPhones();
        }
        this.charms = this.data.charms

    }

    loadEditableData(id: number): ClientInfo {

        this.httpService.get("/client/info", {
            id: id,
        }).toPromise().then(res => {
            var result = JSON.parse(res.text()) as ClientInfo
            console.log(res.text())
            result.charmId = result.charmId + "";
            result.birthDate = new Date(result.birthDate);

            if (!result.actualAddress)
                result.actualAddress = new ClientAddress;
            if (!result.registerAddress)
                result.registerAddress = new ClientAddress;
            
            result.actualAddress.type = AddressType.FACT;
            result.registerAddress.type = AddressType.REG;
            
            if (result.phoneNumbers) {
                result.phoneNumbers.forEach(element => {
                    this.requiredPhoneNumbers[element.type] = this.requiredPhoneNumbers[element.type] - 1;
                    element.type = this.getPhoneType(element.type);
                });
            }
            else {
                result.phoneNumbers = []
            }
            
            this.formData = result;
            this.fillMissingPhones();
        }).catch(error => {
            console.log(error);
        })


        return null
    }

    fillMissingPhones() {

        for (let i = 0; i < this.requiredPhoneNumbers["HOME"]; i++)
            this.formData.phoneNumbers.push({ number: "", type: PhoneNumberType.HOME } as ClientPhone);
        for (let i = 0; i < this.requiredPhoneNumbers["WORK"]; i++)
            this.formData.phoneNumbers.push({ number: "", type: PhoneNumberType.WORK } as ClientPhone);
        for (let i = 0; i < this.requiredPhoneNumbers["MOBILE"]; i++)
            this.formData.phoneNumbers.push({ number: "", type: PhoneNumberType.MOBILE } as ClientPhone);

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

    getPhoneType(type: string) {
        switch (type) {
            case "HOME":
                return PhoneNumberType.HOME
            case "WORK":
                return PhoneNumberType.WORK
            case "MOBILE":
                return PhoneNumberType.MOBILE
        }
        return undefined
    }

    save() {

        let url = this.newForm ? "/client/update" : "/client/add";
        let finalObject = Object.assign({}, this.formData);

        if( !(this.formData.actualAddress.street || this.formData.actualAddress.house || this.formData.actualAddress.flat))
            delete finalObject.actualAddress
        if( !(this.formData.registerAddress.street || this.formData.registerAddress.house || this.formData.registerAddress.flat))
            delete finalObject.registerAddress;
        
        finalObject.phoneNumbers = []
        this.formData.phoneNumbers.forEach(element => {
            if(element.number != null && element.number != "")
                finalObject.phoneNumbers.push(element)
        });

        this.httpService.post(url, {
            client: JSON.stringify(finalObject)
        }).toPromise().then(res => {
            if(res.text() === "bad") 
                alert('something went wrong')

            console.log(res.text())
        }).catch(error => {
            console.log(error);
        })

        this.dialogRef.close();
    }

    exit() {
        this.dialogRef.close(); 
    }



}