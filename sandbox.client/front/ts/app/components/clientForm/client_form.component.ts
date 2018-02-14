import {ClientPhone} from "../../../model/ClientPhone";
import {ClientAddress} from "../../../model/ClientAddress";
import {ClientToSave} from "../../../model/ClientToSave";
import {ClientDetail} from "../../../model/ClientDetail";
import {PhoneNumberType} from "../../../enums/PhoneNumberType";
import {HttpService} from "../../HttpService";
import {ClientInfo} from "../../../model/ClientInfo";
import {Component, Inject, OnInit} from "@angular/core";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material";
import {AddressType} from "../../../enums/AddressType";

@Component({
  selector: 'client-form-component',
  template: require('./client_form.component.html'),
  styles: [require('./client_form.component.css')],
})
export class ClientFormComponent implements OnInit {

  formData: ClientDetail;

  charms: any[];
  phoneMask: any[] = ['+', '7', ' ', '(', /[0-9]/, /\d/, /\d/, ')', ' ', /\d/, /\d/, /\d/, '-', /\d/, /\d/, /\d/, /\d/];
  newForm: boolean = false;

  requiredPhoneNumbers = {
    "HOME": 1,
    "WORK": 1,
    "MOBILE": 3,
  };

  constructor(public dialogRef: MatDialogRef<ClientFormComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private httpService: HttpService) {
  }

  ngOnInit() {
    if (this.data.item) {
      this.loadEditableData(this.data.item.id);
      this.newForm = true;
    } else {
      this.formData = new ClientDetail;
      this.formData.phoneNumbers = [];
      this.formData.registerAddress = new ClientAddress;
      this.formData.registerAddress.type = AddressType.REG;
      this.formData.actualAddress = new ClientAddress;
      this.formData.actualAddress.type = AddressType.FACT;
      this.fillMissingPhones();
    }
    this.charms = this.data.charms;

  }

  loadEditableData(id: number): ClientInfo {

    this.httpService.get("/client/detail", {
      id: id,
    }).toPromise().then(res => {
      this.formData = JSON.parse(res.text()) as ClientDetail;
      this.formData.birthDate = new Date(this.formData.birthDate);

      if (!this.formData.actualAddress) {
        this.formData.actualAddress = new ClientAddress;
        this.formData.actualAddress.type = AddressType.FACT;
      }
      if (!this.formData.registerAddress) {
        this.formData.registerAddress = new ClientAddress;
        this.formData.registerAddress.type = AddressType.REG;
      }

      if (this.formData.phoneNumbers) {
        this.formData.phoneNumbers.forEach(element => {
          element['oldNumber'] = element.number;
          this.requiredPhoneNumbers[element.type] = this.requiredPhoneNumbers[element.type] - 1;
          element.type = this.getPhoneType(element.type);
        });
      }
      else {
        this.formData.phoneNumbers = [];
      }

      this.fillMissingPhones();
    });

    return null
  }

  fillMissingPhones() {

    for (let i = 0; i < this.requiredPhoneNumbers["HOME"]; i++)
      this.formData.phoneNumbers.push({number: "", type: PhoneNumberType.HOME} as ClientPhone);
    for (let i = 0; i < this.requiredPhoneNumbers["WORK"]; i++)
      this.formData.phoneNumbers.push({number: "", type: PhoneNumberType.WORK} as ClientPhone);
    for (let i = 0; i < this.requiredPhoneNumbers["MOBILE"]; i++)
      this.formData.phoneNumbers.push({number: "", type: PhoneNumberType.MOBILE} as ClientPhone);

    this.formData.phoneNumbers.sort((a, b) => {
      let nameA = a.type;
      let nameB = b.type;
      if (nameA < nameB) {
        return -1;
      }
      if (nameA > nameB) {
        return 1;
      }
      return 0;
    });

  }

  //noinspection JSMethodCanBeStatic
  getPhoneLabel(type: PhoneNumberType) {
    switch (type) {
      case PhoneNumberType.HOME:
        return "Home number";
      case PhoneNumberType.WORK:
        return "Work number";
      case PhoneNumberType.MOBILE:
        return "Mobile number";
    }
    return undefined
  }

  //noinspection JSMethodCanBeStatic
  getPhoneType(type: string) {
    switch (type) {
      case "HOME":
        return PhoneNumberType.HOME;
      case "WORK":
        return PhoneNumberType.WORK;
      case "MOBILE":
        return PhoneNumberType.MOBILE;
    }
    return undefined
  }

  saveButton() {
    this.save();
  }

  canSave() {
    return this.formData.name && this.formData.surname && this.formData.birthDate && this.formData.gender && this.formData.charm && this.formData.patronymic;
  }

  save() {
    let toSave = new ClientToSave(this.formData);

    if (this.formData.actualAddress.street || this.formData.actualAddress.house || this.formData.actualAddress.flat)
      toSave.actualAddress = this.formData.actualAddress;
    if (this.formData.registerAddress.street || this.formData.registerAddress.house || this.formData.registerAddress.flat)
      toSave.registerAddress = this.formData.registerAddress;

    toSave.numbersToDelete = [];
    toSave.numersToSave = [];

    this.formData.phoneNumbers.forEach(element => {

      if (element['oldNumber'] && element.number == "") {
        element.number = element['old'];
        delete element['oldNumber'];
        toSave.numbersToDelete.push(element);
      }
      else if (element['oldNumber'] && element.number != element['oldNumber']) {
        toSave.numersToSave.push(element);
      } else if (!element['oldNumber'] && element.number != "") {
        element['oldNumber'] = null;
        toSave.numersToSave.push(element);
      }

    });

    let url = "/client/addOrUpdate";
    this.httpService.post(url, {
      client: JSON.stringify(toSave)
    }).toPromise().then(res => {
      if (res.text() === "bad")
        alert('something went wrong');
    });

    this.dialogRef.close();
  }

  exit() {
    this.dialogRef.close("cancel");
  }

}