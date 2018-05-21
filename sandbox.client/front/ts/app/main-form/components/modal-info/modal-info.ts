import {Component, Inject, OnInit} from "@angular/core";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material";
import {MainFormComponent} from "../../main-form";
import {FormBuilder, FormGroup} from "@angular/forms";
import {ClientInfoModel} from "../../../../model/ClientInfoModel";
import {Gender} from "../../../../model/Gender";
import {PhoneType} from "../../../../model/PhoneType";
import {Charm} from "../../../../model/Charm";

@Component({
  selector: 'modal-info-component',
  template: require('./modal-info.html'),
  styles: [require('./modal-info.css')],
})
export class ModalInfoComponent implements OnInit {

  form: FormGroup;

  name: string = '';
  surname: string = '';
  patronymic: string = '';
  gender: Gender = Gender.MALE;
  birthDate: Date = null;
  charmId: number = 0;
  streetFact: string = '';
  houseFact: string = '';
  flatFact: string = '';
  streetReg: string = '';
  houseReg: string = '';
  flatReg: string = '';
  phoneHome: string = '';
  phoneWork: string = '';
  phoneMobile1: string = '';
  phoneMobile2: string = '';
  phoneMobile3: string = '';

  charmsDictionary: Charm[];

  constructor(private fb: FormBuilder,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private dialogRef: MatDialogRef<MainFormComponent>) {

    this.charmsDictionary = data.clientInfoModel.charmsDictionary;
    this.charmId = this.charmsDictionary[0].id;

    if (data.clientInfoModel.clientInfo !== null) {
      this.loadClientInfo(data.clientInfoModel);
      console.log(data.clientInfoModel)
    }
  }

  private loadClientInfo(clientInfoModel) {
    this.name = clientInfoModel.clientInfo.name;
    this.surname = clientInfoModel.clientInfo.surname;
    this.patronymic = clientInfoModel.clientInfo.patronymic;
    this.gender = clientInfoModel.clientInfo.gender;
    this.birthDate = new Date(clientInfoModel.clientInfo.birthDate);
    this.charmId = clientInfoModel.clientInfo.charmId;

    if (clientInfoModel.factAddress !== null) {
      this.streetFact = clientInfoModel.factAddress.street;
      this.houseFact = clientInfoModel.factAddress.house;
      this.flatFact = clientInfoModel.factAddress.flat;
    }

    if (clientInfoModel.regAddress !== null) {
      this.streetReg = clientInfoModel.regAddress.street;
      this.houseReg = clientInfoModel.regAddress.house;
      this.flatReg = clientInfoModel.regAddress.flat;
    }

    let mobileCounter = 0;
    for (let i = 0; i < clientInfoModel.phones.length; i++) {
      const phone = clientInfoModel.phones[i];
      if (phone.type == PhoneType.HOME) {
        this.phoneHome = phone.number;
      } else if (phone.type == PhoneType.WORK) {
        this.phoneWork = phone.number;
      } else if (phone.type == PhoneType.MOBILE) {
        switch (mobileCounter++) {
          case 0:
            this.phoneMobile1 = phone.number;
            break;
          case 1:
            this.phoneMobile2 = phone.number;
            break;
          case 3:
            this.phoneMobile3 = phone.number;
            break;
        }
      }
    }
  }

  ngOnInit(): void {
    this.form = this.fb.group({
      name: [this.name],
      surname: [this.surname],
      patronymic: [this.patronymic],
      gender: ['MALE'],
      birthDate: [this.birthDate],
      charm: [this.charmId],
      streetFact: [this.streetFact],
      houseFact: [this.houseFact],
      flatFact: [this.flatFact],
      streetReg: [this.streetReg],
      houseReg: [this.houseReg],
      flatReg: [this.flatReg],
      phoneHome: [this.phoneHome],
      phoneWork: [this.phoneWork],
      phoneMobile1: [this.phoneMobile1],
      phoneMobile2: [this.phoneMobile2],
      phoneMobile3: [this.phoneMobile3],
    });
  }


  save() {
    this.dialogRef.close(this.form.value);
  }

  close() {
    this.dialogRef.close();
  }
}
