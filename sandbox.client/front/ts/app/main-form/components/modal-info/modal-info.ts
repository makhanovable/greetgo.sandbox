import {Component, Inject, OnInit} from "@angular/core";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material";
import {MainFormComponent} from "../../main-form";
import {FormArray, FormBuilder, FormGroup} from "@angular/forms";
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

  clientInfo: ClientInfo = new ClientInfo(null, null, null, null);

  name: string = 'test';
  surname: string = 'test';
  patronymic: string = 'test';
  gender: Gender = Gender.MALE;
  birthDate: Date = new Date();
  charmId: number = 1;
  streetFact: string = 'qwe';
  houseFact: string = 'qwe';
  flatFact: string = 'qwe';
  streetReg: string = 'qwe';
  houseReg: string = 'qwe';
  flatReg: string = 'qwe';
  phoneHome: string = 'qwe';
  phoneWork: string = 'qwe';
  mobiles: FormGroup[] = [];

  charmsDictionary: Charm[];

  DUMB_ID = -1;

  constructor(private fb: FormBuilder,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private dialogRef: MatDialogRef<MainFormComponent>,
              private httpService: HttpService, private accountService: AccountService) {

    this.actionType = data.actionType;

    this.httpService.get("/charm/dictionary").toPromise().then(dictionary => {
      this.charmsDictionary = dictionary.json();
      this.charmId = this.charmsDictionary[0].id;
    }, error => {
      console.log(error);
    });

    if (data.clientId !== this.DUMB_ID) {
      this.httpService.get("/client/info", {clientId: data.clientId}).toPromise().then(response => {
        this.loadData(response);
      }, error => {
        console.log(error);
      });
    }
  }

  ngOnInit(): void {
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
      mobiles: this.fb.array([this.createMobile("")])
    });
  }

  private loadData(response) {
    this.clientInfo = response.json();

    if (this.actionType == ActionType.EDIT) {
      this.loadClientInfo(this.clientInfo);
    } else {
      const control = this.form.controls["mobiles"] as FormArray;
      control.push(this.createMobile(""));
    }

    this.loadForm();
  }

  private loadClientInfo(clientInfoModel) {
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


    this.deleteMobile(0);
    for (let i = 0; i < clientInfoModel.phones.length; i++) {
      const phone = clientInfoModel.phones[i];

      if (phone.type == PhoneType.HOME) {
        this.phoneHome = phone.number;
      } else if (phone.type == PhoneType.WORK) {
        this.phoneWork = phone.number;
      } else if (phone.type == PhoneType.MOBILE) {
        const control = this.form.controls["mobiles"] as FormArray;
        control.push(this.createMobile(phone.number));
      }
    }
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
      mobiles: this.form.controls.mobiles
    });
  }

  createMobile(number: string): FormGroup {
    return this.fb.group({
      number: [number]
    });
  }

  addMobile() {
    const control = this.form.controls["mobiles"] as FormArray;
    control.push(this.createMobile(""));
  }

  deleteMobile(index: number) {
    const control = this.form.controls["mobiles"] as FormArray;
    control.removeAt(index);
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

    console.log("check", clientInfo);
    this.httpService.post("/client/create", {clientInfo: JSON.stringify(clientInfo)}).toPromise().then(response => {
      this.accountService.addNewAccount(response.json());
      this.dialogRef.close();
    }, error => {
      console.log(error);
    });
  }

  private editClient() {
    const clientInfo = this.boxClientInfo();
    this.httpService.post("/client/edit", {clientInfo: JSON.stringify(clientInfo)}).toPromise().then(response => {
      console.log(response.json());
      this.accountService.updateAccount(response.json());
      this.dialogRef.close();
    }, error => {
      console.log(error);
    });
  }

  private boxClientInfo(): ClientInfo {
    console.log(this.clientInfo);
    let clientId = -1;
    let factAddressId = -1;
    let regAddressId = -1;
    if (this.clientInfo.client !== null) clientId = this.clientInfo.client.id;
    if (this.clientInfo.factAddress !== null) factAddressId = this.clientInfo.factAddress.id;
    if (this.clientInfo.regAddress !== null) regAddressId = this.clientInfo.regAddress.id;

    const client = new Client(
      clientId,
      this.form.controls["name"].value,
      this.form.controls["surname"].value,
      this.form.controls["patronymic"].value,
      this.form.controls["gender"].value,
      this.form.controls["birthDate"].value.getTime(),
      this.form.controls["charm"].value);

    const factAddress = new Address(
      factAddressId,
      clientId,
      AddressType.FACT,
      this.form.controls["streetFact"].value,
      this.form.controls["houseFact"].value,
      this.form.controls["flatFact"].value);

    const regAddress = new Address(
      regAddressId,
      clientId,
      AddressType.REG,
      this.form.controls["streetReg"].value,
      this.form.controls["houseReg"].value,
      this.form.controls["flatReg"].value);

    const phones: Phone[] = [];

    phones.push(new Phone(this.clientInfo.client.id, this.form.controls["phoneHome"].value, PhoneType.HOME));
    phones.push(new Phone(this.clientInfo.client.id, this.form.controls["phoneWork"].value, PhoneType.WORK));

    const arr = (this.form.controls["mobiles"] as FormArray).controls;
    for (let i = 0; i < arr.length; i++) {
      phones.push(new Phone(this.clientInfo.client.id, arr[i].value.number, PhoneType.MOBILE));
    }

    return new ClientInfo(client, factAddress, regAddress, phones);
  }
}
