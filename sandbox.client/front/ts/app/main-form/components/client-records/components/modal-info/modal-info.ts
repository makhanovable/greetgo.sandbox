import {Component, Inject, OnInit} from "@angular/core";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material";
import {MainFormComponent} from "../../../../main-form";
import {FormArray, FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {Gender} from "../../../../../../model/Gender";
import {PhoneType} from "../../../../../../model/PhoneType";
import {Charm} from "../../../../../../model/Charm";
import {HttpService} from "../../../../../HttpService";
import {ActionType} from "../../../../../../utils/ActionType";
import {AccountService} from "../../../../../services/AccountService";
import {Phone} from "../../../../../../model/Phone";
import {Address} from "../../../../../../model/Address";
import {AddressType} from "../../../../../../model/AddressType";
import {ClientToSave} from "../../../../../../model/ClientToSave";
import {Constants} from "../../../../../../utils/Constants";
import {ClientDetails} from "../../../../../../model/ClientDetails";

@Component({
  selector: 'modal-info-component',
  template: require('./modal-info.html'),
  styles: [require('./modal-info.css')],
})
export class ModalInfoComponent implements OnInit {

  actionType: ActionType;

  form: FormGroup;

  clientDetails: ClientDetails = null;

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

    this.actionType = data.actionType;
    this.requestClientInfo(data.clientId);
  }

  ngOnInit(): void {
    this.initForm(Constants.FORM_INIT);
  }

  private requestClientInfo(clientId: number) {
    this.httpService.get("/client/info", {clientId: clientId}).toPromise().then(clientInfo => {
      this.onClientInfoRequestSuccess(clientInfo);
    }, error => {
      console.log(error);
    });
  }

  private onClientInfoRequestSuccess(response) {
    this.clientDetails = response.json();
    this.charmsDictionary = this.clientDetails.charmsDictionary;

    console.log(response.json());

    if (this.actionType == ActionType.EDIT) this.loadClientDetails();

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

  private loadClientDetails() {
    this.name = this.clientDetails.name;
    this.surname = this.clientDetails.surname;
    this.patronymic = this.clientDetails.patronymic;
    this.gender = this.clientDetails.gender;
    this.birthDate = new Date(this.clientDetails.birthDate);
    this.charmId = this.clientDetails.charmId;

    if (this.clientDetails.factAddress !== null) {
      this.streetFact = this.clientDetails.factAddress.street;
      this.houseFact = this.clientDetails.factAddress.house;
      this.flatFact = this.clientDetails.factAddress.flat;
    }

    if (this.clientDetails.regAddress !== null) {
      this.streetReg = this.clientDetails.regAddress.street;
      this.houseReg = this.clientDetails.regAddress.house;
      this.flatReg = this.clientDetails.regAddress.flat;
    }

    this.deleteMobile(0); // Need to remove the first empty control
    for (let i = 0; i < this.clientDetails.phones.length; i++) {
      const phone = this.clientDetails.phones[i];

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
    const clientInfo = this.boxClientToSave();

    this.httpService.post("/client/create",
      {clientToSave: JSON.stringify(clientInfo)}).toPromise().then(response => {
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
    const clientInfo = this.boxClientToSave();
    this.httpService.post("/client/edit",
      {clientToSave: JSON.stringify(clientInfo)}).toPromise().then(response => {
      this.onEditClientSuccess(response);
    }, error => {
      alert(error._body);
      this.errorMessage = error._body;
    });
  }

  private onEditClientSuccess(response) {
    console.log("edited", response.json());
    this.accountService.updateAccount(response.json());
    this.dialogRef.close();
  }

  private boxClientToSave(): ClientToSave {
    let clientId = this.DUMB_ID;

    if (this.actionType == ActionType.EDIT) clientId = this.clientDetails.id;

    const clientInfo = new ClientToSave();

    this.boxPersonalDetail(clientInfo, clientId);
    this.boxAddresses(clientInfo);
    this.boxPhones(clientInfo);

    console.log(clientInfo);

    return clientInfo;
  }

  private boxPersonalDetail(clientInfo: ClientToSave, clientId: number) {
    clientInfo.id = clientId;
    clientInfo.name = this.form.controls["name"].value;
    clientInfo.surname = this.form.controls["surname"].value;
    clientInfo.patronymic = this.form.controls["patronymic"].value;
    clientInfo.gender = this.form.controls["gender"].value;
    clientInfo.birthDate = this.form.controls["birthDate"].value.getTime();
    clientInfo.charmId = this.form.controls["charm"].value;
  }

  private boxAddresses(clientInfo: ClientToSave) {
    const factAddress = new Address();
    factAddress.type = AddressType.FACT;
    factAddress.street = this.form.controls["streetFact"].value;
    factAddress.house = this.form.controls["houseFact"].value;
    factAddress.flat = this.form.controls["flatFact"].value;

    const regAddress = new Address();
    regAddress .type = AddressType.REG;
    regAddress.street = this.form.controls["streetReg"].value;
    regAddress.house = this.form.controls["houseReg"].value;
    regAddress.flat = this.form.controls["flatReg"].value;

    // if(!(factAddress.street === "" && factAddress.house === "" && factAddress.flat === ""))
      clientInfo.factAddress = factAddress;

    // if(!(regAddress.street === "" && regAddress.house === "" && regAddress.flat === ""))
      clientInfo.regAddress = regAddress;
  }

  private boxPhones(clientInfo: ClientToSave) {
    clientInfo.phones = [];

    const phoneHome = new Phone();
    phoneHome.number = this.form.controls["phoneHome"].value;
    phoneHome.type = PhoneType.HOME;

    const phoneWork = new Phone();
    phoneWork.number = this.form.controls["phoneWork"].value;
    phoneWork.type = PhoneType.WORK;

    clientInfo.phones.push(phoneHome);
    clientInfo.phones.push(phoneWork);

    const arr = (this.form.controls["mobiles"] as FormArray).controls;
    for (let i = 0; i < arr.length; i++) {
      const mobile = new Phone();
      mobile.type = PhoneType.MOBILE;
      mobile.number = arr[i].value.number;

      clientInfo.phones.push(mobile);
    }
  }
}
