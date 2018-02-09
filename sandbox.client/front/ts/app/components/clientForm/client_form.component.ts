import {Http} from "@angular/http";
import {PhoneNumberType} from "../../../enums/PhoneNumberType";
import {ClientAddress} from "../../../model/ClientAddress";
import {HttpService} from "../../HttpService";
import {ClientPhone} from "../../../model/ClientPhone";
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

  formData: ClientInfo;
  charms: any[];
  phoneMask: any[] = ['+', '7', ' ', '(', /[1-9]/, /\d/, /\d/, ')', ' ', /\d/, /\d/, /\d/, '-', /\d/, /\d/, /\d/, /\d/];
  newForm: boolean = false;

  requiredPhoneNumbers = {
    "HOME": 1,
    "WORK": 1,
    "MOBILE": 3,
  };

  requiredFields = new ClientInfo;

  // requiredFields = {
  //   name: true,
  //   surname: true,
  //   patronymic: true,
  //   charmId: true,
  //   birthDate: true
  // }

  constructor(public dialogRef: MatDialogRef<ClientFormComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private httpService: HttpService, private http: Http) {
  }

  ngOnInit() {
    if (this.data.item) {
      this.loadEditableData(this.data.item.id);
      this.newForm = true;
    } else {
      this.formData = new ClientInfo;
      this.formData.phoneNumbers = [];
      this.formData.registerAddress = new ClientAddress;
      this.formData.registerAddress.type = AddressType.REG;
      this.formData.actualAddress = new ClientAddress;
      this.formData.actualAddress.type = AddressType.FACT;
      this.fillMissingPhones();
    }
    this.charms = this.data.charms

  }

  loadEditableData(id: number): ClientInfo {

    this.httpService.get("/client/info", {
      id: id,
    }).toPromise().then(res => {
      let result = JSON.parse(res.text()) as ClientInfo;
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
    if (this.canSave())
      this.save(this.formData);
  }

  canSave() {
    return this.formData.name && this.formData.surname && this.formData.birthDate && this.formData.gender && this.formData.charmId && this.formData.patronymic;
  }

  save(toSave: ClientInfo) {

    let url = this.newForm ? "/client/update" : "/client/add";
    let finalObject = Object.assign({}, toSave);

    if (!(toSave.actualAddress.street || toSave.actualAddress.house || toSave.actualAddress.flat))
      delete finalObject.actualAddress;
    if (!(toSave.registerAddress.street || toSave.registerAddress.house || toSave.registerAddress.flat))
      delete finalObject.registerAddress;

    finalObject.phoneNumbers = [];
    toSave.phoneNumbers.forEach(element => {
      if (element.number != null && element.number != "")
        finalObject.phoneNumbers.push(element)
    });

    this.httpService.post(url, {
      client: JSON.stringify(finalObject)
    }).toPromise().then(res => {
      if (res.text() === "bad")
        alert('something went wrong');
    });

    this.dialogRef.close();
  }

  exit() {
    this.dialogRef.close();
  }

}