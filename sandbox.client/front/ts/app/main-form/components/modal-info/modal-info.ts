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
import {Client} from "../../../../model/Client";
import {Phone} from "../../../../model/Phone";
import {Address} from "../../../../model/Address";
import {AddressType} from "../../../../model/AddressType";
import {ClientInfo} from "../../../../model/ClientInfo";

@Component({
  selector: 'modal-info-component',
  template: require('./modal-info.html'),
  styles: [require('./modal-info.css')],
})
export class ModalInfoComponent implements OnInit {

  actionType: ActionType;

  form: FormGroup;

  clientInfo: ClientInfo;

  name: string = '';
  surname: string = '';
  patronymic: string = '';
  gender: Gender = Gender.MALE;
  birthDate: Date = null;
  charmId: number = -1;
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

    this.httpService.get("/charm/dictionary").toPromise().then(dictionary => {
      this.charmsDictionary = dictionary.json();
      this.charmId = this.charmsDictionary[0].id;
      console.log(this.charmsDictionary);
    }, error => {
      console.log(error);
    });

    this.httpService.get("/client/info", {clientId: data.clientId}).toPromise().then(response => {
      this.loadData(response);
    }, error => {
      console.log(error);
    });
  }

  ngOnInit(): void {
    this.loadForm();
  }

  private loadData(response) {
    this.clientInfo = response.json();

    if (this.actionType == ActionType.EDIT) {
      this.loadClientInfo(this.clientInfo);
    }

    this.loadForm();
  }

  private loadClientInfo(clientInfoModel) {
    console.log(clientInfoModel);

    this.name = clientInfoModel.client.name;
    this.surname = clientInfoModel.client.surname;
    this.patronymic = clientInfoModel.client.patronymic;
    this.gender = clientInfoModel.client.gender;
    this.birthDate = new Date(clientInfoModel.client.birthDate);
    this.charmId = clientInfoModel.client.charmId;

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
      // console.log(phone);
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
          case 2:
            this.phoneMobile3 = phone.number;
            break;
        }
      }
    }
    // console.log(this.phoneHome);
    // console.log(this.phoneWork);
    // console.log(this.phoneMobile1);
    // console.log(this.phoneMobile2);
    // console.log(this.phoneMobile3);

  }

  loadForm() {
    this.form = this.fb.group({
      name: [this.name],
      surname: [this.surname],
      patronymic: [this.patronymic],
      gender: [this.gender],
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
    const clientInfo = this.boxClientInfo();
    this.httpService.post("/client/create", {clientInfo: JSON.stringify(clientInfo)} ).toPromise().then(response => {
      this.accountService.addNewAccount(response.json());
      this.dialogRef.close();
    }, error => {
      console.log(error);
    });
  }

  private editClient() {
    const clientInfo = this.boxClientInfo();
    this.httpService.post("/client/edit", {clientInfo: JSON.stringify(clientInfo)} ).toPromise().then(response => {
      console.log(response.json());
      this.accountService.updateAccount(response.json());
      this.dialogRef.close();
    }, error => {
      console.log(error);
    });
  }

  private boxClientInfo(): ClientInfo {
    const client = new Client(
      this.clientInfo.client.id || null,
      this.form.controls["name"].value,
      this.form.controls["surname"].value,
      this.form.controls["patronymic"].value,
      this.form.controls["gender"].value,
      this.form.controls["birthDate"].value.getTime(),
      this.form.controls["charm"].value);

    const factAddress = new Address(
      this.clientInfo.factAddress.id || null,
      this.clientInfo.client.id || null,
      AddressType.FACT,
      this.form.controls["streetFact"].value,
      this.form.controls["houseFact"].value,
      this.form.controls["flatFact"].value);

    const regAddress = new Address(
      this.clientInfo.regAddress.id || null,
      this.clientInfo.client.id || null,
      AddressType.REG,
      this.form.controls["streetReg"].value,
      this.form.controls["houseReg"].value,
      this.form.controls["flatReg"].value);

    const phones:Phone[] = [];

    return new ClientInfo(client, factAddress, regAddress, phones);
  }
}
