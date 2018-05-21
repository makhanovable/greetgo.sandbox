import {Component, Inject, OnInit} from "@angular/core";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material";
import {MainFormComponent} from "../../main-form";
import {FormBuilder, FormGroup} from "@angular/forms";
import {Gender} from "../../../../model/Gender";
import {PhoneType} from "../../../../model/PhoneType";
import {Charm} from "../../../../model/Charm";
import {HttpService} from "../../../HttpService";
import {ActionType} from "../../../../model/ActionType";
import {AccountService} from "../../../services/AccountService";

@Component({
  selector: 'modal-info-component',
  template: require('./modal-info.html'),
  styles: [require('./modal-info.css')],
})
export class ModalInfoComponent implements OnInit {

  actionType: ActionType;

  form: FormGroup;

  clientId: number;

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
              private dialogRef: MatDialogRef<MainFormComponent>,
              private httpService: HttpService, private accountService: AccountService) {

    this.actionType = data.actionType;
    this.clientId = data.clientId;

    this.httpService.get("/client/info", {clientId: this.clientId}).toPromise().then(response => {
      this.loadData(response);
    }, error => {
      console.log(error);
    });
  }

  ngOnInit(): void {
    this.loadForm();
  }

  private loadData(response) {
    const clientInfoModel = response.json();

    this.charmsDictionary = clientInfoModel.charmsDictionary;
    this.charmId = this.charmsDictionary[0].id;

    if (this.actionType == ActionType.EDIT) {
      this.loadClientInfo(clientInfoModel);
    }

    this.loadForm();
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

  loadForm() {
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
    switch (this.actionType) {
      case ActionType.CREATE:
        this.createNewClient();
        break;
      case ActionType.EDIT:
        this.editClient();
        break;
    }
  }

  close() {
    this.dialogRef.close();
  }

  createNewClient() {
    this.httpService.post("/client/create", {
      name: this.form.controls["name"].value,
      surname: this.form.controls["surname"].value,
      patronymic: this.form.controls["patronymic"].value,
      gender: this.form.controls["gender"].value,
      birthDate: this.form.controls["birthDate"].value.getTime(),
      charmId: this.form.controls["charm"].value,
      streetFact: this.form.controls["streetFact"].value,
      houseFact: this.form.controls["houseFact"].value,
      flatFact: this.form.controls["flatFact"].value,
      streetReg: this.form.controls["streetReg"].value,
      houseReg: this.form.controls["houseReg"].value,
      flatReg: this.form.controls["flatReg"].value,
      phoneHome: this.form.controls["phoneHome"].value,
      phoneWork: this.form.controls["phoneWork"].value,
      phoneMobile1: this.form.controls["phoneMobile1"].value,
      phoneMobile2: this.form.controls["phoneMobile2"].value,
      phoneMobile3: this.form.controls["phoneMobile3"].value,
    }).toPromise().then(response => {

      this.accountService.addNewAccount(response.json());
      this.dialogRef.close();
    }, error => {
      console.log(error);
    });
  }

  private editClient() {
    console.log(this.form.controls["charm"].value);
    this.httpService.post("/client/edit", {
      clientId: this.clientId,
      name: this.form.controls["name"].value,
      surname: this.form.controls["surname"].value,
      patronymic: this.form.controls["patronymic"].value,
      gender: this.form.controls["gender"].value,
      birthDate: this.form.controls["birthDate"].value.getTime(),
      charmId: this.form.controls["charm"].value,
      streetFact: this.form.controls["streetFact"].value,
      houseFact: this.form.controls["houseFact"].value,
      flatFact: this.form.controls["flatFact"].value,
      streetReg: this.form.controls["streetReg"].value,
      houseReg: this.form.controls["houseReg"].value,
      flatReg: this.form.controls["flatReg"].value,
      phoneHome: this.form.controls["phoneHome"].value,
      phoneWork: this.form.controls["phoneWork"].value,
      phoneMobile1: this.form.controls["phoneMobile1"].value,
      phoneMobile2: this.form.controls["phoneMobile2"].value,
      phoneMobile3: this.form.controls["phoneMobile3"].value,
    }).toPromise().then(response => {

      console.log(response.json());
      this.accountService.updateAccount(response.json());
      this.dialogRef.close();
    }, error => {
      console.log(error);
    });
  }
}
