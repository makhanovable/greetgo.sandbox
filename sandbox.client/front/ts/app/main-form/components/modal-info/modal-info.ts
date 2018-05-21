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
  clientInfoModel: ClientInfoModel = null;

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

    if (data.clientInfoModel !== null) {
      this.clientInfoModel = data.clientInfoModel;

      this.name = this.clientInfoModel.clientInfo.name;
      this.surname = this.clientInfoModel.clientInfo.surname;
      this.patronymic = this.clientInfoModel.clientInfo.patronymic;
      this.gender = this.clientInfoModel.clientInfo.gender;
      this.birthDate = new Date(this.clientInfoModel.clientInfo.birthDate);
      this.charmId = this.clientInfoModel.clientInfo.charmId;
      this.charmsDictionary = this.clientInfoModel.charmsDictionary;

      if (this.clientInfoModel.factAddress !== null) {
        this.streetFact = this.clientInfoModel.factAddress.street;
        this.houseFact = this.clientInfoModel.factAddress.house;
        this.flatFact = this.clientInfoModel.factAddress.flat;
      }

      if (this.clientInfoModel.regAddress !== null) {
        this.streetReg = this.clientInfoModel.regAddress.street;
        this.houseReg = this.clientInfoModel.regAddress.house;
        this.flatReg = this.clientInfoModel.regAddress.flat;
      }

      let mobileCounter = 0;
      for (let i = 0; i < this.clientInfoModel.phones.length; i++) {
        const phone = this.clientInfoModel.phones[i];
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

      console.log(data.clientInfoModel)
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
