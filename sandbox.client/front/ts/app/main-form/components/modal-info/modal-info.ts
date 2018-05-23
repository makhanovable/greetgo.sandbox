import {Component, Inject, OnInit} from "@angular/core";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material";
import {MainFormComponent} from "../../main-form";
import {FormArray, FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {Gender} from "../../../../model/Gender";
import {PhoneType} from "../../../../model/PhoneType";
import {Charm} from "../../../../model/Charm";
import {HttpService} from "../../../HttpService";
import {ActionType} from "../../../../utils/ActionType";
import {AccountService} from "../../../services/AccountService";
import {Client} from "../../../../model/Client";
import {Phone} from "../../../../model/Phone";
import {Address} from "../../../../model/Address";
import {AddressType} from "../../../../model/AddressType";
import {ClientInfo} from "../../../../model/ClientInfo";
import {Constants} from "../../../../utils/Constants";

@Component({
  selector: 'modal-info-component',
  template: require('./modal-info.html'),
  styles: [require('./modal-info.css')],
})
export class ModalInfoComponent implements OnInit {

  actionType: ActionType;

  form: FormGroup;

  clientInfo: ClientInfo = new ClientInfo(null, null, null, null);

  name: string = '';
  surname: string = '';
  patronymic: string = '';
  gender: Gender = Gender.MALE;
  birthDate: Date = new Date();
  charmId: number = -1;
  streetFact: string = '';
  houseFact: string = '';
  flatFact: string = '';
  streetReg: string = '';
  houseReg: string = '';
  flatReg: string = '';
  phoneHome: string = '';
  phoneWork: string = '';
  mobiles: FormControl[] = [];

  charmsDictionary: Charm[];

  DUMB_ID = 1;

  errorMessage: string | null = null;

  constructor(private fb: FormBuilder,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private dialogRef: MatDialogRef<MainFormComponent>,
              private httpService: HttpService, private accountService: AccountService) {

    this.requestCharmDictionary();

    this.actionType = data.actionType;

    if (data.clientId !== null) {
      this.requestClientInfo(data.clientId);
    }
  }

  ngOnInit(): void {
    this.initForm(Constants.FORM_INIT);
  }

  private requestCharmDictionary() {
    this.httpService.get("/charm/dictionary").toPromise().then(dictionary => {
      this.onCharmRequestSuccess(dictionary);
    }, error => {
      console.log(error);
    });
  }

  private onCharmRequestSuccess(dictionary) {
    this.charmsDictionary = dictionary.json();
    this.charmId = this.charmsDictionary[0].id;
  }

  private requestClientInfo(clientId: number) {
    this.httpService.get("/client/info", {clientId: clientId}).toPromise().then(clientInfo => {
      this.onClientInfoRequestSuccess(clientInfo);
    }, error => {
      console.log(error);
    });
  }

  private onClientInfoRequestSuccess(clientInfo) {
    this.clientInfo = clientInfo.json();

    if (this.actionType == ActionType.EDIT) this.loadClientInfo();

    this.initForm(Constants.FORM_LOAD);
  }

  initForm(action: number) {
    if (action === Constants.FORM_INIT) {
      this.form = this.fb.group({
        name: new FormControl(this.name, [Validators.required]),
        surname: new FormControl(this.surname, [Validators.required]),
        patronymic: new FormControl(this.patronymic, [Validators.required]),
        gender: new FormControl(this.gender, [Validators.required]),
        birthDate: new FormControl(this.birthDate, [Validators.required]),
        charm: new FormControl('', [Validators.required]),
        streetFact: new FormControl(this.streetFact),
        houseFact: new FormControl(this.houseFact, [Validators.pattern("^[0-9]*$")]),
        flatFact: new FormControl(this.flatFact, [Validators.pattern("^[0-9]*$")]),
        streetReg: new FormControl(this.streetReg, [Validators.required]),
        houseReg: new FormControl(this.houseReg,[Validators.required]),
        flatReg: new FormControl(this.flatReg, [Validators.pattern("^[0-9]*$")]),
        phoneHome: new FormControl(this.phoneHome,
          [Validators.required, Validators.pattern("^[0-9]*$")]),
        phoneWork: new FormControl(this.phoneWork,
          [Validators.required, Validators.pattern("^[0-9]*$")]),
        mobiles: this.fb.array([this.createMobile("")]), // init with an empty control
      });
    } else if (action === Constants.FORM_LOAD) {
      this.form = this.fb.group({
        name: new FormControl(this.name, [Validators.required]),
        surname: new FormControl(this.surname, [Validators.required]),
        patronymic: new FormControl(this.patronymic, [Validators.required]),
        gender: new FormControl(this.gender, [Validators.required]),
        birthDate: new FormControl(this.birthDate, [Validators.required]),
        charm: new FormControl(this.charmId, [Validators.required]),
        streetFact: new FormControl(this.streetFact),
        houseFact: new FormControl(this.houseFact, [Validators.pattern("^[0-9]*$")]),
        flatFact: new FormControl(this.flatFact, [Validators.pattern("^[0-9]*$")]),
        streetReg: new FormControl(this.streetReg, [Validators.required]),
        houseReg: new FormControl(this.houseReg,[Validators.required]),
        flatReg: new FormControl(this.flatReg, [Validators.pattern("^[0-9]*$")]),
        phoneHome: new FormControl(this.phoneHome,
          [Validators.required, Validators.pattern("^[0-9]*$")]),
        phoneWork: new FormControl(this.phoneWork,
          [Validators.required, Validators.pattern("^[0-9]*$")]),
        mobiles: this.form.controls.mobiles,
      });
    }
  }

  private loadClientInfo() {
    this.name = this.clientInfo.client.name;
    this.surname = this.clientInfo.client.surname;
    this.patronymic = this.clientInfo.client.patronymic;
    this.gender = this.clientInfo.client.gender;
    this.birthDate = new Date(this.clientInfo.client.birthDate);
    this.charmId = this.clientInfo.client.charmId;

    if (this.clientInfo.factAddress !== null) {
      this.streetFact = this.clientInfo.factAddress.street;
      this.houseFact = this.clientInfo.factAddress.house;
      this.flatFact = this.clientInfo.factAddress.flat;
    }

    if (this.clientInfo.regAddress !== null) {
      this.streetReg = this.clientInfo.regAddress.street;
      this.houseReg = this.clientInfo.regAddress.house;
      this.flatReg = this.clientInfo.regAddress.flat;
    }

    this.deleteMobile(0); // Need to remove the first empty control
    for (let i = 0; i < this.clientInfo.phones.length; i++) {
      const phone = this.clientInfo.phones[i];

      if (phone.type == PhoneType.HOME) {
        this.phoneHome = phone.number;
      } else if (phone.type == PhoneType.WORK) {
        this.phoneWork = phone.number;
      } else if (phone.type == PhoneType.MOBILE) {
        this.addMobile(phone.number);
      }
    }
  }

  deleteMobile(index: number) {
    const control = this.form.controls["mobiles"] as FormArray;
    control.removeAt(index);
  }

  addMobile(number: string) {
    const control = this.form.controls["mobiles"] as FormArray;
    control.push(this.createMobile(number));
  }

  private createMobile(number: string): FormGroup {
    return this.fb.group({
      number: new FormControl(number,
        [Validators.required,
          Validators.maxLength(10),
          Validators.minLength(10),
          Validators.pattern("^[0-9]*$")])
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

  private createNewClient() {
    const clientInfo = this.boxClientInfo();

    console.log("check", clientInfo);
    this.httpService.post("/client/create",
      {clientInfo: JSON.stringify(clientInfo)}).toPromise().then(response => {
      this.onCreateClientSuccess(response);
    }, error => {
      alert(error._body);
      this.errorMessage = error._body;
    });
  }

  private onCreateClientSuccess(response) {
    this.accountService.addNewAccount(response.json());
    this.dialogRef.close();
  }

  private editClient() {
    const clientInfo = this.boxClientInfo();
    this.httpService.post("/client/edit",
      {clientInfo: JSON.stringify(clientInfo)}).toPromise().then(response => {
      this.onEditClientSuccess(response);
    }, error => {
      alert(error._body);
      this.errorMessage = error._body;
    });
  }

  private onEditClientSuccess(response) {
    console.log(response.json());
    this.accountService.updateAccount(response.json());
    this.dialogRef.close();
  }

  private boxClientInfo(): ClientInfo {
    console.log(this.clientInfo);
    let clientId = this.DUMB_ID;
    let factAddressId = this.DUMB_ID;
    let regAddressId = this.DUMB_ID;
    if (this.actionType == ActionType.EDIT) clientId = this.clientInfo.client.id;
    if (this.actionType == ActionType.EDIT) regAddressId = this.clientInfo.regAddress.id;

    if (this.actionType == ActionType.EDIT
      && this.clientInfo.factAddress !== null) factAddressId = this.clientInfo.factAddress.id;

    const client = new Client(
      clientId,
      this.form.controls["name"].value,
      this.form.controls["surname"].value,
      this.form.controls["patronymic"].value,
      this.form.controls["gender"].value,
      this.form.controls["birthDate"].value.getTime(),
      // -1);
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

    phones.push(new Phone(clientId, this.form.controls["phoneHome"].value, PhoneType.HOME));
    phones.push(new Phone(clientId, this.form.controls["phoneWork"].value, PhoneType.WORK));

    const arr = (this.form.controls["mobiles"] as FormArray).controls;
    for (let i = 0; i < arr.length; i++) {
      phones.push(new Phone(clientId, arr[i].value.number, PhoneType.MOBILE));
    }

    return new ClientInfo(client, factAddress, regAddress, phones);
  }
}
